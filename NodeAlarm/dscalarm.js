﻿#!/usr/bin/env node
/*
 DSC Alarm Bridge
 This NodeJS code will work like a bridge/proxy between Smartthings and DSC IT-100 Serial board
 This code will monitor the DSC IT-100 Serial board and will send and received commands to SmartThings ServiceManager (Also known as SmartApp)

 To install the dependencies just open the npm and type "npm install" inside of the project folder

 SmartTing Reference:
 Building LAN-connected Device Types
 http://docs.smartthings.com/en/latest/cloud-and-lan-connected-device-types-developers-guide/building-lan-connected-device-types/index.html

 The SmartApp
 A Web Services SmartApp
 http://docs.smartthings.com/en/latest/smartapp-web-services-developers-guide/smartapp.html?highlight=mappings

 Cloud-and LAN-connected Devices -> Building LAN-connected Device Types -> Building the Device Type
 http://docs.smartthings.com/en/latest/cloud-and-lan-connected-device-types-developers-guide/building-lan-connected-device-types/building-the-device-type.html

 Service Manager Design Pattern
 http://docs.smartthings.com/en/latest/cloud-and-lan-connected-device-types-developers-guide/understanding-the-service-manage-device-handler-design-pattern.html#basic-overview

 Division of Labor - Architecture
 http://docs.smartthings.com/en/latest/cloud-and-lan-connected-device-types-developers-guide/building-lan-connected-device-types/division-of-labor.html

 Code Reference:
 https://github.com/entrocode/SmartDSC
 https://github.com/isriam/smartthings-alarmserver
 https://github.com/kholloway/smartthings-dsc-alarm
 https://github.com/kholloway/smartthings-dsc-alarm/blob/master/RESTAPISetup.md
 https://github.com/yracine/DSC-Integration-with-Arduino-Mega-Shield-RS-232
 https://github.com/redloro/smartthings
 
*/

////////////////////////////////////////
// Starting Variables and Dependencies
////////////////////////////////////////
console.log("Starting DSCAlarm");

// Loading Dependencies
var http = require("http");
var https = require('https');
var express = require("express");
var app = express();
var SerialPort = require('serialport');
const SerialReadline = require('@serialport/parser-readline')
const Delimiter = require('@serialport/parser-delimiter')
const Ready = require('@serialport/parser-ready')
var nconf = require('nconf');
nconf.file({ file: './config.json' });
//var config = require('./config.js');
fs = require('fs');

////////////////////////////////////////
// Logger Function
////////////////////////////////////////
var logger = function(mod,str) {
    console.log("[%s] [%s] %s", new Date().toISOString(), mod, str);
}

logger("Modules","Modules loaded");

// Setting Variables
var portStatus = 0
var winCom = nconf.get('dscalarm:wincom');
var linuxCom = nconf.get('dscalarm:linuxcom');;
var alarmPassword = nconf.get('dscalarm:alarmpassword');
var baudRate = nconf.get('dscalarm:baudRate');

var httpport = nconf.get('httpport');

// Detecting the OS Version to setup the right com port useful for debugging
logger("OSVersion","Detected OS Version: " + process.platform);
if (process.platform == "win32") {
    var sport = winCom;
}
else {
    var sport = linuxCom;
}

//////////////////////////////////////////////////////////////////
// Creating Endpoints
// Those Endpoints will receive a HTTP GET Request
// Execute the associated Method to make the following:
//  "/" - Used to check if the alarm is running
//  "/api/alarmArmAway" - Used to arm the alarm in away mode
//////////////////////////////////////////////////////////////////

// Used only to check if NodeJS is running
app.get("/", function (req, res) {
    res.send("<html><body><h1>DSC Alarm Running</h1></body></html>");
});

// Used to arm the alarm using the alarm password
app.get("/api/alarmArm", function (req, res) {
    alarmArm();
    //res.send("200 OK");
    res.end();
});

// Used to arm the alarm in Away Mode (password not required)
app.get("/api/alarmArmAway", function (req, res) {
    alarmArmAway();
    //res.send("200 OK");
    res.end();
});

// Used to arm the alarm in Stay Mode (password not required)
app.get("/api/alarmArmStay", function (req, res) {
    alarmArmStay();
    //res.send("200 OK");
    res.end();
});

/////////////////////////////////////////////////////////
// Used to arm the alarm in Stay Mode but Night
app.get("/api/alarmArmNight", function (req, res) {
    alarmArmNight();
    //res.send("200 OK");
    res.end();
});

// Used to enable descriptive control 
app.get("/api/descriptiveControl", function (req, res) {
    descriptiveControl();
    res.end();
});

/////////////////////////////////////////////////////////

// Used to disarm the alarm (need a password)
app.get("/api/alarmDisarm", function (req, res) {
    alarmDisarm();
    res.end();
});

// Used to enable or disable Chime
app.get("/api/alarmChimeToggle", function (req, res) {
    alarmChimeToggle();
    res.end();
});

// Used to activate Panic Siren
app.get("/api/alarmPanic", function (req, res) {
    alarmPanic();
    res.end();
});

// Used to activate Fire Siren
app.get("/api/alarmFire", function (req, res) {
    alarmFire();
    res.end();
});

// Used to activate Ambulance
app.get("/api/alarmAmbulance", function (req, res) {
    alarmAmbulance();
    res.end();
});


// Used to Set Alarm Date and Time
app.get("/api/alarmSetDate", function (req, res) {
    alarmSetDate();
    res.end();
});

// Used to Set Alarm Date and Time
app.get("/api/alarmUpdate", function (req, res) {
    alarmUpdate();
    res.end();
});

/**
 * Subscribe route used by SmartThings Hub to register for callback/notifications and write to config.json
 * @param {String} host - The SmartThings Hub IP address and port number
 */
app.get('/subscribe/:host', function (req, res) {
    var parts = req.params.host.split(":");
    nconf.set('notify:address', parts[0]);
    nconf.set('notify:port', parts[1]);
    nconf.save(function (err) {
      if (err) {
        logger("Subscribe",'Configuration error: '+err.message);
        res.status(500).json({ error: 'Configuration error: '+err.message });
        return;
      }
    });
    res.end();
    logger("Subscribe","Hubitat IpAddress: "+parts[0] +" Port: "+ parts[1]);
});

// Used to save the DSCAlarm password comming from SmartThings App
app.get('/config/:host', function (req, res) {
    //var parts = req.params.host.split(":");
    var parts = req.params.host;
    if(parts != "null"){
        nconf.set('dscalarm:alarmpassword', parts);
        nconf.save(function (err) {
            if (err) {
                logger("SaveConfig",'Configuration error: '+err.message);
                res.status(500).json({ error: 'Configuration error: '+err.message });
                return;
            }
        });
        logger("SaveConfig","DSCAlarm Panel Code Saved: "+parts);
        alarmPassword = nconf.get('dscalarm:alarmpassword');
        logger("SaveConfig","DSCAlarm Panel Reloading Config File: "+alarmPassword);
        
    }
    else{
        logger("SaveConfig","Failed to save DSCAlarm Panel Code password cannot be null");
    }
    res.end();
    
});

/**
 * discover
 */
// Used to send all zones back to SmartThings
app.get("/discover", function (req, res) {
    alarmDiscover();
    res.end();
}); 

logger("HTTP Endpoint","All HTTP endpoints loaded");

////////////////////////////////////////
// Creating Server
////////////////////////////////////////
var server = http.createServer(app);
server.listen(httpport);
logger("HTTP Endpoint","HTTP Server Created at port: "+httpport);

////////////////////////////////////////
// Creating Serial (RS232) Connection
////////////////////////////////////////

// Instanciating the object myPort 
// Using a baudRate (port speed)
// Setting a parser. Based on IT-100 Board communication pattern everytime
//  a command is sent the board will send a \r\n at the end.
// var myPort = new SerialPort(sport, {
//     baudRate: baudRate,
//     parser: SerialPort.parsers.readline("\r\n")
// });
var myPort = new SerialPort(sport, {
    baudRate: baudRate
});


logger("SerialPort","Creating Serial Port: "+sport);

// Example how to List all available Serial ports:
//SerialPort.list(function (err, ports) {
// ports.forEach(function(port) {
//    console.log(port.comName);
//  });
//});

// The object myPort have 4 functions open, data, close, error
// Defining witch method will be called everytime one of those handlers is called
myPort.on('open', showPortOpen);
//myPort.on('data', receivedFromSerial);
myPort.on('close', showPortClose);
myPort.on('error', showError);
const SerialPortparser = myPort.pipe(new SerialReadline({ delimiter: '\r\n' }))
SerialPortparser.on('data', receivedFromSerial)

// Method used to Open the serial communication with DSC IT-100 Board
function showPortOpen() {
    //logger("SerialPort",'Serial Port opened: ' + sport + ' BaudRate: ' + myPort.options.baudRate);
    portStatus = 1;
}

// Method used to Receive serial communication from DSC IT-100 Board
function receivedFromSerial(data) {
    parseReceivedData(data);
}

// Method used to close the Serial Port
function showPortClose() {
    logger("SerialPort",'Serial Port closed: ' + sport);
}

// Method used to list any serial communication error
function showError(error) {
    logger("SerialPort",'Serial port error: ' + error);
}

// Method used to send serial data
function sendToSerial(data) {
    logger("SerialPort","Sending to serial port: " + data);
    myPort.write(data);
}


//////////////////////////////////////////////////////////////////////////////////////
// Alarm DSC Serial Communication Fucntions
// List of function used to send the action to Alarm Board
//////////////////////////////////////////////////////////////////////////////////////

// Send the Arm command to Alarm
function alarmArm() {
    var cmd = "0331" + alarmPassword + "00";
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
}

// Send the ArmAway command to Alarm
function alarmArmAway() {
    var cmd = "0321";
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
}

// Send the ArmStay command to Alarm
function alarmArmStay() {
    var cmd = "0311";
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
}

//////////////////////////////////////
// Send the ArmNight command to Alarm
function alarmArmNight() {
    var cmd = "0311";
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
}
/////////////////////////////////////




// Send the Disarm command to Alarm
function alarmDisarm() {
    var cmd = "0401" + alarmPassword + "00";
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);

}

// Send the Break command to Alarm
function alarmSendBreak() {
    var cmd = "070^";
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
}

// Send the Enable Chime command to Alarm
function alarmChimeToggle() {
    var cmd = "070c";
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
    // wait for 1800 and call alarmSendBreak
    setTimeout(alarmSendBreak, 1800);
}

// Send the Activate Panic Siren
function alarmPanic() {
    var cmd = "0603";
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
}

// Send the Activate Ambulance
function alarmAmbulance() {
    var cmd = "0602";
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
}

// Send the Activate Fire Siren
function alarmFire() {
    var cmd = "0601";
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
}

// Send the descriptiveControl to partition to enable verbose mode.
function descriptiveControl() {
    var cmd = "0501";
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
}




// This command will send the code to the alarm when ever the alarm ask for it with a 900
function alarmSendCode() {
    var cmd = "2001" + alarmPassword + "00";
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
}

// alarm Status Request
function alarmUpdate() {
    var cmd = "001";
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
}

// alarm Set Date Time
function alarmSetDate() {
    
    var date = new Date();
    var hour = date.getHours().toString();
    if(hour.length == 1){
        hour = "0"+hour
    }
    var minute = date.getMinutes().toString();
    if(minute.length == 1){
        minute = "0"+minute
    }
    var month = date.getMonth()+1;
    var monthstr = month.toString();
    if(monthstr.length == 1){
        monthstr = "0"+monthstr
    }
    var day = date.getDate().toString();
    if(day.length == 1){
        day = "0"+day
    }
    var year = date.getFullYear().toString().substring(2,4);
    var timedate = hour+minute+monthstr+day+year;
    logger("AlarmSetDate","SetDate: "+timedate);
    var cmd = "010" + timedate;
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
}

// Send all Zones from config.json back to SmartThings
// SmartThings will create one child device based on this settings
function alarmDiscover(){
    if (nconf.get('dscalarm:panelConfig')) {
        notify(JSON.stringify(nconf.get('dscalarm:panelConfig')));
        logger("AlarmDiscover","Seding zones back: " + JSON.stringify(nconf.get('dscalarm:panelConfig')));
    } else {
        logger("AlarmDiscover","PanelConfig not set.");
    }
    return;
}

////////////////////////////////////////////////////////////////////////////////////////////////
// *** Not in use - function to set the alarm boud rate - *** Not in use
// In cas you want to change the Alarm Board Serial Speed
////////////////////////////////////////////////////////////////////////////////////////////////
function alarmSetBaudRate(speed) {
    // setup and send baud rate command
    // 0=9600, 1=19200, 2=38400, 3=57600, 4=115200

    var cmd = "080";
    if (speed == "9600") {
        cmd = cmd + "0";
    }
    else if (speed == "19200") {
        cmd = cmd + "1";
    }
    else if (speed == "38400") {
        cmd = cmd + "2";
    }
    else if (speed == "57600") {
        cmd = cmd + "3";
    }
    else if (speed == "115200") {
        cmd = cmd + "4";
    }
    else  // By default set to 9600 
    {
        cmd = cmd + "0";
    }  
    cmd = appendChecksum(cmd);
    sendToSerial(cmd);
}

////////////////////////////////////////////////////////////////////////////////////////////////
// Method used to append the right checksum at the end of any command sent to DSC IT-100 Board
// According with DSC IT-100 manual each command sent to the board must have a checksum
// This method will calculate the checksum according to the command that need to be sent
// Will return the data ready to be sent to DSC IT-100 Board 
// Alarm Documentation - http://cms.dsc.com/download.php?t=1&id=16238
////////////////////////////////////////////////////////////////////////////////////////////////
function appendChecksum(data) {
    var result = 0;
    var arrData = data.split('');
    arrData.forEach(function (entry) {
        var entryBuffer = new Buffer(entry, 'ascii');
        var entryRepHex = entryBuffer.toString('hex');
        var entryHex = parseInt(entryRepHex, 16);
        result = result + parseInt(entryHex, 10);
    });
    data = data + (parseInt(result, 10).toString(16).toUpperCase().slice(-2) + "\r\n");
    return data;
}


///////////////////////////////////////////
// Function used to parser all received commands from the Alarm
// We will analise the received data and send the request to SmartThing to control the app
// Based on what we have received we will change the Device Alarm and Zone (Open/Close Sensor) on SmartThing
// Alarm Documentation - http://cms.dsc.com/download.php?t=1&id=16238
//500 previous command received
//501 command error
//502 system error
//601 zone alarm
//602 zone restore
//603 zone tamper
//604 zone tamper restore
//605 zone fault
//606 zone fault restore
//609 zone open
//610 zone close
//650 partition ready
//651 partition not ready
//652 Partition Armed, but also is a descriptive arming (i.e AWAY, STAY, ZERO-ENTRY-AWAY or ZERO-ENTRY-STAY)
//653 Partition is ready, but to force alarm
//654 Partition is in alarm
//655 patition disarmed
//656 partition arming/exit delay
//657 partition is entry delay	                
//658 keyboard lockout, too many attempts
//660 command in progress
//670 invalid code
//671 funct not avail
//672 fail to arm
//673 partition is busy
//700 user closing, partition has been armed by user at the end of exit delay
//701 special closing indicates partition was armed quick arm, auto arm, keyswitch, DLS software
//702 partial closing
//750 disarm by a user
//751 special opening, indicates partition was disarmed quick arm, auto arm, keyswitch, DLS software
//800 low batt
//801 restored low batt
//802 lost power
//803 power restored
//806 siren cables had been tampered
//807 siren cable issue restored
//816 buffer full
//829 system tamper
//830 system tamper restore
//900 code required
//
////////////////////////////////
//codes suffix
//AR-3 system is armed
//AR-2 Arming/exit delay
//
///////////////////////////////////////////
function parseReceivedData(data) {
    logger("SerialPort","Received Serial data: " + data);
    var cmdfullstr = data.toString('ascii');
    if (cmdfullstr.length >= 3){
        var cmd = cmdfullstr.substr(0, 3);
        if (cmd == "609") {
            var msg = ("ZN-" + cmdfullstr.substr(0, 6));
            sendSmartThingMsg(msg);
        }
        else if (cmd == "610") {
            var msg = ("ZN-" + cmdfullstr.substr(0, 6));
            sendSmartThingMsg(msg);
        }
        else if (cmd == "621") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "622") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "650") {
            var msg = ("RD-1");
            sendSmartThingMsg(msg);
        }
        else if (cmd == "651") {
            var msg = ("RD-0");
            sendSmartThingMsg(msg);
        }
        else if (cmd == "656") {
            var msg = ("AR-2");
            sendSmartThingMsg(msg);
        }
        else if ((cmd == "652") || (cmd == "700")) {
            var msg = ("AR-3");
            sendSmartThingMsg(msg);
        }
        else if ((cmd == "655") || (cmd == "750")) {
            var msg = ("AR-0");
            sendSmartThingMsg(msg);
            var msg = ("AL-0");
            sendSmartThingMsg(msg);
        }
        else if (cmd == "654") {
            var msg = ("AL-1");
            sendSmartThingMsg(msg);

        }
        else if (cmd == "900") {
            alarmSendCode();
        }
        else if (cmd == "901") {
            if (cmdfullstr.indexOf("Door Chime") >= 0) {
                if (cmdfullstr.indexOf("ON") >= 0) {
                    var msg = ("CH-1");
                    sendSmartThingMsg(msg);
                }
                else {
                    var msg = ("CH-0");
                    sendSmartThingMsg(msg);
                }
            }
        }
        else if (cmd == "658") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "659") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }        
        else if (cmd == "670") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "672") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "673") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }        
        else if (cmd == "802") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "803") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "806") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "807") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "810") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "811") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "812") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "813") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "821") {
            var msg = ("SY-" + cmdfullstr.substr(0, 6));
            sendSmartThingMsg(msg);
        }
        else if (cmd == "822") {
            var msg = ("SY-" + cmdfullstr.substr(0, 6));
            sendSmartThingMsg(msg);
        }
        else if (cmd == "829") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "830") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "840") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "841") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "842") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "843") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "896") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "897") {
            var msg = ("SY-" + cmd);
            sendSmartThingMsg(msg);
        }
        else if (cmd == "500") {
            if ((cmdfullstr.substr(0, 8)) == "5000332B") {
                var msg = ("AR-112");
                sendSmartThingMsg(msg);
            }
            else if ((cmdfullstr.substr(0, 8)) == "50003129") {
                var msg = ("AR-114");
                sendSmartThingMsg(msg);
            }
            else if ((cmdfullstr.substr(0, 8)) == "5000322A") {
                var msg = ("AR-110");
                sendSmartThingMsg(msg);
            }
            else if ((cmdfullstr.substr(0, 8)) == "50004029") {
              var msg = ("AR-0");
              sendSmartThingMsg(msg);
            }
        }        
        else {

        }
    }
}

///////////////////////////////////////////
// Function to send alarm msgs to Hubitat
///////////////////////////////////////////
function sendSmartThingMsg(command) {
    var msg = JSON.stringify({type: 'zone', command: command});
    notify(msg);
    logger("MsgSentHubitat","Sending Hubitat command: " + msg);
}

///////////////////////////////////////////
// Send HTTP callback to Hubitat HUB
///////////////////////////////////////////
/**
 * Callback to the Hubitat Hub via HTTP NOTIFY
 * @param {String} data - The HTTP message body
 */
var notify = function(data) {
    if (!nconf.get('notify:address') || nconf.get('notify:address').length == 0 ||
      !nconf.get('notify:port') || nconf.get('notify:port') == 0) {
      logger("Notify","Notify server address and port not set!");
      return;
    }
  
    var opts = {
      method: 'NOTIFY',
      host: nconf.get('notify:address'),
      port: nconf.get('notify:port'),
      path: '/notify',
      headers: {
        'CONTENT-TYPE': 'application/json',
        'CONTENT-LENGTH': Buffer.byteLength(data),
        'device': 'dscalarm'
      }
    };
  
    var req = http.request(opts);
    req.on('error', function(err, req, res) {
      logger("Notify","Notify error: "+err);
    });
    req.write(data);
    req.end();
}
//console.log("DSCAlarm Started");
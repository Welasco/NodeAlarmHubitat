const SerialPort = require('serialport')
const Readline = require('@serialport/parser-readline')
const port = new SerialPort('COM4')

port.on('open', showPortOpen);
port.on('data', receivedFromSerial);
port.on('close', showPortClose);
port.on('error', showError);

// sample: aaaaaaaaaaa0

const parser = port.pipe(new Readline({ delimiter: '0' }))
parser.on('data', console.log)

// Method used to Open the serial communication with DSC IT-100 Board
function showPortOpen() {
    //logger("SerialPort",'Serial Port opened: ' + sport + ' BaudRate: ' + myPort.options.baudRate);
    portStatus = 1;
}





// Method used to Receive serial communication from DSC IT-100 Board
function receivedFromSerial(data) {
    console.log(data.toString());
}

// Method used to close the Serial Port
function showPortClose() {
    console.log("SerialPort",'Serial Port closed: ' + sport);
}

// Method used to list any serial communication error
function showError(error) {
    console.log("SerialPort",'Serial port error: ' + error);
}
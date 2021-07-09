/**
 *  DSCAlarmDeviceType
 *
 *  Author: Victor Santana
 *  
 *  Date: 2020-12-02
 */


metadata {
    // Automatically generated. Make future change here.
    definition (name: "DSCAlarmV2 Alarm Panel", namespace: "DSCAlarmV2", author: "victor@hepoca.com") {
        capability "Alarm"
        capability "Switch"
        capability "Motion Sensor"
        capability "Contact Sensor"
        capability "Refresh"
        
        attribute "alarmStatus", "string"
        attribute "zone1", "string"     
        attribute "zone2", "string"
        attribute "zone3", "string"
        attribute "zone4", "string"
        attribute "zone5", "string"
        attribute "zone6", "string"
        attribute "awaySwitch", "string"
        attribute "homeSwitch", "string"
        attribute "nightSwitch", "string"
        attribute "panic", "string"
        attribute "systemStatus", "string"
        attribute "chime", "string"

        command "armAway"
        command "armHome"
        command "armNight"
        command "disarm"
        //command "clear"
        //command "update"
        command "chimeToggle"
        // command "panic"
        // command "away"
        // command "dscalarmparse"
        // command "updatestatus"
        command "alarmsetdate"
    }
}


def dscalarmparse(String description) {
    def stateToDisplay
    def msg = description
    parent.writeLog("DSCAlarmSmartAppV2 Motion Device Type - Processing command: $msg")
    
    if ( msg.length() >= 4 ) {
        if ( msg.substring(0, 2) == "RD" ) {
            if (msg[3] == "0") {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm notready")
                sendEvent(name: "alarmStatus", value: "notready")
                sendEvent(name: "systemStatus", value: "notReady")
                sendEvent(name: "awaySwitch", value: "off")
                sendEvent(name: "staySwitch", value: "off")
                sendEvent(name: "nightSwitch", value: "off")
                sendEvent(name: "contact", value: "open")
            }
            else {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm ready")
                //parent.updateAlarmSystemStatus("ready")
                sendEvent(name: "alarmStatus", value: "ready")
                sendEvent(name: "awaySwitch", value: "off")
                sendEvent(name: "staySwitch", value: "off")
                sendEvent(name: "nightSwitch", value: "off")
                sendEvent(name: "switch", value: "off")
                sendEvent(name: "contact", value: "open")
                sendEvent(name: "systemStatus", value: "noEvents")
            }
        // Process arm update
        } else if ( msg.substring(0, 2) == "AR" ) {
            if (msg[3] == "0") {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm disarmed")
                parent.updateAlarmSystemStatus("disarmed")
                sendEvent(name: "systemStatus", value: "disarmed")
                sendEvent(name: "alarmStatus", value: "disarmed") 
                sendEvent(name: "awaySwitch", value: "off")
                sendEvent(name: "staySwitch", value: "off")
                sendEvent(name: "nightSwitch", value: "off")
                sendEvent(name: "switch", value: "off")
                sendEvent(name: "contact", value: "open")
            }
            else if (msg[3] == "1") {
                if (msg[5] == "0") {
                    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm Away")
                    parent.updateAlarmSystemStatus("armAway")
                    sendEvent(name: "alarmStatus", value: "armAway")
                    sendEvent(name: "awaySwitch", value: "on")
                    sendEvent(name: "staySwitch", value: "off")
                    sendEvent(name: "nightSwitch", value: "off")
                    sendEvent(name: "switch", value: "on")
                    sendEvent(name: "contact", value: "closed")
                }
                else if (msg[5] == "2") {
                    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm Stay")
                    parent.updateAlarmSystemStatus("armHome")
                    sendEvent(name: "alarmStatus", value: "armHome")
                    sendEvent(name: "awaySwitch", value: "off")
                    sendEvent(name: "staySwitch", value: "on")
                    sendEvent(name: "nightSwitch", value: "off")
                    sendEvent(name: "switch", value: "on")
                    sendEvent(name: "contact", value: "closed")
                }
                else if (msg[5] == "4") {
                    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm Night")
                    parent.updateAlarmSystemStatus("armNight")
                    sendEvent(name: "alarmStatus", value: "armNight")
                    sendEvent(name: "awaySwitch", value: "off")
                    sendEvent(name: "staySwitch", value: "off")
                    sendEvent(name: "nightSwitch", value: "on")
                    sendEvent(name: "switch", value: "on")
                    sendEvent(name: "contact", value: "closed")
                }                
            }
            else if (msg[3] == "2") {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm Arming")
                //parent.updateAlarmSystemStatus("arming")
                sendEvent(name: "systemStatus", value: "arming")
                // sendEvent(name: "awaySwitch", value: "off")
                // sendEvent(name: "staySwitch", value: "off")
                // sendEvent(name: "nightSwitch", value: "off")
                // sendEvent(name: "switch", value: "on")
            }
            else if (msg[3] == "3") {
               parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm Armed")
               // parent.updateAlarmSystemStatus("armed")
               sendEvent(name: "systemStatus", value: "armed")
                
            }            
        } else if ( msg.substring(0, 2) == "SY" ) {
         // Process various system statuses
            if ( msg.substring(3, 6) == "658")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Keypad Lockout")
                sendEvent(name: "systemStatus", value: "System Status\nKeypad Lockout")
            }
            else if ( msg.substring(3, 6) == "659")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Keypad Blanking")
                sendEvent(name: "systemStatus", value: "keypadBlanking")
            }            
            else if ( msg.substring(3, 6) == "670")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm Invalid Access Code")
                sendEvent(name: "systemStatus", value: "System Status\nInvalid Access Code")
            }
            else if ( msg.substring(3, 6) == "672")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Failed to Arm")
                sendEvent(name: "systemStatus", value: "System Status\nFailed to arm")
            }
            else if ( msg.substring(3, 6) == "673")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Partition Busy")
                sendEvent(name: "systemStatus", value: "partitionBusy")
            }            
            else if ( msg.substring(3, 6) == "802")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Panel AC Trouble")
                sendEvent(name: "systemStatus", value: "System Status\nPanel AC Trouble")
            }
            else if ( msg.substring(3, 6) == "803")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Panel AC Trouble Rest")
                sendEvent(name: "systemStatus", value: "System Status\nPanel AC Trouble Rest")
            }
            else if ( msg.substring(3, 6) == "806")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status System Bell Trouble")
                sendEvent(name: "systemStatus", value: "System Status\nSystem Bell Trouble")
            }
            else if ( msg.substring(3, 6) == "807")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status System Bell Trouble Rest")
                sendEvent(name: "systemStatus", value: "System Status\nSystem Bell Trouble Rest")
            }
            else if ( msg.substring(3, 6) == "810")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status TLM line 1 Trouble")
                sendEvent(name: "systemStatus", value: "System Status\nTLM line 1 Trouble")
            }
            else if ( msg.substring(3, 6) == "811")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status TLM line 1 Trouble Rest")
                sendEvent(name: "systemStatus", value: "System Status\nTLM line 1 Trouble Rest")
            }
            else if ( msg.substring(3, 6) == "812")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status TLM line 2 Trouble")
                sendEvent(name: "systemStatus", value: "System Status\nTLM line 2 Trouble")
            }
            else if ( msg.substring(3, 6) == "813")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status TLM line 2 Trouble Rest")
                sendEvent(name: "systemStatus", value: "System Status\nTLM line 2 Trouble Rest")
            }
            else if ( msg.substring(3, 6) == "821")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Low Battery")
                sendEvent(name: "systemStatus", value: "System Status\nLow Battery")
            }
            else if ( msg.substring(3, 6) == "822")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Low Battery Rest")
                sendEvent(name: "systemStatus", value: "System Status\nLow Battery Rest")

            }
            else if ( msg.substring(3, 6) == "829")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Sytem Tamper")
                sendEvent(name: "systemStatus", value: "System Status\nSystem Tamper")
            }
            else if ( msg.substring(3, 6) == "830")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Sytem Tamper Rest")
                sendEvent(name: "systemStatus", value: "System Status\nSystem Tamper Rest")
            }
            else if ( msg.substring(3, 6) == "840")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Trouble Status (LCD)")
                sendEvent(name: "systemStatus", value: "System Status\nTrouble Status(LCD)")
            }
            else if ( msg.substring(3, 6) == "841")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Trouble Status (LCD) Rest")
                sendEvent(name: "systemStatus", value: "System Status\nTrouble Status Rest")

            }
            else if ( msg.substring(3, 6) == "896")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Keybus fault")
                sendEvent(name: "systemStatus", value: "System Status\nKeybus fault")
            }
            else if ( msg.substring(3, 6) == "897")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Keybus fault Rest")
                sendEvent(name: "systemStatus", value: "System Status\nKeybus Fault Rest")
            }
         
        // Process alarm update
        } else if ( msg.substring(0, 2) == "AL" ) {
            if (msg[3] == "1") {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System AL")
                sendEvent(name: "alarmStatus", value: "alarm")
            }
        // Process chime update
        } else if ( msg.substring(0, 2) == "CH" ) {
            if (msg[3] == "1") {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm Chime On")
                sendEvent(name: "chime", value: "chimeOn")
            } else {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm Chime Off")
                sendEvent(name: "chime", value: "chimeOff")
            }    
        // Process zone update
        } else if ( msg.substring(0, 2) == "ZN" ) {
            parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm Changing Zone Status Type to Open or Close ${msg.substring(3, 9)}")            
            if ( msg.substring(3, 9) == "609001" ){
                stateToDisplay = "zone1open"
                sendEvent(name: "zone1", value: stateToDisplay)
            }
            else if ( msg.substring(3, 9) == "610001" ){
                stateToDisplay = "zone1closed"
                sendEvent(name: "zone1", value: stateToDisplay)
            }
            else if ( msg.substring(3, 9) == "609002" ){
                stateToDisplay = "zone2open"
                sendEvent(name: "zone2", value: stateToDisplay)
            }
            else if ( msg.substring(3, 9) == "610002" ){
                stateToDisplay = "zone2closed"
                sendEvent(name: "zone2", value: stateToDisplay)
            }
            else if ( msg.substring(3, 9) == "609003" ){
                stateToDisplay = "zone3open"
                sendEvent(name: "zone3", value: stateToDisplay)
            }
            else if ( msg.substring(3, 9) == "610003" ){
                stateToDisplay = "zone3closed"
                sendEvent(name: "zone3", value: stateToDisplay)
            }
            else if ( msg.substring(3, 9) == "609004" ){
                stateToDisplay = "zone4open"
                sendEvent(name: "zone4", value: stateToDisplay)
            }
            else if ( msg.substring(3, 9) == "610004" ){
                stateToDisplay = "zone4closed"
                sendEvent(name: "zone4", value: stateToDisplay)
            }
            else if ( msg.substring(3, 9) == "609005" ){
                stateToDisplay = "zone5open"
                sendEvent(name: "zone5", value: stateToDisplay)
            }
            else if ( msg.substring(3, 9) == "610005" ){
                stateToDisplay = "zone5closed"
                sendEvent(name: "zone5", value: stateToDisplay)
            }
            else if ( msg.substring(3, 9) == "609006" ){
                stateToDisplay = "zone6open"
                sendEvent(name: "zone6", value: stateToDisplay)
            }
            else if ( msg.substring(3, 9) == "610006" ){
                stateToDisplay = "zone6closed"
                sendEvent(name: "zone6", value: stateToDisplay)
            }     
            else {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Unhandled zone: ${msg}")
            }
        }
    }
}

// Implement "switch" (turn alarm on/off)
def on() {
    armAway()
}

def off() {
    disarm()
}

// Commands sent to the device
def armAway() {
    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Sending armAway command")
    sendRaspberryCommand("alarmArmAway")
}

def armNight() {
    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Sending armNight command")
    sendRaspberryCommand("alarmArmNight")
}

def armHome() {
    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Sending armHome command")
    sendRaspberryCommand("alarmArmStay")
}

def disarm() {
    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Sending disarm command")    
    sendRaspberryCommand("alarmDisarm")
}

def chimeToggle() {
    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Sending Toggling chime")    
    sendRaspberryCommand("alarmChimeToggle")
}

def siren() {
    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Sending alarmPanic")    
    sendRaspberryCommand("alarmPanic")
}

def strobe() {
    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Sending alarmFire")    
    sendRaspberryCommand("alarmFire")
}

def alarmsetdate() {
    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Sending alarmSetDate")    
    sendRaspberryCommand("alarmsetdate")
}

// TODO: Need to send off, on, off with a few secs in between to stop and clear the alarm
def refresh() {
    log.info "DSC AlarmPanel issued command: alarmUpdate"
    sendRaspberryCommand("alarmUpdate")
}



def sendRaspberryCommand(String command) {
	def path = "/api/$command"
    parent.sendCommand(path);
}

// This method must exist
// it's used by hubitat to process the device message
def parse(description) {
    //parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Receive Lan Command ${description}")
	parent.lanResponseHandler(description)
}
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
        command "chimeToggle"
        command "alarmSetDate"
        
    }
}


def dscalarmparse(String description) {
    def stateToDisplay
    def msg = description
    parent.writeLog("DSCAlarmSmartAppV2 Motion Device Type - Processing command: $msg")
    
    if ( msg.length() >= 4 ) {
        if ( msg.substring(0, 2) == "RD" ) {
            if (msg[3] == "0") {
                //log.info("DSC AlarmPanel status: not ready")
                sendEvent(name: "alarmStatus", value: "notready")
                sendEvent(name: "systemStatus", value: "notReady")
                sendEvent(name: "awaySwitch", value: "off")
                sendEvent(name: "staySwitch", value: "off")
                sendEvent(name: "nightSwitch", value: "off")
                sendEvent(name: "contact", value: "open")
            }
            else {
                //log.info("DSC AlarmPanel status: ready")
                //parent.updateAlarmSystemStatus("ready")
                sendEvent(name: "alarmStatus", value: "ready")
                sendEvent(name: "awaySwitch", value: "off")
                sendEvent(name: "staySwitch", value: "off")
                sendEvent(name: "nightSwitch", value: "off")
                sendEvent(name: "switch", value: "off")
                sendEvent(name: "contact", value: "open")
                sendEvent(name: "systemStatus", value: "noEvents")
            }
 ////////////////// Process arm update//////////////////////////////
            
        } else if ( msg.substring(0, 2) == "AR" ) {
            if (msg[3] == "0") {
                log.info("DSC AlarmPanel status: disarmed")
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
                    log.info("DSC AlarmPanel status: armAway")
                    parent.updateAlarmSystemStatus("armAway")
                    sendEvent(name: "alarmStatus", value: "armAway")
                    sendEvent(name: "awaySwitch", value: "on")
                    sendEvent(name: "staySwitch", value: "off")
                    sendEvent(name: "nightSwitch", value: "off")
                    sendEvent(name: "switch", value: "on")
                    sendEvent(name: "contact", value: "closed")
                }
                else if (msg[5] == "2") {
                    log.info("DSC AlarmPanel status: armHome")
                    parent.updateAlarmSystemStatus("armHome")
                    sendEvent(name: "alarmStatus", value: "armHome")
                    sendEvent(name: "awaySwitch", value: "off")
                    sendEvent(name: "staySwitch", value: "on")
                    sendEvent(name: "nightSwitch", value: "off")
                    sendEvent(name: "switch", value: "on")
                    sendEvent(name: "contact", value: "closed")
                }
                else if (msg[5] == "4") {
                    log.info("DSC AlarmPanel status: armNight")
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
               log.info("DSC AlarmPanel: exit delay/arming")
               // parent.updateAlarmSystemStatus("arming")
               sendEvent(name: "systemStatus", value: "arming")
                
            }
            else if (msg[3] == "3") {
               log.info("DSC AlarmPanel: Armed")
               // parent.updateAlarmSystemStatus("armed")
               sendEvent(name: "systemStatus", value: "armed")
                
            }
        } else if ( msg.substring(0, 2) == "SY" ) {
         // Process various system statuses
            if ( msg.substring(3, 6) == "658")  {
                log.info ("DSC AlarmPanel warning: Keypad Lockout")
                sendEvent(name: "systemStatus", value: "keypadLockout")
            }
            else if ( msg.substring(3, 6) == "659")  {
                log.info ("DSC AlarmPanel warning: Keypad Blanking")
                sendEvent(name: "systemStatus", value: "keypadBlanking")
            }
            else if ( msg.substring(3, 6) == "670")  {
                log.info ("DSC AlarmPanel warning: Invalid Access Code")
                sendEvent(name: "systemStatus", value: "invalidAccessCode")
            }
            else if ( msg.substring(3, 6) == "672")  {
                log.info ("DSC AlarmPanel warning: Failed to Arm")
                sendEvent(name: "systemStatus", value: "failedToArm")
            }
            else if ( msg.substring(3, 6) == "673")  {
                log.info ("DSC AlarmPanel warning: Partition Busy")
                sendEvent(name: "systemStatus", value: "partitionBusy")
            }
            else if ( msg.substring(3, 6) == "802")  {
                log.info ("DSC AlarmPanel warning: Lost AC Power")
                sendEvent(name: "systemStatus", value: "acPowerLost")
            }
            else if ( msg.substring(3, 6) == "803")  {
                log.info ("DSC AlarmPanel warning: AC power restored")
                sendEvent(name: "systemStatus", value: "acPowerRestored")
            }
            else if ( msg.substring(3, 6) == "806")  {
                log.info ("DSC AlarmPanel warning: Siren tampered")
                sendEvent(name: "systemStatus", value: "sirenTamper")
            }
            else if ( msg.substring(3, 6) == "807")  {
                log.info ("DSC AlarmPanel warning: Siren tamper restored")
                sendEvent(name: "systemStatus", value: "sirenTamperRestored")
            }
            else if ( msg.substring(3, 6) == "821")  {
                log.info ("DSC AlarmPanel warning: Low Battery")
                sendEvent(name: "systemStatus", value: "lowBattery")
            }
            else if ( msg.substring(3, 6) == "822")  {
                log.info ("DSC AlarmPanel warning: Battery Restored")
                sendEvent(name: "systemStatus", value: "batteryRestored")

            }
            else if ( msg.substring(3, 6) == "829")  {
                log.info ("DSC AlarmPanel warning: System Tampered")
                sendEvent(name: "systemStatus", value: "systemTamper")
            }
            else if ( msg.substring(3, 6) == "830")  {
                log.info ("DSC AlarmPanel warning: Sytem Tamper Restored")
                sendEvent(name: "systemStatus", value: "tamperRestored")
            }
            else if ( msg.substring(3, 6) == "840")  {
                log.info ("DSC AlarmPanel warning: Trouble led ON")
                sendEvent(name: "systemStatus", value: "troubleLedOn")
            }
            else if ( msg.substring(3, 6) == "841")  {
                log.info ("DSC AlarmPanel warning: Trouble led OFF")
                sendEvent(name: "systemStatus", value: "troubleLedOff")

            }
                     
        // Process alarm update
        } else if ( msg.substring(0, 2) == "AL" ) {
            if (msg[3] == "1") {
                log.info ("DSC AlarmPanel status: Intrusion Detected")
                sendEvent(name: "systemStatus", value: "partitionAlarmed")
            }
        // Process chime update
        } else if ( msg.substring(0, 2) == "CH" ) {
            if (msg[3] == "1") {
                log.info("DSC AlarmPanel status: chimeOn")
                sendEvent(name: "chime", value: "chimeOn")
            } else {
                log.info("DSC AlarmPanel status: chimeOff")
                sendEvent(name: "chime", value: "chimeOff")
            }    
        // Process zone update
        } else if ( msg.substring(0, 2) == "ZN" ) {
            parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type Alarm Changing Zone Status Type to Open or Close ${msg.substring(3, 9)}")            
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
            parent.writeLog ("DSCAlarmSmartAppV2 AlarmPanel Device Type - Unhandled zone: ${msg}")
            }
        }
    }
}



// Commands sent to the device
def armAway() {
    log.info("DSC AlarmPanel issued command: armAway")
    sendRaspberryCommand("alarmArmAway")
}

def armNight() {
    log.info("DSC AlarmPanel issued command: armNight")
    sendRaspberryCommand("alarmArmNight")
}

def armHome() {
    log.info("DSC AlarmPanel issued command: armHome")
    sendRaspberryCommand("alarmArmStay")
}

def disarm() {
    log.info ("DSC AlarmPanel issued command: disarm")    
    //sendRaspberryCommand("alarmDisarm")
    parent.updateAlarmSystemStatus("disarmed")
}

def off() {
    log.info ("DSC AlarmPanel issued command: disarm")    
    //parent.updateAlarmSystemStatus("disarmed")
    sendRaspberryCommand("alarmDisarm")
}

def on() {
    log.info("DSC AlarmPanel issued command: armAway")
    sendRaspberryCommand("alarmArmAway")
}

def chimeToggle() {
    log.info("DSC AlarmPanel issued command: alarmChimeToggle")    
    sendRaspberryCommand("alarmChimeToggle")
}

def siren() {
    log.info "DSC AlarmPanel issued command: alarmPanic"
    sendRaspberryCommand("alarmPanic")
}

def strobe() {
    log.info "DSC AlarmPanel issued command: alarmFire"
        sendRaspberryCommand("alarmFire")
}

def alarmSetDate() {
    log.info "DSC AlarmPanel issued command: alarmSetDate"
    sendRaspberryCommand("alarmSetDate")
}

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
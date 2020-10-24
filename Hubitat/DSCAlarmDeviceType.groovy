/**
 *  DSCAlarmDeviceType
 *
 *  Author: Victor Santana
 *   based on work by XXX
 *  
 *  Date: 2017-03-26
 */


metadata {
    // Automatically generated. Make future change here.
    definition (name: "DSCAlarmV2 Alarm Panel", namespace: "DSCAlarmV2", author: "victor@hepoca.com") {
        capability "Alarm"
        capability "Switch"
        capability "Motion Sensor"
        capability "Contact Sensor"
        
        attribute "alarmStatus", "string"
        attribute "zone1", "string"     
        attribute "zone2", "string"
        attribute "zone3", "string"
        attribute "zone4", "string"
        attribute "zone5", "string"
        attribute "zone6", "string"
        attribute "switchAway", "string"
        attribute "switchStay", "string"
        attribute "panic", "string"
        attribute "systemStatus", "string"

        command "armAway"
        command "armStay"
        command "disarm"
        command "clear"
        command "update"
        command "chimeToggle"
        command "panic"
        command "away"
        command "dscalarmparse"
        command "updatestatus"
        command "alarmsetdate"
    }

        // UI tile definitions
    tiles {
        
                standardTile("alarmStatus", "device.alarmStatus", width: 2, height: 2, canChangeIcon: false, canChangeBackground: false) {
                        state "ready", label: 'Ready', action: "armAway", icon: "st.Home.home2", backgroundColor: "#ffffff"
                        state "disarmed", label: 'Ready', action: "armAway", icon: "st.Home.home2", backgroundColor: "#ffffff"
                        state "notready", label: 'Not Ready', icon: "st.Home.home2", backgroundColor: "#ffa81e"
                        state "away", label: 'Away', action: "disarm", icon: "st.Home.home3", backgroundColor: "#add8e6"
                        state "stay", label: 'Stay', action: "disarm", icon: "st.Home.home4", backgroundColor: "#f1d801"
                        state "arming", label: 'Arming', action: "disarm", icon: "st.Home.home2", backgroundColor: "#B8B8B8"
                        state "alarm", label: 'Alarm', action: "clear", icon: "st.Home.home2", backgroundColor: "#ff0000"
                }
                standardTile("away", "device.awaySwitch", width: 1, height: 1, canChangeIcon: false, canChangeBackground: false) {
                        state "on", label: "Away", action: "disarm", icon: "st.Home.home3", backgroundColor: "#add8e6"
                        state "off", label: "Away", action: "armAway",icon: "st.Home.home3", backgroundColor: "#ffffff"
                }
                standardTile("stay", "device.staySwitch", width: 1, height: 1, canChangeIcon: false, canChangeBackground: false) {
                        state "on", label: "Stay", action: "disarm", icon: "st.Home.home4", backgroundColor: "#f1d801"
                        state "off", label: "Stay", action: "armStay",icon: "st.Home.home4", backgroundColor: "#ffffff"
                }
                standardTile("zone1", "device.zone1", width: 1, height: 1,inactiveLabel: false,  canChangeIcon: true, canChangeBackground: true) {
                        state "zone1open", label:'doors', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
                        state "zone1closed", label:'doors', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
                }
                standardTile("zone2", "device.zone2", width: 1, height: 1,inactiveLabel: false, canChangeIcon:true, canChangeBackground: true) {
                        state "zone2open", label:'So Din W', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
                        state "zone2closed", label:'So Din W', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
                } 
                standardTile("zone3", "device.zone3", width: 1, height: 1,inactiveLabel: false,canChangeIcon:true, canChangeBackground: true) {
                        state "zone3open", label:'Ki Ro W', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
                        state "zone3closed", label:'Ki Ro W', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
                }	
                standardTile("zone4", "device.zone4", width: 1, height: 1, inactiveLabel: false,canChangeIcon:true, canChangeBackground: true) {
                        state "zone4open", label:'Master', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
                        state "zone4closed", label:'Master', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
                }				
                standardTile("zone5", "device.zone5", width: 1, height: 1,inactiveLabel: false, canChangeIcon: true, canChangeBackground: true) {
                        state "zone5open", label:'BY Door', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
                        state "zone5closed", label:'BY Door', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
                }
                standardTile("zone6", "device.zone6", width: 1, height: 1,inactiveLabel: false,canChangeIcon:true, canChangeBackground: true) {
                        state "zone6open", label:'2ndFloor', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
                        state "zone6closed", label:'2ndFloor', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
                }

                standardTile("chime", "device.chime", width:1, height: 1, canChangeIcon: false, canChangeBackground: false) {
                        state "chimeOff", label:'Chime', action:'chimeToggle', icon:"st.secondary.off", backgroundColor: "#ffffff"
                        state "chimeOn", label:'', action:'chimeToggle', icon:"st.secondary.beep", backgroundColor: "#ffffff"
                }
                standardTile("panic", "device.panic", width: 1, height: 1, canChangeIcon: false, canChangeBackground: true) {
                        state "panic", label:'Panic', action:"panic", icon:"st.alarm.alarm.alarm", backgroundColor:"#ff0000"
                }
                standardTile("alarmsetdate", "device.alarmsetdate", width: 1, height: 1, canChangeIcon: false, canChangeBackground: true) {
                        state "alarmsetdate", label:'DateTime', action:"alarmsetdate", icon:"st.Office.office6"
                }
		        valueTile("systemStatus", "device.systemStatus", inactiveLabel: false,
		 	               decoration: "flat", width: 3, height: 1) {
			               state "default", label: '${currentValue}'
		        }
                main(["alarmStatus"])
                details(["alarmStatus","away","stay","zone5","zone6","zone4","zone1","zone2","zone3","basementBedWindow", "chime","systemStatus","panic","alarmsetdate"])
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
                // When status is "Not Ready" we cannot arm
                sendEvent(name: "awaySwitch", value: "off")
                sendEvent(name: "staySwitch", value: "off")
                sendEvent(name: "contact", value: "open")
            }
            else {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm ready")
                parent.updateAlarmSystemStatus("ready")
                sendEvent(name: "alarmStatus", value: "ready")
                // When status is "Ready" we can arm
                sendEvent(name: "awaySwitch", value: "off")
                sendEvent(name: "staySwitch", value: "off")
                sendEvent(name: "switch", value: "off")
                sendEvent(name: "panic", value: "off")
                sendEvent(name: "contact", value: "open")
                sendEvent(name: "systemStatus", value: "System Status:No events")
            }
        // Process arm update
        } else if ( msg.substring(0, 2) == "AR" ) {
            if (msg[3] == "0") {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm disarmed")
                parent.updateAlarmSystemStatus("ready")
                sendEvent(name: "alarmStatus", value: "disarmed") 
                sendEvent(name: "awaySwitch", value: "off")
                sendEvent(name: "staySwitch", value: "off")
                sendEvent(name: "switch", value: "off")
                sendEvent(name: "contact", value: "open")
            }
            else if (msg[3] == "1") {
                if (msg[5] == "0") {
                    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm Away")
                    parent.updateAlarmSystemStatus("armedaway")
                    sendEvent(name: "alarmStatus", value: "away")
                    sendEvent(name: "awaySwitch", value: "on")
                    sendEvent(name: "staySwitch", value: "off")
                    sendEvent(name: "switch", value: "on")
                    sendEvent(name: "contact", value: "closed")
                }
                else if (msg[5] == "2") {
                    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm Stay")
                    parent.updateAlarmSystemStatus("armedstay")
                    sendEvent(name: "alarmStatus", value: "stay")
                    sendEvent(name: "awaySwitch", value: "off")
                    sendEvent(name: "staySwitch", value: "on")
                    sendEvent(name: "switch", value: "on")
                    sendEvent(name: "contact", value: "closed")
                }
            }
            else if (msg[3] == "2") {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm Arming")
                parent.updateAlarmSystemStatus("arming")
                sendEvent(name: "alarmStatus", value: "arming")
                sendEvent(name: "awaySwitch", value: "off")
                sendEvent(name: "staySwitch", value: "off")
                sendEvent(name: "switch", value: "on")
            }
        } else if ( msg.substring(0, 2) == "SY" ) {
         // Process various system statuses
            if ( msg.substring(3, 6) == "658")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Keypad Lockout")
                sendEvent(name: "systemStatus", value: "System Status\nKeypad Lockout")
            }
            else if ( msg.substring(3, 6) == "670")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm Invalid Access Code")
                sendEvent(name: "systemStatus", value: "System Status\nInvalid Access Code")
            }
            else if ( msg.substring(3, 6) == "672")  {
                parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Parse msg - Alarm System Status Failed to Arm")
                sendEvent(name: "systemStatus", value: "System Status\nFailed to arm")
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

def away() {
    armAway()
}

def strobe() {
    panic()
}

def siren() {
    panic()
} 

def both() {
    panic()
}

// Commands sent to the device
def armAway() {
    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Sending armAway command")
    sendRaspberryCommand("alarmArmAway")
}

def armStay() {
    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Sending armStay command")
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

def panic() {
    log.debug "DSCAlarm AlarmDeviceType - Sending panic command"
    parent.writeLog("DSCAlarmSmartAppV2 AlarmPanel Device Type - Sending Toggling chime")        
    sendRaspberryCommand("alarmPanic")
}

def alarmsetdate() {
    log.debug "DSCAlarm AlarmDeviceType - Sending alarmSetDate command"
    sendRaspberryCommand("alarmsetdate")
}

// TODO: Need to send off, on, off with a few secs in between to stop and clear the alarm
def clear() {
    disarm()
}

def sendRaspberryCommand(String command) {
	def path = "/api/$command"
    parent.sendCommand(path);
}
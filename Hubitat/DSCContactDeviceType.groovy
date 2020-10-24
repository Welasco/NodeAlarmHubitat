/**
 *  DSCOpenCloseDeviceType
 *
 *  Author: Victor Santana
 *   based on work by XXX
 *  
 *  Date: 2017-03-26
 */

// for the UI
metadata {
  definition (name: "DSCAlarmV2 Zone Contact", namespace: "DSCAlarmV2", author: "victor@hepoca.com") {
    // Change or define capabilities here as needed
    capability "Refresh"
    capability "Contact Sensor"
    capability "Polling"
    capability "Sensor"

    // Add commands as needed
    command "updatedevicezone"
  }

  simulator {
    // Nothing here, you could put some testing stuff here if you like
  }

  tiles {
    // Main Row
    standardTile("zone", "device.contact", width: 2, height: 2, canChangeBackground: true, canChangeIcon: true) {
      state "open",   label: '${name}', icon: "st.contact.contact.open",   backgroundColor: "#ffa81e"
      state "closed", label: '${name}', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
    }

    // This tile will be the tile that is displayed on the Hub page.
    main "zone"

    // These tiles will be displayed when clicked on the device, in the order listed here.
    details(["zone"])
  }
}

// handle commands
def updatedevicezone(String cmd) {
  parent.writeLog("DSCAlarmSmartAppV2 Contact Device Type - Processing command: $cmd")
	if(cmd.substring(3,9).substring(0,3) == "609"){
		sendEvent (name: "contact", value: "open")
    parent.writeLog("DSCAlarmSmartAppV2 Contact Device Type - Changed to: Open")
	}
	else if (cmd.substring(3,9).substring(0,3) == "610"){
		sendEvent (name: "contact", value: "closed")
    parent.writeLog("DSCAlarmSmartAppV2 Contact Device Type - Changed to: Closed")
	}
}

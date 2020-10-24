# DSC IT-100 (RS232) NodeJS Alarm and SmartThings
Credits
-------
I'm really grateful that very knowledgeable guys have shared their research and projects in github and forums.

This project was inspired in those samples:

 - https://github.com/entrocode/SmartDSC
 - https://github.com/isriam/smartthings-alarmserver/
 - https://github.com/kholloway/smartthings-dsc-alarm
 - https://github.com/yracine/DSC-Integration-with-Arduino-Mega-Shield-RS-232


History
-------
This is just one more variant available to make your DSCAlarm system Smart with SmartThings.

The majority of those amazing projects available are using EnvisaLink board. If you already have one I suggest to you to use one of them.

My favority project is the one using Arduino but unfortunately the SmartThing Shield is deprecated and no longer available in the market.

I already have the DSC IT-100 board for that reason I decided to use it.

The project is on top of Raspberry Pi running NodeJS and SmartThings HUB Callback.

Prerequisites
--------------
* Compatible DSC Alarm System 
* [IT-100 interface](http://www.dsc.com/index.php?n=products&o=view&id=22)
* [Raspberry PI](https://www.raspberrypi.org/products/)
* [USB to Serial cable](https://www.insigniaproducts.com/pdp/NS-PU99501/5883029)

Required Software
-----------------
* Raspbian (latest)
* NodeJS
* SmartThing developer Access

Preparing Raspberry PI
----------------------
Install the latest available Raspbian Linux on your Raspberry PI. Official link with step by step available [HERE](https://www.raspberrypi.org/documentation/installation/installing-images/).
After you have installed Raspbian I would recommend update all available packets, just run "sudo apt-get update" then "sudo apt-get upgrade".

Keep in mind the easyest way to implement this project is configuring a static IP address on your Raspberry Pi and also for the SmartThings HUB.
The SmartThing HUB will send web request to NodeJS, your Raspberry Pi IP address will be configured during the Mobile App Setup using SmartThing app in your phone.
That's how SmartThing HUB knows how to reach your Raspberry Pi. If you prefer you can just go to your DHCP in your house and reserv IP Address to you Raspberry PI and Smartthings HUB using MAC address.
There is a really good step by step [HERE](http://thisdavej.com/beginners-guide-to-installing-node-js-on-a-raspberry-pi/) covering almost everything we need.
In case you are using RaspberryPI Zero W Here is the link on how to install [HERE](https://oshlab.com/install-latest-node-js-raspberry-pi-3/)

Now it's time to install NodeJS. Just type sudo "apt-get install nodejs".
Note: If you are using PiZeroW follow this link: [HERE](https://oshlab.com/install-latest-node-js-raspberry-pi-3/)

Install git to clone this repository type "sudo apt-get install git"

Create a folder named dscalarm under /home/pi it will look like this "/home/pi/dscalarm".

Now copy the following files under /home/pi/dscalarm:
 - config.json
 - dscalarm.js
 - listserialports.js
 - package.json
 - dscalarm.service

Install all project dependencies using npm. Make sure you are in /home/pi/dscalarm and type "npm install". It will download all dependencies under /home/pi/dscalarm/node_modules.

Time to plug the "USB to Serial cable" in your Raspberry Pi USB port. We need to know witch port it was loaded. Run node listserialports.js it will list all available serial ports.
It will be something like "/dev/ttyUSB0". Open config.js and update the config entry linuxcom.

Let's test if everything is right. Now we will run the dscalarm.js for the first time just type "node dscalarm.js". Something like this will show in the screen:

        Dec 22 18:04:58 raspberrypi dscalarm.js[1854]: Starting DSCAlarm
        Dec 22 18:05:00 raspberrypi dscalarm.js[1854]: [2017-12-23T00:05:00.677Z] [Modules] Modules loaded
        Dec 22 18:05:00 raspberrypi dscalarm.js[1854]: [2017-12-23T00:05:00.692Z] [OSVersion] Detected OS Version: linux
        Dec 22 18:05:00 raspberrypi dscalarm.js[1854]: [2017-12-23T00:05:00.731Z] [HTTP Endpoint] All HTTP endpoints loaded
        Dec 22 18:05:00 raspberrypi dscalarm.js[1854]: [2017-12-23T00:05:00.785Z] [HTTP Endpoint] HTTP Server Created at port: 3000
        Dec 22 18:05:00 raspberrypi dscalarm.js[1854]: [2017-12-23T00:05:00.798Z] [SerialPort] Creating Serial Port: /dev/ttyUSB0
        Dec 22 18:05:00 raspberrypi dscalarm.js[1854]: [2017-12-23T00:05:00.824Z] [SerialPort] Serial Port opened: /dev/ttyUSB0 BaudRate: 9600


The default port is configured to 3000 in config.json. You can now open your browser and check if you can access like: http://\<RaspberryPI-IPAddress>:3000/. 
We expect to have a message saying "DSC Alarm Running".

If you receve the message it means you have done a good job so far :).

Now we will test the communication between DSC IT-100 and your Raspberry PI.
Plug your USB to Serial cable to "DSC IT-100".
Open your browser and access http://\<RaspberryPI-IPAddress>:3000/api/alarmArmAway
It will send a command to your alarm to Arm. Your alarm keypad will start beeping and preparing the system to arm.

Now let's make DSCAlarm work as a service.

First we need to make it executable. Type chmod +755 dscalarm.js.
Copy the file dscalarm.service to /lib/systemd/system.
Reload the daemons type "sudo systemctl daemon-reload". Let's make it run on boot type "sudo systemctl enable dscalarm". Now we can start dscalarm type "sudo systemctl start dscalarm".
To check if the services is up and running type "sudo systemctl status dscalarm". To check the log type "sudo journalctl --follow -u dscalarm".

Reference: https://certsimple.com/blog/deploy-node-on-linux

Lets restart your Raspberry Pi and check if dscalarm is up and running. Don't forget to let the USB to Seral cable plugged, if it's not plugged it will fail.
Check if you can arm and disarm your alarm accessing the URLs that we have already used.

If you reach at this point it means you are able to arm the alarm using the URLs above and you are ready to start preparing the code on Smartthings.

Preparing SmartThing Code
--------------------------
First you need to determine your shard location. Shard is where your SmartThing account is hosted. In order to figure it out access this link [HERE](https://community.smartthings.com/t/faq-how-to-find-out-what-shard-cloud-slice-ide-url-your-account-location-is-on/53923).
Usually if your SmartThing account was created after september 2015 your shard will be: graph-na02-useast1.api.smartthings.com.

Now lets access your SmartThing shard and login with your SmartThing account if everything is fine you will be able to see all yours SmartThings devices in the Menu "My Devices".
Keep in mind that if your account is empty nothing will be listed.

The SmartThings have 3 main files:
 - DSCAlarmApp.groovy
 - DSCAlarmDeviceType.groovy
 - DSCContactDeviceType.groovy
 - DSCSmokeDeviceType.groovy
 - DSCMotionDeviceType.groovy

__DSCAlarmApp.groovy__:

That's our SmartApp (Service Manager). This is where we handle the Web requests that NodeJS will send to SmartThings using HUB CallBack.
Using your shard (SmartThing IDE Web Site) we will install your DSCAlarm Smpart App. Here is the steps:
 - Click on My SmartApps.
 - Click New SmartApp.
 - Click From Code.
 - Now Copy and Paste the file content to and click Create.
 - Click in Publish then For Me.

__DSCAlarmDeviceType.groovy__:
 
That's our DSC Alarm Device type. 
This is the Virtual Alarm device that will be present in your SmartThing Mobile App and also where you will be able to send commands to your alarm like Arm, Disarm.
Using your shard (SmartThing IDE Web Site) we will install your DSCAlarm Device type. Here is the steps:
 - Click on My Device Handlers.
 - Click Create New Device Handler. 
 - Click From Code.
 - Now Copy and Paste the file content to and click Create.
 - Click in Publish then For Me.

__DSCOpenContactDeviceType.groovy__:

That's our Open/Close sensor.
This is the Virtual Open/Close sensor that will be representing each Zone that you may have on your alarm.
Using your shard (SmartThing IDE Web Site) we will install your DSCAlarm Open/Close Device type. Here is the steps:
 - Click on My Device Handlers.
 - Click Create New Device Handler. 
 - Click From Code.
 - Now Copy and Paste the file content to and click Create.
 - Click in Publish then For Me.

 
Troubleshooting
---------------
After some time using this project I found a couple of problems related with USB Serial cable and Raspberry Pi 3.
Some times it just stop sending and receiving data. 
Using dmesg I found this error: "ftdi_sio ttyUSB0: usb_serial_generic_read_bulk_callback - urb stopped: -32".
I did a internet research and found this link with a solution that worked for me: https://github.com/raspberrypi/linux/issues/1187

You have to open /boot/cmdline.txt and add an entry to dwc_otg.speed=1. It changed the USB Bus speed to USB 1.1 but fix the issue.
Here is how my /boot/cmdline.txt looks like:

		dwc_otg.speed=1 dwc_otg.lpm_enable=0 console=serial0,115200 console=tty1 root=/dev/mmcblk0p2 rootfstype=ext4 elevator=deadline fsck.repair=yes rootwait

I hope it can help you too.


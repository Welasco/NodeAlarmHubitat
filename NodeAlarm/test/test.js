#!/usr/bin/env node
/*
 DSC Alarm Bridge
 This NodeJS code will work like a bridge/proxy between Smartthings and DSC IT-100 Serial board
 This code will monitor the DSC IT-100 Serial board and will send the received commands to SmartThings ServiceManager (Also known as SmartApp)

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
var nconf = require('nconf');
nconf.file({ file: './config.json' });
fs = require('fs');

console.log("Modules loaded");

// Setting Variables
var portStatus = 0
var winCom = nconf.get('dscalarm:wincom');
var linuxCom = nconf.get('dscalarm:linuxcom');;
var alarmPassword = nconf.get('dscalarm:alarmpassword');
var baudRate = nconf.get('dscalarm:baudRate');

var httpport = nconf.get('httpport');
console.log(nconf.get());
//var isWin = /^win/.test(process.platform);
// Detecting the OS Version to setup the right com port useful for debugging
console.log("Detected OS Version: " + process.platform);

var winCom = nconf.get('dscalarm:wincom');
var linuxCom = nconf.get('dscalarm:linuxcom');;
var alarmPassword = nconf.get('dscalarm:alarmpassword');
var baudRate = nconf.get('dscalarm:baudRate');
var httpport = nconf.get('httpport');

console.log(httpport + " " + winCom + " " + linuxCom + " " + alarmPassword + " " + baudRate);
console.log(nconf.get());
/**
 *  Insteon Dimmer Switch
 *  Original Author     : ethomasii@gmail.com
 *  Creation Date       : 2013-12-08
 *
 *  Rewritten by        : idealerror
 *  Last Modified Date  : 2016-12-13 
 *
 *  Rewritten by        : kuestess
 *  Last Modified Date  : 2017-5-10
 *
 *	Rewritten by		: Trussmky
 *	Last Modified Date	: 2017-06-12
 *  
 *  Disclaimer about 3rd party server: No longer uses third-party server :)
 * 
 *  Changelog:
 * 
 *  2016-12-13: Added polling for Hub2
 *  2016-12-13: Added background refreshing every 3 minutes
 *  2016-11-21: Added refresh/polling functionality
 *  2016-10-15: Added full dimming functions
 *  2016-10-01: Redesigned interface tiles
 *
 * Device ID's must be capital letters 
 */
 
import groovy.json.JsonSlurper
 
preferences {
    input("deviceid", "text", title: "Device ID", description: "Your Insteon device.  Do not include periods example: FF1122. Use all Caps")
    input("host", "text", title: "URL", description: "The URL of your Hub (without http:// example: my.hub.com ")
    input("port", "text", title: "Port", description: "The hub port.")
    input("username", "text", title: "Username", description: "The hub username (found in app)")
    input("password", "text", title: "Password", description: "The hub password (found in app)")
} 
 
metadata {
    definition (name: "Insteon Fan-Linc Fan (Hub 2254)", author: "Trussmky", oauth: true) {
        capability "Light"
        capability "Actuator"
        capability "Switch"
        capability "Sensor"
        capability "Refresh"
        capability "Polling"
        
        command "low_Speed"
        command "med_Speed"
        command "high_Speed"
        command "fan_off"

        attribute "fanSpeed", "string"
    }
	tiles(scale: 2) {
    	
        standardTile("fanSpeed", "fanSpeed", decoration: "flat", width: 4, height: 4) {
        	state "Fan_Off", label: "OFF", backgroundColor:"#ffffff", icon:"st.thermostat.fan-auto"
            state "on01", label: "LOW", backgroundColor: "#F2F5A9", icon:"st.thermostat.fan-auto"
            state "on02", label: "MED", backgroundColor: "#ACFA58", icon:"st.thermostat.fan-auto"
            state "on03", label: "HIGH", backgroundColor: "#2ECCFA", icon:"st.thermostat.fan-auto"
            }
        standardTile("offSpeed", "fanSpeed", decoration: "flat", width:2, height: 2) {
        	state "Fan_Off", label: "OFF", action: "fan_off", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#ff9602"//, nextState: "on01" //"turningOn"
            state "on01", label: "OFF", action: "fan_off", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#ffffff"
            state "on02", label: "OFF", action: "fan_off", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#ffffff"
            state "on03", label: "OFF", action: "fan_off", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#ffffff"
            }
        standardTile("01Speed", "fanSpeed", decoration: "flat", width:2, height:2) { 
            state "on01", label: "LOW", action: "low_Speed", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#79b821"//, nextState: "on02" //"turningOff"
            state "Fan_Off", label:"LOW", action: "low_Speed", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#ffffff"
            state "on02", label:"LOW", action: "low_Speed", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#ffffff"
            state "on03", label:"LOW", action: "low_Speed", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#ffffff"
		}
        standardTile("02Speed", "fanSpeed", decoration: "flat", width: 2, height: 2) {
            state "on02", label: "MED", action: "med_Speed", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#79b821"//, nextState: "on03" //"turningOff"
            state "Fan_Off", label: "MED", action: "med_Speed", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#ffffff"
            state "on01", label: "MED", action: "med_Speed", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#ffffff"
            state "on03", label: "MED", action: "med_Speed", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#ffffff"            
		}
        standardTile("03Speed", "fanSpeed", decoration: "flat", width: 2, height: 2) {
            state "on03", label: "HIGH", action: "high_Speed", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#79b821"//, nextState: "off" //"turningOff"
            state "Fan_Off", label: "HIGH", action: "high_Speed", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#ffffff"
            state "on01", label: "HIGH", action: "high_Speed", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#ffffff"
            state "on02", label: "HIGH", action: "high_Speed", icon:"st.samsung.da.RAC_4line_02_ic_fan", backgroundColor:"#ffffff"            
		}

        standardTile("refresh", "fanSpeed", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        main(["fanSpeed"])
        	details(["fanSpeed", "offSpeed", "01Speed", "02Speed", "03Speed", "refresh"])
		}

// simulator metadata
    simulator {
    }
} // end Metadata

// Not in use
def parse(String description) {
}

def fan_off() {
//def fan_Off() {
    log.debug ("Fan Set OFF")
    sendCmd("11", "00", "1F") // (command_Code, Level, SubDevice)
    sendEvent(name: "fanSpeed", value: "Fan_Off", isStateChange: true);
}

def low_Speed() {
    log.debug "Fan Set Low"
    sendCmd("11", "55", "1F")
//        sendEvent(name: "currentSpeed", value: "LOW", isStateChange: true);
        sendEvent(name: "fanSpeed", value: "on01", isStateChange: true)
}

def med_Speed() {
    log.debug "Fan Set Medium"
    sendCmd("11", "A9", "1F")
    sendEvent(name: "fanSpeed", value: "on02", isStateChange: true);
}

def high_Speed() {
    log.debug "Fan Set High"
    sendCmd("11", "FF", "1F")
    sendEvent(name: "fanSpeed", value: "on03", isStateChange: true);
}

/*
def setLevel(value) {

    // log.debug "setLevel >> value: $value"
    
    // Max is 255
    def percent = value / 100
    def realval = percent * 255
    def valueaux = realval as Integer
    def level = Math.max(Math.min(valueaux, 255), 0)
    if (level > 0) {
        sendEvent(name: "switch", value: "on")
    } else {
        sendEvent(name: "switch", value: "off")
    }
    log.debug "dimming to $level"
    dim(level,value)
}

def dim(level, real) {
    String hexlevel = level.toString().format( '%02x', level.toInteger() )
    sendCmd("11",hexlevel , "0F")
    sendEvent(name: "level", value: real, unit: "%")
}
*/
def sendCmd(num, level, subCode)
{
    log.debug "Sending Command"

    httpGet("http://${settings.username}:${settings.password}@${settings.host}:${settings.port}//3?0262${settings.deviceid}${subCode}${num}${level}=I=3") {response -> 
        def content = response.data
        
         //log.debug "content: ${content}"
    }
    log.debug "Command Completed"
}

def refresh()
{
    log.debug "Refreshing.."
    poll()
}

def poll()
{
    log.debug "Polling.."
	getStatus()
    runIn(180, refresh)
}

def ping()
{
    log.debug "Pinging.."
    poll()
}

def initialize(){
    poll()
}

def getStatus() {

    def myURL = [
		uri: "http://${settings.username}:${settings.password}@${settings.host}:${settings.port}/3?0262${settings.deviceid}0F1903=I=3"
	]
    
    log.debug myURL
    httpPost(myURL)
	
    def buffer_status = runIn(2, getBufferStatus)
}

def getBufferStatus() {
	def buffer = ""
	def params = [
        uri: "http://${settings.username}:${settings.password}@${settings.host}:${settings.port}/buffstatus.xml"
    ]
    
    try {
        httpPost(params) {resp ->
            buffer = "${resp.responseData}"
            log.debug "Buffer: ${resp.responseData}"
        }
    } catch (e) {
        log.error "something went wrong: $e"
    }

	def buffer_end = buffer.substring(buffer.length()-2,buffer.length())
	def buffer_end_int = Integer.parseInt(buffer_end, 16)
    
    def parsed_buffer = buffer.substring(0,buffer_end_int)
    log.debug "ParsedBuffer: ${parsed_buffer}"
    
    def responseID = parsed_buffer.substring(22,28)
     
    
    if (responseID == settings.deviceid) {
        log.debug "Response is for correct device: ${responseID}"
        def status = parsed_buffer.substring(38,40)
        log.debug "Status: ${status}"
		def devCode = parsed_buffer.substring(12,16)
        def level = Math.round(Integer.parseInt(status, 16)*(100/255))
        log.debug "Level: ${level}"
        log.debug "device code: ${devCode}"
        
	if (devCode == "1901"){	//status of light 
        
        if (level == 0) {
            log.debug "Device is off..."
            sendEvent(name: "switch", value: "off")
            sendEvent(name: "level", value: level, unit: "%")
            }

        else if (level > 0) {
            log.debug "Device is on..."
            sendEvent(name: "switch", value: "on")
            sendEvent(name: "level", value: level, unit: "%")
        	}
        }
     else if (devCode == "1903") {	//status of fan
     		log.debug "fan status returned with ${devCode}"
        if (level == 00) {
        	log.debug "fan is Off"
            sendEvent(name: "fanSpeed", value: "off")
        }
        else if (level < 34) {
        	log.debug "fan is Low"
            sendEvent(name: "fanSpeed", value: "on01")
        }
        else if (level <99) {
        	log.debug "fan is Med"
            sendEvent(name: "fanSpeed", value: "on02")
       	}
        else if (level == 100) {
        	log.debug "Fan is High"
            sendEvent(name: "fanSpeed", value: "on03")
            }
     }
    } else {
    	log.debug "Response is for wrong device - trying again"
        getStatus()
    }
}
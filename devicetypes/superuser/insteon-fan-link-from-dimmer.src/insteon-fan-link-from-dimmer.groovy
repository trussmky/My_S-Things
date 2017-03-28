/**
 *  Insteon Dimmer Switch
 *  Original Author     : ethomasii@gmail.com
 *  Creation Date       : 2013-12-08
 *
 *  Rewritten by        : idealerror
 *  Last Modified Date  : 2016-12-13 
 *  
 *  Disclaimer about 3rd party server:
 * 
 *  The refresh function of this device type currently 
 *  calls out to my 3rd party server to contact your hub
 *  to determine the status of your device.  The reason
 *  for this is SmartThings cannot parse the XML that 
 *  the hub gives us.  It's malformed.  If you're
 *  uncomfortable with my server contacting your hub 
 *  directly, you should not use this device type.
 *  I do not store or log any information related to 
 *  this device type.
 * 
 *  Changelog:
 * 
 *  2016-12-13: Added background refreshing every 3 minutes
 *  2016-11-21: Added refresh/polling functionality
 *  2016-10-15: Added full dimming functions
 *  2016-10-01: Redesigned interface tiles
 *
 *	2017-02-06:	Attempting to create FanLink Device, Using GE Fan Control as a template
 
	NO Fan Control Yet
 
 */
 
import groovy.json.JsonSlurper
 
preferences {
    input("deviceid", "text", title: "Device ID", description: "Your Insteon device.  Do not include periods example: FF1122.")
    input("host", "text", title: "URL", description: "The URL of your Hub (without http:// example: my.hub.com ")
    input("port", "text", title: "Port", description: "The hub port.")
    input("username", "text", title: "Username", description: "The hub username (found in app)")
    input("password", "text", title: "Password", description: "The hub password (found in app)")
} 
 
metadata {
    definition (name: "Insteon Fan Link from Dimmer", author: "idealerror", oauth: true) {
        capability "Switch Level"
        capability "Polling"
        capability "Switch"
        capability "Refresh"
        
        //added from fan control
        capability "Actuator" 
        capability "Sensor"
        capability "Health Check"
        
        command "lowSpeed"
        command "medSpeed"
        command "highSpeed"
        command "fanOff"
        
        attribute "currentState", "string"
        attribute "currentSpeed", "string"
        attribute "dCode", "string"
    }

    // simulator metadata
    simulator {
    }

    // UI tile definitions
    tiles(scale:2) {
       multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }
            
/*           tileAttribute ("statusText", key: "SECONDARY_CONTROL") {
               attributeState "statusText", label:'${currentValue}'	// Fan State?
           }
           */   
           tileAttribute ("device.fan", key: "SECONDARY_CONTROL") {
               attributeState "device.fan", label:'${currentSpeed}'	// Fan State?
           }
        }
// Fan Control Tiles
        
        standardTile("lowSpeed", "device.currentSpeed", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "LOW", label:'LOW', action: "lowSpeed", icon:"st.Home.home30"
  		}
		standardTile("medSpeed", "device.currentSpeed", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "MED", label: 'MED', action: "medSpeed", icon:"st.samsung.da.RAC_4line_02_ic_fan_dim"
		}
		standardTile("highSpeed", "device.currentSpeed", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "HIGH", label: 'HIGH', action: "highSpeed", icon:"st.samsung.da.RAC_4line_02_ic_fan_dim"
		}
		standardTile("fanOff", "device.currentSpeed", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "OFF", label: 'OFF', action: "fanOff", icon:"st.samsung.da.RAC_4line_02_ic_fan_dim"
		}

// basic tiles        
        standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }

        valueTile("level", "device.level", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "level", label:'${currentValue} %', unit:"%", backgroundColor:"#ffffff"
        }
        

        main(["switch"])
        details(["switch", "fanOff", "lowSpeed", "medSpeed", "highSpeed", "refresh"])
    }
}

// Not in use
def parse(String description) {
}

def on() {
    log.debug "Turning Light ON"
    sendCmd("11", "FF", "0F")
    sendEvent(name: "switch", value: "on");
//    sendEvent(name: "level", value: 100, unit: "%")
}

def off() {
    log.debug "Turning Light OFF"
    sendCmd("13", "00", "0F")
    sendEvent(name: "switch", value: "off");
//    sendEvent(name: "level", value: 0, unit: "%")
}

def fanOff() {
    log.debug "Fan Set OFF"
    sendCmd("19", "00", "1F") // (command_Code, Level, SubDevice)
//    sendEvent(name: "switch", value: "Fan_Off");
    sendEvent(name: "currentSpeed", value: "Fan_Off" as String);
//    sendEvent(name: "level", value: 0, unit: "%")
}

def lowSpeed() {
    log.debug "Fan Set Low"
    sendCmd("19", "55", "1F")
//    sendCmd("11", "55", "1F")
        sendEvent(name: "switch", value: "LOW" as String);
//    sendEvent(name: "level", value: 0, unit: "%")
}

def medSpeed() {
    log.debug "Fan Set Medium"
    sendCmd("19", "AA", "1F")
//    sendCmd("11", "AA", "1F")
    sendEvent(name: "switch", value: "MED" as String);
//    sendEvent(name: "level", value: 0, unit: "%")
}

def highSpeed() {
    log.debug "Fan Set High"
    sendCmd("19", "FF", "1F")
//    sendCmd("11", "FF", "1F")
    sendEvent(name: "switch", value: "HIGH" as String);
//    sendEvent(name: "level", value: 0, unit: "%")
}

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
    // log.debug "dimming value is $valueaux"
    log.debug "dimming to $level"
    dim(level,value)
}
/* 
// 	Does not have dimming
def dim(level, real) {
    String hexlevel = level.toString().format( '%02x', level.toInteger() )
    // log.debug "Dimming to hex $hexlevel"
    sendCmd("11",hexlevel)
    sendEvent(name: "level", value: real, unit: "%")
}
*/
def sendCmd(num, level, subCode)
{
    log.debug "Sending Command"

    // Will re-test this later
//     sendHubCommand(new physicalgraph.device.HubAction("""GET /3?0262${settings.deviceid}0F${num}${level}=I=3 HTTP/1.1\r\nHOST: IP:PORT\r\nAuthorization: Basic B64STRING\r\n\r\n""", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
//    httpGet("http://${settings.username}:${settings.password}@${settings.host}:${settings.port}//3?0262${settings.deviceid}${subCode}${num}${level}=I=3") {response -> 
    httpGet("http://${settings.username}:${settings.password}@${settings.host}:${settings.port}//3?0262${settings.deviceid}0F${num}${level}02=I=3") {response -> 

        def content = response.data
        
        log.info "num: ${num} level: ${level}"
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
    getStatus("1902")	// Status of Light
    getStatus("1903") // Status of fan
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

def getStatus(dCode) {

    // Unfortunately SmartThings is unable to parse the XML from Insteon.
    // This site is not logging anything, it's strictly a pass through.

    def params = [
//        uri: "http://st.idealerror.com/?url=http://${settings.username}:${settings.password}@${settings.host}:${settings.port}/sx.xml?${settings.deviceid}=1900"
//        uri: "http://72.193.55.67:21212/HubStatus.php/?url=http://${settings.username}:${settings.password}@${settings.host}:${settings.port}/sx.xml?${settings.deviceid}=1900"
        uri: "http://72.193.55.67:21212/HubStatus.php/?url=http://${settings.username}:${settings.password}@${settings.host}:${settings.port}/sx.xml?${settings.deviceid}=${dCode}"
]
	log.info "params: ${params}"
    try {
        httpPost(params) { resp ->

            def jsonSlurper = new JsonSlurper()
            def object = jsonSlurper.parseText("${resp.data}")
            log.debug "response: ${resp.data}"
			log.debug "Device Code: ${dCode}"
            log.debug "Percent: ${object.percent}"
            log.debug "Status: ${object.status}"
            }
            
       if (${dCode} == 1902){     
            if (object.percent > 0) {
                sendEvent(name: "switch", value: "on")
            //    sendEvent(name: "level", value: object.percent, unit: "%")
            } else {
                sendEvent(name: "switch", value: "off")
            //    sendEvent(name: "level", value: object.percent, unit: "%")
            }
        } 
        else if (dCode == 1903){
        
        	if (object.percent == 0){
        		sendEvent(name: "switch", value: "off")
     //   		object.currentSpeed = "Fan Off"
        	} else if (object.percent == 33){
        		sendEvent(name: "switch", value: "LOW")
        		
        	} else if (object.percent == 66){
        		sendEvent(name: "switch", value: "MED")
        	} else if (object.percent == FF){
        		sendEvent(name: "switch", value: "HIGH")
        	}
        }
    } catch (e) {
        log.error "something went wrong: $e"
    }
    
    //log.debug content
}


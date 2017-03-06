/**
 *  Raspberry Pi
 *
 *  Copyright 2015 Nicholas Wilde
 *
 *  Monitor your Raspberry Pi using SmartThings and BerryIO SmartThings <https://github.com/nicholaswilde/berryio-smartthings>
 *
 *  Contributors:
 *  Thanks to NewHorizons for BerryIO
 *  Thanks to Ledridge for the SmartThings addition to BerryIO
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
import groovy.json.JsonSlurper
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


preferences {
        input("ip", "string", title:"IP Address", description: "192.168.1.150", defaultValue: "192.168.1.150" ,required: true, displayDuringSetup: true)
        input("port", "string", title:"Port", description: "80", defaultValue: "80" , required: true, displayDuringSetup: true)
        input("username", "string", title:"Username", description: "pi", defaultValue: "pi" , required: true, displayDuringSetup: true)
        input("password", "password", title:"Password", description: "raspberry", defaultValue: "raspberry" , required: true, displayDuringSetup: true)
}

metadata {
	definition (name: "Raspberry Pi Multi", namespace: "nicholaswilde", author: "Nicholas Wilde") {
		capability "Polling"
		capability "Refresh"
		capability "Temperature Measurement"
        capability "Switch"
        capability "Sensor"
        //capability "Actuator"
        capability "Contact Sensor"
        
        attribute "cpuPercentage", "string"
        attribute "memory", "string"
        attribute "diskUsage", "string"
        attribute "fridge", "string"
        attribute "fridgeTemp", "string"
        attribute "fridgeHumid", "string"
        attribute "cpuTemperature", "string"
        attribute "SW1", "string"
        
        command "restart"
        command "SW1_on"
        command "SW1_off"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
		valueTile("cpuTemperature", "device.cpuTemperature", width: 1, height: 1) {
            state "temperature", label:'${currentValue}° CPU', unit: "C",
            backgroundColors:[
                [value: 25, color: "#153591"],
                [value: 35, color: "#1e9cbb"],
                [value: 47, color: "#90d2a7"],
                [value: 59, color: "#44b621"],
                [value: 67, color: "#f1d801"],
                [value: 76, color: "#d04e00"],
                [value: 77, color: "#bc2323"]
            ]
        }

        valueTile("cpuPercentage", "device.cpuPercentage", inactiveLabel: false) {
        	state "default", label:'${currentValue}% CPU', unit:"Percentage",
            backgroundColors:[
                [value: 31, color: "#153591"],
                [value: 44, color: "#1e9cbb"],
                [value: 59, color: "#90d2a7"],
                [value: 74, color: "#44b621"],
                [value: 84, color: "#f1d801"],
                [value: 95, color: "#d04e00"],
                [value: 96, color: "#bc2323"]
            ]
        }
        valueTile("memory", "device.memory", width: 1, height: 1) {
        	state "default", label:'${currentValue} MB', unit:"MB",
            backgroundColors:[
                [value: 353, color: "#153591"],
                [value: 287, color: "#1e9cbb"],
                [value: 210, color: "#90d2a7"],
                [value: 133, color: "#44b621"],
                [value: 82, color: "#f1d801"],
                [value: 26, color: "#d04e00"],
                [value: 20, color: "#bc2323"]
            ]
        }
        valueTile("diskUsage", "device.diskUsage", width: 1, height: 1) {
        	state "default", label:'${currentValue}% Disk', unit:"Percent",
            backgroundColors:[
                [value: 31, color: "#153591"],
                [value: 44, color: "#1e9cbb"],
                [value: 59, color: "#90d2a7"],
                [value: 74, color: "#44b621"],
                [value: 84, color: "#f1d801"],
                [value: 95, color: "#d04e00"],
                [value: 96, color: "#bc2323"]
            ]
        }
		valueTile("fridgeHumid", "device.fridgeHumid", width: 2, height: 1) {
            state "default", label:'${currentValue}% Humid', unit: "Percent",
            backgroundColors:[
                [value: 20, color: "#bc2323"],
                [value: 30, color: "#99ff66"],
                [value: 40, color: "#90d2a7"],
                [value: 50, color: "#33cc33"],
                [value: 60, color: "#00cc00"],
                [value: 70, color: "#99ff66"],
                [value: 80, color: "#bc2323"]
            ]
        }
		valueTile("temperature", "device.temperature", width: 2, height: 1) {
            state "temperature", label:'${currentValue}° Wine', unit: "F",
            backgroundColors:[
                [value: 25, color: "#153591"],
                [value: 47, color: "#1e9cbb"],
                [value: 55, color: "#90d2a7"],
                [value: 57, color: "#44b621"],
                [value: 65, color: "#f1d801"],
                [value: 68, color: "#d04e00"],
                [value: 77, color: "#bc2323"]
            ]
        }
        standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'Mode', icon: "st.Appliances.appliances6", backgroundColor: "#9de33b", action: "switch.on", nextState: "on"
			state "on", label: 'Mode', icon: "st.Appliances.appliances6", backgroundColor: "#79b821", action: "switch.on", nextState: "off"
		}
        
        standardTile("fridge", "device.fridge", width: 2, height: 2) {
  		  	state "off", label: 'Idle', icon:"st.Weather.weather11", backgroundColor:"#ffffff"
  		  	state "on", label: 'Cooling', icon:"st.Weather.weather7", backgroundColor:"#53a7c0"
		}
        
        standardTile("SW1", "device.SW1", width: 2, height: 2) {
        	state "off", label: 'On', icon: "st.contact.contact.closed", backgroundColor: "#ffffff", action: "SW1.off", nextState: "on"
            state "on", label: 'Off', icon: "st.contact.contact.open", backgroundColor: "#53a7c0", action: "SW1.on", nextState: "off"
        }
        
        
		multiAttributeTile(name:"richcontact", type:"generic", width:6, height:4) {
 			tileAttribute("device.controller", key: "PRIMARY_CONTROL") {
  		  	attributeState("off", label: 'Off', icon:"st.Seasonal Fall.seasonal-fall-008", action: "off", backgroundColor:"#ffffff")
  		  	attributeState("on", label: 'AutoTemp', icon:"st.Weather.weather2", action: "off", backgroundColor:"#53a7c0")
            }
  			tileAttribute("device.fridgeTemp", key: "SECONDARY_CONTROL") {
 			   attributeState("default", label:'${currentValue}°', unit:"F")
            }
		}
  		standardTile("restart", "device.restart", inactiveLabel: false, decoration: "flat") {
        	state "default", action:"restart", icon: "st.Seasonal Fall.seasonal-fall-009", label: "Restart", displayName: "Restart"
        }
        standardTile("refresh", "device.refresh", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
        	state "default", action:"refresh.refresh", icon: "st.secondary.refresh"
        }
        main "temperature"
        details(["richcontact", "switch", "SW1", "refresh", "fridge", "temperature", "fridgeHumid", "cpuTemperature", "cpuPercentage", "memory", "diskUsage", "restart"])
    }
}

// ------------------------------------------------------------------

// parse events into attributes
def parse(String description) {
	log.trace "Parse returned rpi"
    def map = [:]
    def descMap = parseDescriptionAsMap(description)
    log.debug "descMap: ${descMap}"
    
    def body = new String(descMap["body"].decodeBase64())
    log.debug "body: ${body}"
    
    def slurper = new JsonSlurper()
    def result = slurper.parseText(body)
    
    log.debug "result: ${result}"

	if (result){
    	log.debug "Computer is up"
   		//sendEvent(name: "switch", value: "on")
    }
    
    log.debug "check temp..."
    if (result.containsKey("cpu_temp")) {
    	log.debug "temp: ${result.cpu_temp.toDouble().round()}"
    	sendEvent(name: "cpuTemperature", value: result.cpu_temp.toDouble().round())
    }
    if (result.containsKey("fr_humid")) {
    	log.debug "fridgeHumid: ${result.fr_humid.toDouble().round()}"
    	sendEvent(name: "fridgeHumid", value: result.fr_humid.toDouble().round())
    }    
	if (result.containsKey("fr_temp")) {
    	log.debug "fridgetemp: ${result.fr_temp.toDouble().round()}"
    	sendEvent(name: "temperature", value: result.fr_temp.toDouble().round())
    	sendEvent(name: "fridgeTemp", value: result.fr_temp.toDouble().round())
    }    
    if (result.containsKey("cpu_perc")) {
    	log.debug "cpu_perc: ${result.cpu_perc}"
        sendEvent(name: "cpuPercentage", value: result.cpu_perc)
    }
    
    if (result.containsKey("mem_avail")) {
    	log.debug "mem_avail: ${result.mem_avail.toDouble().round()}"
        sendEvent(name: "memory", value: result.mem_avail.toDouble().round())
    }
    if (result.containsKey("disk_usage")) {
    	log.debug "disk_usage: ${result.disk_usage.toDouble().round()}"
        sendEvent(name: "diskUsage", value: result.disk_usage.toDouble().round())
    }
  	if (result.containsKey("gpio_value_17")) {
    	log.debug "gpio_value_17: ${result.gpio_value_17}"
        if (result.gpio_value_17.contains("0")){
        	log.debug "gpio_value_17: off"
            sendEvent(name: "SW1", value: "off")
        } else {
        	log.debug "gpio_value_17: on"
            sendEvent(name: "SW1", value: "on")
        }
    }
  	if (result.containsKey("gpio_value_21")) {
    	log.debug "gpio_value_21: ${result.gpio_value_21}"
        if (result.gpio_value_21.contains("0")){
        	log.debug "gpio_value_21: on"
            sendEvent(name: "controller", value: "on")
        } else {
        	log.debug "gpio_value_21: off"
            sendEvent(name: "controller", value: "off")
        }
    }  	
}

// handle commands
def poll() {
	log.debug "Executing 'poll'"
    //sendEvent(name: "switch", value: "off")
    getRPiData()
}

def on() {
	def fridgeState = device.currentState("fridge")
    log.debug "FridgeState: $fridgeState.value"
    if (fridgeState.value.contains("on")){
		sendEvent(name: "fridge", value: "off")
		log.debug "Turning fridge off."
    	def uri = "/api_command/gpio_set_mode/24/out"
    	def uri2 = "/api_command/gpio_set_value/24/0"
        
/*        def uri = "/api_command/gpio_set_mode/21/out"
    	def uri2 = "/api_command/gpio_set_value/21/0"
        */
    	postAction(uri)    
   		postAction(uri2) 
	}
    else {
		sendEvent(name: "fridge", value: "on")
    	log.debug "Turning fridge on."
    	def uri = "/api_command/gpio_set_mode/24/out"
    	def uri2 = "/api_command/gpio_set_value/24/1"

/*		def uri = "/api_command/gpio_set_mode/21/out"
    	def uri2 = "/api_command/gpio_set_value/21/1"
*/
		postAction(uri)    
   		postAction(uri2)    
    }
}
def off() {
	def fridgeState = device.currentState("controller")
    log.debug "FridgeState: $fridgeState.value"
    if (fridgeState.value.contains("on")){
		sendEvent(name: "controller", value: "off")
		log.debug "WARNING: Turning fridge auto temp control off."

    	def uri = "/api_command/gpio_set_mode/24/out"
    	def uri2 = "/api_command/gpio_set_value/24/1"
		postAction(uri)    
   		postAction(uri2) 
	}
    else {
		sendEvent(name: "controller", value: "on")
    	log.debug "Turning fridge auto temp control on."

		def uri = "/api_command/gpio_set_mode/24/out"
    	def uri2 = "/api_command/gpio_set_value/24/0"
		postAction(uri)    
   		postAction(uri2)    
    }
}


def SW1_on() {
	def SW1State = device.currentState("SW1")
//    log.debug "FridgeState: $fridgeState.value"
    if (SW1State.value.contains("on")){
		sendEvent(name: "SW1", value: "off")
		log.debug "Turning fridge off."
    	def uri = "/api_command/gpio_set_mode/17/out"
    	def uri2 = "/api_command/gpio_set_value/17/0"
        
    	postAction(uri)    
   		postAction(uri2) 
	}
    else {
		sendEvent(name: "SW1", value: "on")
    	log.debug "Turning fridge on."
    	def uri = "/api_command/gpio_set_mode/17/out"
    	def uri2 = "/api_command/gpio_set_value/17/1"
        
		postAction(uri)    
   		postAction(uri2)    
    }
}


def SW1_off() {
	def SW1State = device.currentState("SW1")
    log.debug "SW1_off"
    if (SW1State.value.contains("on")){
		sendEvent(name: "SW1", value: "off")
		log.debug "WARNING: Turning fridge auto temp control off."

    	def uri = "/api_command/gpio_set_mode/17/out"
    	def uri2 = "/api_command/gpio_set_value/17/1"
		postAction(uri)    
   		postAction(uri2) 
	}
    else {
		sendEvent(name: "SW1", value: "on")
//    	log.debug "Turning SW1 on."

		def uri = "/api_command/gpio_set_mode/17/out"
    	def uri2 = "/api_command/gpio_set_value/17/0"
		postAction(uri)    
   		postAction(uri2)    
    }
}

def refresh() {
	//sendEvent(name: "switch", value: "off")
	log.debug "Executing 'refresh'"
	def uri = "/api_command/smartthings"
    postAction(uri)
}

def restart(){
	log.debug "Restart was pressed"
    //sendEvent(name: "switch", value: "off")
    def uri = "/api_command/reboot"
    postAction(uri)
}

// Get CPU percentage reading
private getRPiData() {
	def uri = "/api_command/smartthings"
    postAction(uri)
}

// ------------------------------------------------------------------

private postAction(uri){
  setDeviceNetworkId(ip,port)  
  
  def userpass = encodeCredentials(username, password)
  //log.debug("userpass: " + userpass) 
  
  def headers = getHeader(userpass)
  //log.debug("headders: " + headers) 
  
  def hubAction = new physicalgraph.device.HubAction(
    method: "POST",
    path: uri,
    headers: headers
  )//,delayAction(1000), refresh()]
  log.debug("Executing hubAction on " + getHostAddress())
  //log.debug hubAction
  hubAction    
}

// ------------------------------------------------------------------
// Helper methods
// ------------------------------------------------------------------

def parseDescriptionAsMap(description) {
	description.split(",").inject([:]) { map, param ->
		def nameAndValue = param.split(":")
		map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
	}
}


def toAscii(s){
        StringBuilder sb = new StringBuilder();
        String ascString = null;
        long asciiInt;
                for (int i = 0; i < s.length(); i++){
                    sb.append((int)s.charAt(i));
                    sb.append("|");
                    char c = s.charAt(i);
                }
                ascString = sb.toString();
                asciiInt = Long.parseLong(ascString);
                return asciiInt;
    }

private encodeCredentials(username, password){
	log.debug "Encoding credentials"
	def userpassascii = "${username}:${password}"
    def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    //log.debug "ASCII credentials are ${userpassascii}"
    //log.debug "Credentials are ${userpass}"
    return userpass
}

private getHeader(userpass){
	log.debug "Getting headers"
    def headers = [:]
    headers.put("HOST", getHostAddress())
    headers.put("Authorization", userpass)
    log.debug "Headers are ${headers}"
    return headers
}

private delayAction(long time) {
	new physicalgraph.device.HubAction("delay $time")
}

private setDeviceNetworkId(ip,port){
  	def iphex = convertIPtoHex(ip)
  	def porthex = convertPortToHex(port)
  	device.deviceNetworkId = "$iphex:$porthex"
  	log.trace "Device Network Id set to ${iphex}:${porthex}"
}

private getHostAddress() {
	return "${ip}:${port}"
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    return hex

}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    return hexport
}
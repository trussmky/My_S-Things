/**
 *  Insteon Switch (LOCAL)
 *
 *  Copyright 2014 patrick@patrickstuart.com
 *  Updated 1/4/15 by goldmichael@gmail.com
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
metadata {
	definition (name: "Insteon Switch (LOCAL)", namespace: "michaelgold", author: "patrick@patrickstuart.com/tslagle13@gmail.com/goldmichael@gmail.com") {
		capability "Switch"
		capability "Sensor"
		capability "Actuator"

	}

    preferences {
    input("InsteonIP", "string", title:"Insteon IP Address", description: "Please enter your Insteon Hub IP Address", defaultValue: "192.168.14.103", required: true, displayDuringSetup: true)
    input("InsteonPort", "string", title:"Insteon Port", description: "Please enter your Insteon Hub Port", defaultValue: 25105, required: true, displayDuringSetup: true)
    input("InsteonID", "string", title:"Device Insteon ID", description: "Please enter the devices Insteon ID - numbers only", defaultValue: "dev id", required: true, displayDuringSetup: true)
    input("InsteonHubUsername", "string", title:"Insteon Hub Username", description: "Please enter your Insteon Hub Username", defaultValue: "user" , required: true, displayDuringSetup: true)
    input("InsteonHubPassword", "string", title:"Insteon Hub Password", description: "Please enter your Insteon Hub Password", defaultValue: "pass" , required: true, displayDuringSetup: true)
   }

	simulator {
		// status messages
		status "on": "on/off: 1"
		status "off": "on/off: 0"

		// reply messages
		reply "zcl on-off on": "on/off: 1"
		reply "zcl on-off off": "on/off: 0"
	}

	// UI tile definitions
	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}

		main "switch"
		details "switch"
	}
}

// handle commands
def on() {
	//log.debug "Executing 'take'"
    sendEvent(name: "switch", value: "on")
    def host = InsteonIP

	def path = "/3?0262" + "${InsteonID}" + "0F12FF=I=3"
    log.debug "path is: $path"

	
    def userpassascii = "${InsteonHubUsername}:${InsteonHubPassword}"
	def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def headers = [:] //"HOST:" 
    headers.put("HOST", "$host:$InsteonPort")
    headers.put("Authorization", userpass)
    

    try {
    def hubAction = new physicalgraph.device.HubAction(
    	method: method,
    	path: path,
    	headers: headers
        )  
    }
    catch (Exception e) {
    log.debug "Hit Exception on $hubAction"
    log.debug e
    }
}

def off() {
	//log.debug "Executing 'take'"
    sendEvent(name: "switch", value: "off")
    def host = InsteonIP
  
	def path = "/3?0262" + "${InsteonID}"  + "0F14FF=I=3"
    log.debug "path is: $path"


    def userpassascii = "${InsteonHubUsername}:${InsteonHubPassword}"
	def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def headers = [:] //"HOST:" 
    headers.put("HOST", "$host:$InsteonPort")
    headers.put("Authorization", userpass)

    try {
    def hubAction = new physicalgraph.device.HubAction(
    	method: method,
    	path: path,
    	headers: headers
        )
    }
    catch (Exception e) {
    	log.debug "Hit Exception on $hubAction"
    	log.debug e
    }
}
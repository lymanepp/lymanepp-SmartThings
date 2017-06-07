/**
 *  Virtual Presence
 *
 *  Copyright 2017 Lyman Epp
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
 *  Author: Lyman Epp, Based on work by fuzzysb with thanks
 */
definition(
	name: "Virtual Presence",
	namespace: "lymanepp",
	author: "Lyman Epp",
	description: "Triggers Virtual Presence when HTTP GET Request is received",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section(title: "Select Devices") {
		input "virtualPresence", "capability.presenceSensor", title: "Select Virtual Presence Sensors", required: true, multiple: true
	}
}

def installed() {
	createAccessToken()
	getToken()
	log.debug "Installed Phone with rest api: $app.id"
	log.debug "Installed Phone with token: $state.accessToken"
}

def updated() {
	log.debug "Updated Phone with rest api: $app.id"
	log.debug "Updated Phone with token: $state.accessToken"
}

mappings {
  path("/presence/:sensor/:status") {
	action: [
	  GET: "updatePresence"
	]
  }
}

def updatePresence() {
	log.debug "Presence $params.sensor $params.status"
	virtualPresence.each {
		if (it.name.equalsIgnoreCase(params.sensor)) {
			if (params.status.equalsIgnoreCase("home")) {
				log.debug "Executing arrived for $it.name"
				it.arrived();
			} else {
				log.debug "Executing departed for $it.name"
				it.departed();
			}
		}
	}
}

def getToken() {
	if (!state.accessToken) {
		try {
			getAccessToken()
			log.debug "Creating new Access Token: $state.accessToken"
		} catch (ex) {
			log.debug "Did you enable OAuth in SmartApp IDE settings?"
			log.debug ex
		}
	}
}
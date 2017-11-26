#! /usr/bin/env python

# /****************************************************************************

#  *

#  *   (c) 2009-2016 QGROUNDCONTROL PROJECT <http://www.qgroundcontrol.org>

#  *

#  * QGroundControl is licensed according to the terms in the file

#  * COPYING.md in the root of the source code directory.

#  *

#  ****************************************************************************/





# #include "RTCMMavlink.h"



# #include "MultiVehicleManager.h"

# #include "Vehicle.h"



# #include <cstdio>

class RTCMMavlink():
	def __init__(self):
		self.current_id = 0
		self.callbacks = {}
	
	def RTCMDataUpdate(self,message):
		data = message
		length = len(message)
		self.sendToAll(message)
	
	def sendToAll(self,message):
		for callback_id in self.callbacks:
			callback = self.callbacks[callback_id]
			callback(message)
	
	def registerNewCallback(self,callback):
		id = self.current_id
		self.current_id = self.current_id + 1
		self.callbacks[id] = callback
		return id
	
	def deregisterCallback(self,callback_id):
		self.callbacks.pop(callback_id)
	
	# void RTCMMavlink::sendMessageToVehicle(const mavlink_gps_rtcm_data_t& msg)

	# {

	# 	QmlObjectListModel& vehicles = *_toolbox.multiVehicleManager()->vehicles();

	# 	MAVLinkProtocol* mavlinkProtocol = _toolbox.mavlinkProtocol();

	# 	for (int i = 0; i < vehicles.count(); i++) {

	# 		Vehicle* vehicle = qobject_cast<Vehicle*>(vehicles[i]);

	# 		mavlink_message_t message;

	# 		mavlink_msg_gps_rtcm_data_encode_chan(mavlinkProtocol->getSystemId(),

	#   											mavlinkProtocol->getComponentId(),

	#   											vehicle->priorityLink()->mavlinkChannel(),

	#   											&message,

	#   											&msg);

	# 		vehicle->sendMessageOnLink(vehicle->priorityLink(), message);

	# 	}

	# }
#! /usr/bin/env python

import time
import os
import threading

class FileLogging():
	def __init__(self,drone):
		self.drone = drone
		self.uavid = self.drone.getID()
		if not os.path.exists('./TestLogs'):
			os.makedirs('./TestLogs')
		self.log_filename = './TestLogs/'+self.uavid+'__'+self.get_cur_time_str()+'.log'
		with open(self.log_filename,mode='w') as log_file:
			log_file.write('')
		self.logcont_async()

	def get_cur_time_str(self):
		tim = time.time()
		return time.strftime("%Y_%m_%d__%H-%M-%S",time.localtime(tim))+ '_' + str(int(((tim*1000)%1000)+1000))[1:]

	def log_data(self,items):
		out_str = self.get_cur_time_str()
		for item in items:
			out_str = out_str+'\t'+item
		with open(self.log_filename,mode='a') as log_file:
			log_file.write(out_str+'\n')

	def log_AllDroneInfo(self):
		out = ['AllAttributes']
		AllAttributes = self.drone.getAllAttributes()
		for param in AllAttributes:
			out.append(param)
			out.append(str(AllAttributes[param]))
		self.log_data(out)
		# out = ['vehicle.parameters']
		# for param in vehicle.parameters:
		# 	out.append(param)
		# 	out.append(str(vehicle.parameters[param]))
		# self.log_data(out)

	def log_status(self,status):
		self.log_data(['status',status])

	def logcont(self):
		self.still_logging = True
		period = 0.1
		last_time = time.time() - period
		while self.still_logging:
			this_time = time.time()
			prev_delay = this_time - last_time
			prev_delay_err = period - prev_delay
			this_delay = prev_delay_err*0.9
			if prev_delay_err<=0:
				self.log_AllDroneInfo()
				last_time = last_time + period
			if this_delay>0:
				time.sleep(this_delay)
	
	def logcont_async(self):
		threading.Thread(target=self.logcont).start()

	def shutdown_handler(self,signal,frame):
		print "Stopping automatic logging..."
		self.still_logging = False
		print "Automatic logging stopped!"
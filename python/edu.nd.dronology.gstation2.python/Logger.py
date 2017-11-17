#! /usr/bin/env python

class Logger():
	def __init__(self,classname,parent=None,output_handler=None):
		self.classname = classname
		self.parent = parent
		self.output_handler = output_handler
	
	def log(self,msg_type,message):
		self.outputMessage(msg_type, self.getClasspath(), message)
	
	def setClassname(self,classname):
		self.classname = classname

	def getClasspath(self):
		if self.parent!=None:
			return self.parent.getClasspath() + "." + self.classname
		else:
			return self.classname
	
	def outputMessage(self,msg_type,classpath,message):
		if self.parent!=None:
			self.parent.outputMessage(msg_type,classpath,message)
		elif self.output_handler!=None:
			self.output_handler(msg_type,classpath,message)
		else:
			self.outputMessageStdout(msg_type,classpath,message)
	
	def get_term_color(self,name):
		term_colors = {
			'DEFAULT':	'\x1b[0m',
			'BLACK':	'\x1b[0;30m',
			'RED':		'\x1b[0;31m',
			'GREEN':	'\x1b[0;32m',
			'YELLOW':	'\x1b[0;33m',
			'BLUE':		'\x1b[0;34m',
			'MAGENTA':	'\x1b[0;35m',
			'CYAN':		'\x1b[0;36m',
			'WHITE':	'\x1b[0;37m',
		}
		if name in term_colors:
			return term_colors[name]
		else:
			err_msg = "Unknown terminal color \"{name}\"!".format(name=name)
			self.log("WARN",err_msg)
			return term_colors['DEFAULT']
	
	def get_msg_type_color(self,type):
		msg_type_colors = {
			'ERROR':'RED',
			'INFO':'GREEN',
			'WARN':'YELLOW',
			'DEBUG':'CYAN',
			'MAVLINK':'MAGENTA',
		}
		if type in msg_type_colors:
			return self.get_term_color(msg_type_colors[type])
		else:
			err_msg = "Unknown message type \"{type}\"!".format(type=type)
			self.log("WARN",err_msg)
			return self.get_term_color("DEFAULT")
	
	def getColoredPrint(self,msg_type,classpath,message):
		return "[{type_color}{msg_type}{reset_color}] [{path_color}{classpath}{reset_color}] {message}".format(
			msg_type=msg_type,
			classpath=classpath,
			message=message,
			type_color=self.get_msg_type_color(msg_type),
			path_color=self.get_term_color("BLUE"),
			reset_color=self.get_term_color("DEFAULT"),
		)

	def outputMessageStdout(self,msg_type,classpath,message):
		print self.getColoredPrint(msg_type,classpath,message)

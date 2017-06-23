#! /bin/bash

ps -A | grep arducopter | awk '{print $1}' | xargs kill -9
ps -A | grep py | awk '{print $1}' | xargs kill -9


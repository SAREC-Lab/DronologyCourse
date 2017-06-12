#! /bin/bash

ps -A | grep py | awk '{print $1}' | xargs kill -9


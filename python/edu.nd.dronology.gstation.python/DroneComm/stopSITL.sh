#! /bin/bash

screen -S SITL -X stuff ^C
screen -S mavproxy -X stuff ^C

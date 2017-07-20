#! /bin/bash

export instance=0

if [ ! -z "$1" ]
then
export instance=$1
fi

export arducopterScreenName=ArduCopter_$instance
export mavproxyScreenName=mavproxy_$instance

screen -S $arducopterScreenName -X stuff ^C
screen -S $mavproxyScreenName -X stuff ^C

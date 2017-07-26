#! /bin/bash

export ip=$1
export port=$2
export instance=$3
export ardupath=$4
export speed=$5
export rate=$6
export home=$7

let "masterPORT = 5760 + 10 * $instance"
let   "sitlPORT = 5501 + 10 * $instance"
let  "outPORTa = 14550 + 10 * $instance"
let  "outPORTb = 14551 + 10 * $instance"


export PATH=$PATH:${ardupath}/../jsbsim/src
export PATH=$PATH:${ardupath}/Tools/autotest
export PATH=/usr/lib/ccache:$PATH

mkdir .sitl_temp
cd .sitl_temp

echo -dmS ArduCopter ${ardupath}/build/sitl/bin/arducopter -S -I${instance} --home ${home} --model + --speedup ${speed} --rate ${rate} --defaults ${ardupath}/Tools/autotest/default_params/copter.parm
screen -dmS ArduCopter ${ardupath}/build/sitl/bin/arducopter -S -I${instance} --home ${home} --model + --speedup ${speed} --rate ${rate} --defaults ${ardupath}/Tools/autotest/default_params/copter.parm

echo screen -dmS mavproxy mavproxy.py --master tcp:127.0.0.1:$masterPORT --sitl 127.0.0.1:$sitlPORT --out 127.0.0.1:$outPORTa --out 127.0.0.1:$outPORTb
screen -dmS mavproxy mavproxy.py --master tcp:127.0.0.1:$masterPORT --sitl 127.0.0.1:$sitlPORT --out 127.0.0.1:$outPORTa --out 127.0.0.1:$outPORTb

cd ..
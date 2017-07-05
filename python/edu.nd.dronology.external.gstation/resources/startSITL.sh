#! /bin/bash

export ip=$1
export port=$2
export instance=$3
export ardupath=$4
export speed=$5
export rate=$6
export home=$7

export PATH=$PATH:${ardupath}/../jsbsim/src
export PATH=$PATH:${ardupath}/Tools/autotest
export PATH=/usr/lib/ccache:$PATH

mkdir .sitl_temp
cd .sitl_temp

echo -dmS ArduCopter ../${ardupath}/build/sitl/bin/arducopter -S -I${instance} --home ${home} --model + --speedup ${speed} --rate ${rate} --defaults ../${ardupath}/Tools/autotest/default_params/copter.parm
screen -dmS ArduCopter ../${ardupath}/build/sitl/bin/arducopter -S -I${instance} --home ${home} --model + --speedup ${speed} --rate ${rate} --defaults ../${ardupath}/Tools/autotest/default_params/copter.parm

cd ..
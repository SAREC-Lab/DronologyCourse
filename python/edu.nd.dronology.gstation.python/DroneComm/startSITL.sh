#! /bin/bash

# # start dronekit-sitl in one terminal:
# screen -dmS SITL dronekit-sitl copter --home=41.732955,-86.180886,0,0
# # start mavproxy in another terminal:
# # screen -dmS mavproxy mavproxy.py --master tcp:127.0.0.1:5760 --sitl 127.0.0.1:5501 --out 127.0.0.1:14550 --out 127.0.0.1:14551 --map --console
# sleep 5
# xterm -T mavproxy -e mavproxy.py --master tcp:127.0.0.1:5760 --sitl 127.0.0.1:5501 --out 127.0.0.1:14550 --out 127.0.0.1:14551 --map --console
# # print out the connection string for the drone:
# echo 127.0.0.1:14550




# sleep 4




# #! /bin/bash
# export PATH=$PATH:/home/joshua/link/Fall_2016/cse40773/jsbsim/src
# export PATH=$PATH:/home/joshua/link/Fall_2016/cse40773/ardupilot/Tools/autotest
# export PATH=/usr/lib/ccache:$PATH
# cd /home/joshua/link/Fall_2016/cse40773/ardupilot/ArduCopter
# # xterm -T SITL -e sim_vehicle.py -j4 --map --console -l 41.732955,-86.180886,0,0 &
# xterm -T SITL -e sim_vehicle.py -j4 --map --console -l 41.732955,-86.180886,0,0 -S 100 &

# sleep 10




# export ardupath=/mnt/c/Users/jdhus/Documents/Fall_2016/cse40773/ardupilot
export ardupath=$(<ardupath.conf)
mkdir -p .SITL_workingdir
cd .SITL_workingdir
export instance=0
export speed=1
export rate=10
export home=41.732955,-86.180886,0,0

if [ ! -z "$1" ]
then
export instance=$1
fi
if [ ! -z "$2" ]
then
export home=$2
fi

let "masterPORT = 5760 + 10 * $instance"
let   "sitlPORT = 5501 + 10 * $instance"
let  "outPORTa = 14550 + 10 * $instance"
let  "outPORTb = 14551 + 10 * $instance"

export PATH=$PATH:$ardupath/../jsbsim/src
export PATH=$PATH:$ardupath/Tools/autotest
export PATH=/usr/lib/ccache:$PATH

# $ardupath/Tools/autotest/run_in_terminal_window.sh ArduCopter $ardupath/build/sitl/bin/arducopter -S -I$instance --home $home --model + --speedup $speed --rate $rate --defaults $ardupath/Tools/autotest/default_params/copter.parm
screen -dmS ArduCopter $ardupath/build/sitl/bin/arducopter -S -I$instance --home $home --model + --speedup $speed --rate $rate --defaults $ardupath/Tools/autotest/default_params/copter.parm
# $ardupath/Tools/autotest/run_in_terminal_window.sh mavproxy mavproxy.py --master tcp:127.0.0.1:$masterPORT --sitl 127.0.0.1:$sitlPORT --out 127.0.0.1:$outPORTa --out 127.0.0.1:$outPORTb --map --console
screen -dmS mavproxy mavproxy.py --master tcp:127.0.0.1:$masterPORT --sitl 127.0.0.1:$sitlPORT --out 127.0.0.1:$outPORTa --out 127.0.0.1:$outPORTb --map --console

cd ..


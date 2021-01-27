#cd "$(dirname "$(realpath "$0")")"
REPO=/home/uav/git/ardupilot/ArduCopter

pkill -9 python

gnome-terminal -x bash -c "dronekit-sitl copter --home=41.71480,-86.24187"
gnome-terminal --working-directory=$REPO -x bash -c "python3 ../Tools/autotest/sim_vehicle.py -j4 --map --console"




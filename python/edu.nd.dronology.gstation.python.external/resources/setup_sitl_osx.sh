sudo pip2.7 install dronekit
sudo pip2.7 install dronekit-sitl --ignore-installed six
sudo pip2.7 install pymavlink MAVProxy

sudo pip2.7 install twisted==13.1.0
sudo pip2.7 install service_identity

sudo pip2.7 install console
sudo pip2.7 install map
sudo pip2.7 install wxPython
sudo pip2.7 install wxutils

sudo chown -R $(whoami) /usr/local
brew update
sudo chown root:wheel /usr/local
brew install --python wxmac

git clone git://github.com/ArduPilot/ardupilot.git

cd ardupilot
git submodule init
git submodule update

export ardupath=$(pwd)
export prev_path=$PATH
export PATH=$PATH:$ardupath/../jsbsim/src:$ardupath/Tools/autotest:/usr/lib/ccache
sim_vehicle.py -v ArduCopter -j4 --map --console
export PATH=prev_path

# change to the directory in which GroundStation.py lives
echo ardupath > "/Users/seanbayley/Desktop/git/Dronology/python/edu.nd.dronology.gstation.python/ardupath.conf"


#! /bin/bash

# make sure pip is set-up
sudo apt-get install -y python-requests python-pip
sudo pip install --upgrade pip
# due to a problem with the existing pip package, I had to instead run:
# $ sudo easy_install -U pip

sudo apt-get install -y python-dev

# install SITL and MAVProxy
sudo pip install pymavlink MAVProxy
sudo pip install dronekit-sitl

# used by startSITL.sh
sudo apt-get install -y screen
# used in python for communication with the Java ground station
sudo pip install twisted==13.1.0
sudo pip install service_identity
# used by mavproxy for map and console
sudo pip install console
sudo pip install map
sudo pip install numpy
sudo apt-get install -y gtk+-3.0
sudo apt-get install -y libgtk2.0-dev
sudo apt-get install -y libglw1-mesa libglw1-mesa-dev
sudo apt-get install -y freeglut3 freeglut3-dev
sudo apt-get install -y libgconf2-dev
sudo apt-get install -y libgstreamer1.0-dev
sudo apt-get install -y libgstreamer1.0
sudo apt-get install -y libgstreamer0.10
sudo apt-get install -y gstreamer0.10
sudo apt-get install -y dpkg-dev
sudo apt-get install -y libwebkitgtk-dev
sudo apt-get install -y libjpeg-dev
sudo apt-get install -y libtiff-dev
sudo apt-get install -y libsdl1.2-dev
sudo apt-get install -y libgstreamer-plugins-base0.10-dev
sudo pip install -v wxpython

sudo apt-get install -y python-wxgtk2.8


# install ardupilot
export prevdir=$(pwd)
export ardupilotPARENT=$(pwd) # set to parent directory of desired ardupilot install location
cd $ardupilotPARENT
# download ardupilot from github
git clone git://github.com/ArduPilot/ardupilot.git
# download the dependencies of ardupilot
cd ardupilot
git submodule init
git submodule update
# save path of ardupilot install to variable
export ardupath=$ardupilotPARENT/ardupilot
# run ardupilot sim_vehicle to compile arducopter-quad
cd ArduCopter
export prevPATH=$PATH
export PATH=$PATH:$ardupath/../jsbsim/src
export PATH=$PATH:$ardupath/Tools/autotest
export PATH=/usr/lib/ccache:$PATH
sim_vehicle.py -j4 --map --console
export PATH=$prevPATH
cd ..
cd ..
cd $prevdir

# update ardupilot installation location for startSITL.sh to use
export prevdir=$(pwd)
export runtimeDIR=$(pwd) # set to directory in which GroundStation.py will be run
cd $runtimeDIR
# save ardupath to file
echo $ardupath > ardupath.conf
cd $prevdir

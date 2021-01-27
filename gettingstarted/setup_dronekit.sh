#! /bin/bash
################################ MODIFY REPO PARAMETERS HERE ##############################
# Clone the Dronology Repository
# modify these two pointers if you want to change where the repos should be cloned
# defaults to $HOME/git (e.g., /home/bayley/git)
# will fail and/or result in unexpected behavior if parent directory does not exist or is not an absolute path!
export REPOS_PARENT_DIR=$HOME
export REPOS_DIR=git
################################ MODIFY  REPO PARAMETERS HERE ##############################

# prereqs
export CURR_VER=`lsb_release -rs`
function version_ge() { test "$(echo "$@" | tr " " "\n" | sort -rV | head -n 1)" == "$1"; }

# install python
sudo apt-get install software-properties-common
sudo add-apt-repository ppa:deadsnakes/ppa
sudo apt-get update
sudo apt-get install python2.7
# install pip
sudo apt-get install python-pip
# install git
sudo apt-get install git


cd $REPOS_PARENT_DIR
# makes the directory if it doesn't already exist
mkdir $REPOS_DIR
cd $REPOS_DIR

# Clone the ArduPilot repository.
git clone git://github.com/ArduPilot/ardupilot.git
cd ardupilot
git submodule update --init --recursive

# Install ardupilot dependencies
#if version_ge $CURR_VER '16.0'; then
export WXGTK_VERSION=3.0
#else
#export WXGTK_VERSION=2.8
#fi
echo "Linux Version is: $CURR_VER -- wxgtk $WXGTK_VERSION needs to be installed"
sudo apt-get install python-dev python-opencv python-wxgtk$WXGTK_VERSION python-pip python-matplotlib python-pygame python-lxml
sudo pip install future pymavlink MAVProxy

sudo pip install pyudev
sudo pip install PyYAML 
sudo pip install nvector 
sudo pip install geographiclib
sudo pip install numpy==1.16.6
sudo pip install boltons 
sudo pip install dronekit==2.9.1
sudo pip install dronekit-sitl==3.2.0


# Build SITL
export PATH=$PATH:$REPOS_PARENT_DIR/$REPOS_DIR/ardupilot/Tools/autotest
export PATH=/usr/lib/ccache:$PATH
cd ArduCopter
sim_vehicle.py -w -j4 --map



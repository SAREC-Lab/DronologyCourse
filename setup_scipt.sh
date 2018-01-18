#! /bin/bash

# prereqs

#install maven
sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt-get update
sudo apt install openjdk-8-jdk maven


# install python
sudo apt-get install software-properties-common
sudo add-apt-repository ppa:deadsnakes/ppa
sudo apt-get update
sudo apt-get install python2.7
# install pip
sudo apt-get install python-pip
# install git
sudo apt-get install git

# Clone the Dronology Repository
# modify these two pointers if you want to change where the repos should be cloned
# defaults to $HOME/git (e.g., /home/bayley/git)
# will fail and/or result in unexpected behavior if parent directory does not exist or is not an absolute path!
export REPOS_PARENT_DIR=$HOME
export REPOS_DIR=git

cd $REPOS_PARENT_DIR
# makes the directory if it doesn't already exist
mkdir $REPOS_DIR
cd $REPOS_DIR
git clone https://github.com/SAREC-Lab/DronologyCourse
cd DronologyCourse
git checkout 2018_01_Dronology
cd ..

# Install groundstation dependencies
sudo pip install -r $REPOS_PARENT_DIR/$REPOS_DIR/DronologyCourse/python/edu.nd.dronology.gstation1.python/requirements.txt

# Clone the ArduPilot repository.
git clone git://github.com/ArduPilot/ardupilot.git
cd ardupilot
git submodule update --init --recursive

# Install ardupilot dependencies
sudo apt-get install python-dev python-opencv python-wxgtk3.0 python-pip python-matplotlib python-pygame python-lxml
sudo pip install future pymavlink MAVProxy

# Build SITL
export PATH=$PATH:$REPOS_PARENT_DIR/$REPOS_DIR/ardupilot/Tools/autotest
export PATH=/usr/lib/ccache:$PATH
cd ArduCopter
sim_vehicle.py -w -j4






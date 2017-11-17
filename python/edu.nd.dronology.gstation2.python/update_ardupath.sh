#! /bin/bash

# update ardupilot path
export ardupilotPARENT=$(pwd) # set to parent directory of desired ardupilot install location
# save path of ardupilot install to variable
export ardupath=$ardupilotPARENT/ardupilot

# update ardupilot installation location for startSITL.sh to use
export prevdir=$(pwd)
export runtimeDIR=$(pwd) # set to directory in which GroundStation.py will be run
cd $runtimeDIR
# save ardupath to file
echo $ardupath > ardupath.conf
cd $prevdir

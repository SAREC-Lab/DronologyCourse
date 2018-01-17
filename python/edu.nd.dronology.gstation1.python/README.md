# Dronology Groundstation Setup

These instructions have been tested using Ubuntu 14.04.

### Prerequisites

Ensure you have Python 2.7 installed.

```
python -V
```

You should expect to see "Python 2.7.x" If you don't, [download and install](https://www.python.org/download/releases/2.7/) a copy.

Now make sure [pip](https://pypi.python.org/pypi/pip) is installed. 
```bash
sudo apt-get update
sudo apt-get install python-pip
```
Lastly, make sure [git](https://git-scm.com/) is installed. 
```bash
sudo apt-get install git
```

### Setup

Clone the Dronology repository.
```bash
cd ~
mkdir git
cd git
git clone https://github.com/SAREC-Lab/DronologyCourse.git
```

Clone the ArduPilot repository.
```bash
git clone git://github.com/ArduPilot/ardupilot.git
cd ardupilot
git submodule update --init --recursive
```

Install a few ArduPilot dependencies.
```bash
sudo apt-get install python-dev python-opencv python-wxgtk3.0 python-pip python-matplotlib python-pygame python-lxml
sudo pip install future pymavlink MAVProxy
```
Temporarily add some directories to your search path (to make this permanent, add these lines to ~/.bashrc). _Note: if you did not clone ardpilot into ~/git you will need to modify the first line to point to the correct location._
```bash
export PATH=$PATH:$HOME/git/ardupilot/Tools/autotest
export PATH=/usr/lib/ccache:$PATH
```
Start a simulated vehicle to automatically build SITL. 
```bash
cd ArduCopter
sim_vehicle.py -w -j4
```
Once everything is built you can kill the process (ctrl+c).

Lastly, install all Groundstation dependencies.
```bash
cd ~/git/DronologyCourse/python/edu.nd.dronology.gstation1.python
sudo pip install -r requirements.txt
```

### Configuring Simulations
For now, you configure the vehicles (drones) for a simulation using a JSON configuration file. Some sample configuration files are located in _~/git/DronologyCourse/python/edu.nd.dronology.gstation1.python/cfg/drone_cfgs_ e.g., _default.json_:
```json
[
  {
    "vehicle_type": "VRTL",
    "vehicle_id": null,
    "home": [41.6795175,-86.2525],
    "ip": null
  },
]
```
Each JSON entry corresponds to a single drone. For simulations, the only necessary fields are "vehicle_type" (VRTL) and "home" (the starting latitude and longitude of the drone). 

Before running a simulation, you must also modify the ardupath specified in _~/git/DronologyCourse/python/edu.nd.dronology.gstation1.python/cfg/global_cfg.json_:

```json
{
  "ardupath": "/home/bayley/git/ardupilot"
}
```

This should point to your cloned copy of ArduPilot.


### Running
Once you've [setup and started Dronology](https://github.com/SAREC-Lab/Dronology/blob/master/README.md), you can start the groundstation.
```bash
cd ~/git/DronologyCourse/python/edu.nd.dronology.gstation1.python/src
python main.py -gid mygid -addr localhost -p 1234 -d ../cfg/drone_cfgs/default.json -c ../cfg/global_cfg.json
```
All command line arguments have default values. 

* _gid_ the groundstation id (default: default_ground_station)
* _addr_ the address that dronology is running on (default: localhost)
* _p_ the port that dornology is running on (default: 1234)
* _d_ the path to the drone configuration file (default: ../cfg/drone_cfgs/default.json)
* _c_ the path to the global configuration file (default: ../cfg/global_cfg.json)

# DronologyCourse

The Dronology Project has primarily been funded by the National Science Foundation with the explicit goal of creating a research incubator for Software Engineering research in Cyber Physical Systems.  The project provides a framework for managing and coordinating the flight of both physical and simulated small Unmanned Aerial Systems (sUAS). Project collaborators are committed to not only creating executable, functioning, (flying!), code, but to deliver other artifacts commensurate with a medium-level safety-critical system. This repository houses Dronology releases.

This project is licensed under the terms of a modified MIT license. 

To learn more about the project please visit our website at http://Dronology.info or contact:
<br>Jane Cleland-Huang, University of Notre Dame
<br>Michael Vierhauser, University of Notre Dame
<br>Sean Bayley, University of Notre Dame





## Getting Started

The following directions explain how to setup your computer as a development machine. By the end you should be able to build, test, run, and modify the project.

1. Install a [JDK 8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and [Maven](https://maven.apache.org). Make sure the JDK's bin directory is in your `PATH`. On some platforms, like Windows, you also need to make sure that the `JAVA_HOME` environment variable is set up and pointing to the directory where the JDK is installed. On Windows, you also need to add the JDK's bin directory to your `PATH` variable. Make sure that Maven's bin directory is in your `PATH` too. On Ubuntu you can quickly install a JDK and Maven with:
	```bash
	sudo add-apt-repository ppa:openjdk-r/ppa
	sudo apt-get update
	sudo apt install openjdk-8-jdk maven
	```
	On Ubuntu, using the above command, you don't need to setup environment variables.

1. Clone the project:
   ```bash
   git clone https://github.com/SAREC-Lab/DronologyCourse.git
   cd DronologyCourse
   git checkout 2018_01_Dronology
   ```

1. Build, test, package, and install the project in your local Maven repository:
    ```bash
    (cd /path/to/DronologyCourse)
    mvn install
    ````
	
1. In a terminal, run the Vaadin UI:
    ```bash
    cd /path/to/DronologyCourse/edu.nd.dronology.ui.vaadin
    mvn jetty:run
    ```
    This starts a web server that you will connect to in a later step. This runs until you stop it with Ctrl + C.

1. In a terminal, run Dronology:
    ```bash
    cd /path/to/DronologyCourse/edu.nd.dronology.services.launch
    mvn exec:java
    ```
    This runs until you stop it with Ctrl + C.

1. Open your browser and navigate to the Dronology web UI at [http://localhost:8080/vaadinui](http://localhost:8080/vaadinui).


1. To install and setup the GroundStation and the SITL Simulator continue [here](python/edu.nd.dronology.gstation1.python/README.md)


### Tips and Troubleshooting
* If you plan to work with the source code, consider installing an IDE. Popular IDEs like [Eclipse](https://www.eclipse.org), [Netbeans](https://netbeans.org/downloads/) and [IntelliJ](https://www.jetbrains.com/idea/) all work well with maven projects like this one.

* Sometimes it's necessary to run the project even if tests are failing. To force the Maven install command use:
    ```bash
    mvn install -Dmaven.test.skip=true
    ```

* If your local maven repository gets messed up, you can force maven to re-download dependencies, rebuild everything and install:
    ```bash
    cd /path/to/DronologyCourse
    mvn clean
    cd ~/.m2/repository
    rm -rf *
    cd /path/to/DronologyCourse
    mvn install
    ```

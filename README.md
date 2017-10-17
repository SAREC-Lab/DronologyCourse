# Dronology

## Getting Started

The following directions explain how to setup your computer as a development environment. By the end you should be able to run this project in eclipse and develop contributions.

1. Install a jdk.
  
2. Download and install [eclipse for java EE](https://www.eclipse.org/home/index.php). While it's possible to expand the capabilities of another version of eclipse, the procedure for doing so is beyond the scope of this guide and we will make use of features in eclipse for java EE. Download and use the installer to install the **Eclipse IDE for Java EE Developers**.

3. git clone the project
	```bash
	git clone git@github.com:SAREC-Lab/Dronology.git
	```

4. Import the project into eclipse. In eclipse, Click **File** > **Open Projects from File System**. Click **Directory...** and select the project directory.

5. Install the vaadin tools for eclipse. Click **Help** > **Install New Software...**. Click the **Add...** button and fill in the name: `Vaadin Update Site` and location: `http://vaadin.com/eclipse`. Check the box for Vaadin. Click through the wizard to install the vaadin tools. Restart eclipse when prompted. For more information checkout the [vaadin docs](https://vaadin.com/docs/framework/installing/installing-eclipse.html).

6. Convert the project called `edu.nd.dronology.ui.vaadin` into a maven project. Right click on the project in the **Project Explorer** panel. In the context menu, mouse over the **Configure** sub-menu and select **Convert to Maven Project**. Note that eclipse might have imported the project as a maven project. If this is the case, you will see a **Maven** sub-menu when you right click, and you can skip this step.

7. Install the maven comand line tools. For information on how to do this see the [Maven website](https://maven.apache.org/).

8. Now you need to use maven from within the vaadin project directory. In your terminal:
	```bash
	cd Dronology/edu.nd.dronology.ui.vaadin
	mvn package
	mvn vaadin:compile
	mvn vaadin:update-widgetset
	```

9. In eclipse, right click the project called `edu.nd.dronology.ui.vaadin` in the **Project Explorer**. Click **Properties** in the context menu. Go to the **Deployment and Assembly** section. Click **Add...**. Select **Folder**. Click next. In the next screen select `src > main > libs`. Click Finish. In the deploy path of the newly added source double click on `/` and change the value to `WEB-INF/lib`. Click `Apply and Close`

10. Download and install [Tomcat 9](https://tomcat.apache.org/download-90.cgi). On Linux, here is one possible way to install tomcat. Assuming you downloaded tomcat, in your terminal:
	```bash
	cd ~/Downloads
	tar xf apache-tomcat-9.0.1.tar.gz
	mv apache-tomcat-9.0.1 ~/tomcat-9.0.1
	```
	
11. Setup tomcat as a server in eclipse. Click **Window** > **Show View** > **Servers**. In the newly opened panel, click the link that says: `No servers are available. Click this link to create a new server...` Follow the wizard to setup your tomcat 9 server.

12. Add the vaadin jar to tomcat. Right click on the Tomcat server in the **Servers** view, and select **Add or Remove...**. You should see `edu.nd.dronology.ui.vaadin` as available. Select it and click **Add >**. Then click **Finish**.

13. Select the tomcat server in the **Servers** view of eclipse and press the play button to start the server. Note this may take a few minutes.

14. In the **Project Explorer**, navigate to the project called `edu.nd.dronology.services.launch`, and open `DronologyServiceRunner.java`. Click the play button at the top to run the program. 

15. Open your browser and navigate to the Dronology web UI at [http://localhost:8080/vaadinui](http://localhost:8080/vaadinui).

### Troubleshooting

Here are things for you to try, if the above directions don't work.

* Force maven to re-download dependencies and rebuild. First shutdown tomcat and stop `DronologyServiceRunner.java`. Then in your terminal:
	```bash
	cd ~/.m2/repository
	rm -rf *
	cd Dronology/edu.nd.dronology.ui.vaadin
	mvn clean
	mvn package
	mvn vaadin:compile
	mvn vaadin:update-widgetset
	```
	Then in eclipse refresh the project by right clicking on an empty place in the **Project Explorer** and selecting **Refresh**. Eclipse should rebuild the project. When it's done, start tomcat, re-run `DronologyServiceRunner.java`, and navigate your browser to [http://localhost:8080/vaadinui](http://localhost:8080/vaadinui).

* Try selecting the project called `edu.nd.dronology.ui.vaadin` and clicking **Compile Vaadin Widgetset** and **Compile Vaadin Theme** buttons at the top. Then restart the tomcat server and `DronologyServiceRunner.java`.
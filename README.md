# Dronology

## Getting started

The following directions explain how to setup eclipse and your terminal. By the end you should be able to run the project in eclipse and develop contributions to the project.
  
1. Download and install [eclipse for java EE](https://www.eclipse.org/home/index.php). While it's possible to expand the capabilities of another version of eclipse, the procedure for doing so is beyond the scope of this guide and we will make use of features in eclipse for java EE.

2. git clone the project
```bash
git clone git@github.com:SAREC-Lab/Dronology.git
```

3. Import the project into eclipse. In eclipse, Click **File** > **Open Projects from File System**. Click **Directory...** and select the project directory.

4. Install the vaadin tools for eclipse. Click **Help** > **Install New Software...**. Click the **Add...** button and fill in the name: `Vaadin Update Site` and location: `http://vaadin.com/eclipse`. Check the box for Vaadin. Click through the wizard to install the vaadin tools. Restart eclipse when prompted. For more information checkout the [vaadin docs](https://vaadin.com/docs/framework/installing/installing-eclipse.html).

5. Convert the project called `edu.nd.dronology.ui.vaadin` into a maven project. Right click on the project in the **Project Explorer** panel. In the context menu, mouse over the **Configure** sub-menu and select **Convert to Maven Project**.

6. Install the maven comand line tools. For information on how to do this see the [Maven website](https://maven.apache.org/).

7. Now you need to use maven from within the vaadin project directory. In your terminal:
```bash
cd Dronology/edu.nd.dronology.ui.vaadin
mvn package
mvn vaadin:compile
mvn vaadin:update-widgetset
```

8. In eclipse, right click the project called `edu.nd.dronology.ui.vaadin` in the **Project Explorer**. Click **Properties** in the context menu. Go to the **Deployment and Assembly** section. Click **Add...**. Select **Folder**. Click next. In the next menu navigate to `src > main > libs`. Click Finish. In the deploy path of the newly added source double click on `/` and change the value to `WEB-INF/lib`. Click `Apply and Close`

9. Download and install [Tomcat 9](https://tomcat.apache.org/download-90.cgi). On Linux, here is one possible way to install tomcat. In your terminal:
```bash
cd ~/Downloads
wget http://mirror.cogentco.com/pub/apache/tomcat/tomcat-9/v9.0.1/bin/apache-tomcat-9.0.1.tar.gz
tar xf apache-tomcat-9.0.1.tar.gz
mv apache-tomcat-9.0.1 ~/tomcat-9.0.1
```
10. Setup tomcat as a server in eclipse. Click **Window** > **Show View** > **Servers**. In the newly opened panel, click the link that says: `No servers are available. Click this link to create a new server...` Follow the wizard to setup your tomcat server.

11. Add the vaadin jar to tomcat. Right click on the Tomcat server in the **Servers** view, and select **Add or Remove...**. You should see `edu.nd.dronology.ui.vaadin` as available. Select it and click **Add >**.

12. Select the tomcat server in the **Servers** view of eclipse and press the play button to start the server.

13. In the **Project Explorer**, navigate to the `edu.nd.dronology.services.launch` project, and open `DronologyServiceRunner.java`. Click the play button to start the project 

14. Open your browser and navigate to the Dronology web UI at [http://localhost:8080/vaadinui](http://localhost:8080/vaadinui)
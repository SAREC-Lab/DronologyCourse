# Getting Started with Collision Avoidance

This guide introduces you to the collision avoidance subsystem. We will walk you through creating a simple collision avoidance algorithm, testing it in a simulator, and explaining how to use it with real drones.

We designed the collision avoidance subsystem to be simple for you to use. When you want to create an avoidance algorithm, you can focus on what you want your drones to do, and not how to get them to do it. To get started, we guide you through implementing an example collision avoidance algorithm. We will show you how to work with the collision avoidance subsystem and how we intend for you to use the various programming interfaces.

In this example, we will create a simple avoidance algorithm where we command all drones to hover in place if any two get too close to each other. First, create a class called `StopEveryone` that implements `CollisionAvoider`.

```java
public class StopEveryone implements CollisionAvoider {
  public avoid(ArrayList<DroneSnapshot> drones) {
    ...
  }
}
```

The Collision Avoider interface has one method, called `avoid`, that takes an array list of `DroneSnapshot` objects. You can think of these objects as representing a snap-shot of the current state of real-world drones plus the actions they plan to do next. When creating an avoidance algorithm, it is your responsibility to read the state of each drone and then command them to avoid crashing into each other. You have the final say on what guidance commands get sent to each drone. These commands tell the drone's autopilot what to do. It's crucial for you to remember that the onboard autopilot will blindly follow any commands you give it. So implement `CollisionAvoider` carefully.

Conceptually, you can categorize all the operations you can perform on `DroneSnapshot` objects as operations that let you 1) read sensor data (like GPS) or 2) control the drone.

We will check the distance between each pair of drones, and if we find that two drones are within some threshold distance, we will command all drones stop whatever they're doing and hover in place. We would expect human operators to take over at this point and land each drone individually.

Let's change the code above so that we can check the distance  between each pair:

```java
public class StopEveryone implements CollisionAvoider {
  private double threshold;
  
  public  StopEveryone(double threshold) {
    this.threshold = threshold;
  }
  
  public avoid(ArrayList<DroneSnapshot> drones) {
    for (int i = 0; i < drones.size() - 1; ++i) {
      for (int j = i + 1; j < drones.size(); ++j) {
        if (i != j) {
          if (drones[i].getPosition().distance(drones[j].getPosition()) < this.threshold) {
            this.clearAll(drones);
            return;
          }
        }
      }
    }
  }
  
  private void clearAll(ArrayList<DroneSnapshot> drones) {
    for (int k = 0; k < drones.size(); k++) {
      drones[k].getCommands().clear();
    }
  }
} 
```

//todo show creating tests
//todo show running it in the simulator
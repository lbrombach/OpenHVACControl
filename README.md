# Open HVAC Control

## Description:

A Raspberry Pi based multi-stage, multi-zone HVAC controller. Replace thermostats and control damper motors for up to
two stages heat, two stages cooling, and three zones. User interface is currently via web browser (HTML pages and a
RESTful interface with the back end), but an accompanying Android App is on the todo list. While the system was designed
around a forced air zoned system, it should work fine for zoned hydronic systems by simply using the relays to control
zone valves or pumps instead of the zone dampers. It will work as well for single stage, single zone systems if you
simply play and learn instead of using an off-the-shelf thermostat.

## Purpose/Motivation:

"Multi-stage" means that the furnace or air conditioner is capable of running at either "low" or "high" unlike standard
equipment that can only be "on" or "off." This saves on energy and can contribute to better humidity control for
increased comfort and reduced energy consumption. "Multi-zone" means different areas of the home or building have their
own thermostat, and motorized dampers in ductwork open and close to control the flow of air to the different zones.
(hydronic systems user either motorized valves or control different pumps for different zones).  
<img src="/images/zonedSystem.jpeg" alt="Zone System" width="400" height="360"/>

Commercially available equipment controllers for these systems are available, but very expensive - especially if the
requirement is for both multi-stage AND multi-zone control. The purpose of this project is to reduce the initial expense
of installing such a system, and the result of this project so far has made it financially possible for my customer to
install the system at all. Indeed, version 1 of this controller has been in service for several years, but it was a
crude C++ program with no real interface/information available aside from counting blinks on a status LED. This version
2 is written in Java and uses a REST api to allow for graphical user interface on any device with a web browser.

## Functionalities/features:

- Control up to 3 zones. For each zone:
    - A graphical control display as shown in the image below. From the user's point of view, this is essentially a
      virtual thermostat.  
      <img src="/images/screenshots/zone.png" alt="Zone control image" width="600"/>
    - Control 2 stages heating - Control 2 stages cooling
    - Up to two temperature sensors (these, along with each zone's logic, replace having a thermostat for each zone).
    - in case of primary sensor failure, system will check secondary sensor

- Graphical User Interface
    - Control from any device on network with web browser
    - Home (System overview) page allows for control of all zones
    - System output display so user can tell at a glance what stages are on and what zone dampers are open or closed.

    <img src="/images/screenshots/outputs.png" alt="Outputs display" width="600"/>  

- Emergency operation mode. In event of controller or relay module failure, user can use toggle switches to open zones
  manually and operate as a single-zone system using an off-the-shelf thermostat.

### To do:

- Individual web page for each zone (currently one big overview page)
- Sensor setup interface page (currently have to edit json)
- Zone setup page (currently have to edit json)
- override feature - a button on interface that commands system to use the secondary temperature sensor (allowing for "
  zone-within-a-zone" control of sorts. Useful in case of especially sunny rooms, special purpose rooms that are only
  occasionally used, etc )

## Technologies Used

This is a Spring Boot project built with Maven for dependency management that runs on a Raspberry Pi single board
computer. Written in Java, it makes use of the Pi4J GPIO library (https://pi4j.com/1.2/index.html) which is itself
mostly a wrapper for the WiringPi C++ Raspberry Pi GPIO library (http://wiringpi.com/). Many thanks to Robert Savage and
Daniel Sendula for their PI4J work and saving me a ton of work developing my own, and Gordon for his Wiring Pi library
and support when I was first learning to use a Raspberry Pi.

DS18B20 (one wire protocol) temperature sensors were chosen for the ability to add as many as needed to a single gpio
pin and single, long wire run.  
<img src="/images/sensor_module.jpg" alt="sensor_module display" width="300"/>

## Special Obstacles and Learning Experiences

As a controls engineer and historically back-end programmer, the control system was pretty straightforward, but this
project was as much about getting more familiar with Spring Boot MVC and writing REST APIs as it was about simply
rolling out a new version.

My front end is very plain as my goal was to learn enough HTML and Javascript to simply get it done while spending more
time learning Spring Boot, MVC, and REST APIs. At this I have succeeded, although now that I understand a bit more about
HTML and Javascfipt, some refactoring is in order before I add the new interface feature pages. Additionally, my only
previous experience with a RESTful API project used MYSQL and I had to figure out how to handle passing data back and
forth without calling the database. I think that could certainly be better, and some refactoring is needed there as
well.

## Installation/usage:

Please see the wiki for wiring diagrams, photos, setup, and operating instructions.

(Temporarily putting stuff here until I make public to enable the wiki)

1. Build and wire your system

   1.1 You can use the operating system of your choice, but I used the latest Raspberry Pi OS you can find
   here: https://www.raspberrypi.org/software/
   1.2 set that up as usual, but make sure to use the config program (`sudo raspi-config` from a terminal) make sure you
   select autologin and autoboot to the Desktop GUI. Under interfaces, make sure you enable at least ssh and one-wire.

2. Follow the tutorial here https://pimylifeup.com/raspberry-pi-temperature-sensor/ to learn DS18B20 sensor basics. At
   the least you need to add the dtoverlay to config.txt and get the address numbers for your sensors. Mark them well
   and don't mix them up. Future features of this project will make manually tracking which sensor is which unnecessary
   but you'll still need to add the dtoverlay.

3. Open a terminal on your Rasberry Pi and (preferably from your home directory) clone this repository, cd to the
   OpenHVACControl directory, and build the project with maven:  
   `git clone https://github.com/lbrombach/OpenHVACControl.git`  
   `cd OpenHVACControl`  
   `mvn clean package`  
   This hopefully results in a successful build like the one shown in the image below.  
   <img src="/images/screenshots/buildsuccess.png" alt="buildsuccess.png" width="500"/>

4. The image above shows the executable file you just built, and you can run it with the java -jar command. In my case,
   I execute the file with:  
   `java -jar /home/pi/OpenHVACControl/target/OpenHVACControl-1.0-SNAPSHOT.jar`
   **Note: the file must be run with the command prompt in the OpenHVACControl directory (the root folder that you just
   cloned) in order for it to find setup and configuration files.

5. Until I have a chance to add zone config and sensor setup pages on the web portal/interface, we have to edit JSON
   files that can be found in OpenHVACControl/src/main/resources.

   5.1 In zones.json, you'll find some default/startup setpoints and such like these:
   (mostly you should only need to change the alias, but there are some other options if desired)

    - "name": "zone3"              ## ***DO NOT CHANGE ZONE NAME***
    - "alias": "Basement"          ## okay to change
    - "mode": "HEAT"               ## must be "HEAT" or "COOL" or "OFF"
    - "SECOND_STAGE_DIFF": 3 ## second stage comes on if temp is this many degrees F away from setpoint. Recommend 2 or
      3 - use whole numbers only ( once triggered, second stage does not turn off until first stage turns off)
    - "FIRST_STAGE_DIFF": 2 ## first stage comes on if temperature is this many degrees F away from setpoint. recommend
      1 or 2 - use whole number only.
    - "PROCESS_SP": 70 ## doesn't matter, system should update this on the fly
    - "PROCESS_TEMP": 74 ## doesn't matter, system should update this on the fly
    - "OCCUPIED_SP_HEAT": 70 ## This is the initial setpoint for heating mode (until user changes on interface)
    - "OCCUPIED_SP_COOL": 70 ## This is the initial setpoint for cooling mode (until user changes on interface)
    - "UNOCCUPIED_SP_HEAT": 70 ## this is not yet in use - reserved for adding occupied/away modes and scheduling in the
      future
    - "UNOCCUPIED_SP_COOL": 70 ## this is not yet in use - reserved for adding occupied/away modes and scheduling in the
      future
    - "secondaryIsPrimaryTime": 30 ## for a future feature that enables user to temporarily switch which sensor the zone
      uses for the process temp (main control temp). Minutes - whole numbers only
    - "secondStageTimeDelay": 10 ## the zone will call for a second stage if setpoint is not reached in this time.
      Minutes - whole numbers only
    - "isOccupied": true ## this is not yet in use - reserved for adding occupied/away modes and scheduling in the
      future
    - "isActive": true ## true of false. For disabling a zone if not needed or in case of sensor failure. Not fully
      implemented.
    - "usingSecondarySensor": false ##this may go away. Not in use yet.

   5.2 in tempSensors.json, there are less things to change but you are more likely to want to add or remove sensors. It
   is critical to keep the json format and note change the names. If you add sensors, just add an entry but they must be
   named with the same pattern you see (zoneName+"Primary" OR zoneName+"Secondary"). Addresses must be unique as
   discussed earlier.
    - "name": "zone1Primary" ## ***DO NOT CHANGE NAME***
    - "alias": "Main Floor", ## okay to change
    - "address": "28-0316a3167aff" ##see section above for how to find sensor addresses. At least until I automate this.
    - "sensorOffset": 0, ## This is for calibration. If you notice a sensor always reads 2 degrees high, you want to
      apply an offset of -2 here.
    - "lastTempRead": 0 ## not in use yet. This may go away.

6. Recommend running a simple script at startup. The contents of my startup script are shown below.  
   <img src="/images/startscript.png" alt="startscript.png" width="500"/>

7. With the system configured and started, you can point a web browser on that machine to localhost:8080 for the system
   overview and control page. Here you can change setpoints, modes, and see temperature sensor data as if you were at a
   thermostat for all zones at once. At the bottom, you can also see the system outputs. Below system outputs is a
   button to take you to the future settings page where you'll be able to make changes without messing with the json
   files directly. Currently, this has a little window that should display the controllers IP address if you have it
   connected to a network.  
   <img src="/images/screenshots/ipaddress.png" alt="ipaddress.png"/>  
   This is how the system was designed to be used - a monitor at this controller is totally optional (but recommended)
   and in place of conventional thermostats, a pi zero with a small touchscreen or a cheap tablet pc set up with the web
   browser pointed to this ip address. Don't forget to add port 8080 (I navigate to 192.168.1.38:8080, for example).

***Security Note:*** This works from any device with a browser that is connected to the same network. It will not work from
outside your network and I do not recommend opening your ports to the outside world because I have taken zero security
measures in this project. Keep in mind that anyone on the same network can access these pages without any login, so if
there is anyone using the network that you don't want messing with your system, you might want to get set up a dedicated
router for this system (The cheapest wireless router will do). The only security this system provides at this time is
you controlling access to the network it's connected to.

## System Operation info:

The main control loop is located in MainController.java. This class has one method that handles some setup matters such
as initializing the zones, then loops forever to complete the control sequence. Each zone is responsible for calculating
how many stages of heating or cooling it needs to reach the temperature setpoint the user controls from the interface,
and the controller then checks the requests from all zones to decide whether to set the system to heating, cooling, off,
or run only the fan for circulation. The controller prioritizes calls for heat - only if there are no zones requesting
heat does the controller check if any zones are requesting cooling. Only if no zones are requesting cooling does the
controller check if any zones are requesting fan-only operation. When turning off heating or cooling either due to
reaching setpoint or user turn the mode to "OFF" , there is a delay of about a minute to ensure the system has time to
cool down before closing dampers or processing a new request.

## Contributing, bug reports, etc:

Please use the bug reporting system for bugs and feature requests. With many projects, a job, a family, I can't promise
to get to feature requests very quickly, but am definitely listening for feedback to make improvements. I am open to
pull requests if you'd like to contribute. 


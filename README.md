# Open HVAC Control

## Description:

A Raspberry Pi based multi-stage, multi-zone HVAC controller. Replace thermostats and control damper motors for up to
two stages heat, two stages cooling, and three zones. User interface is currently via web browser (HTML pages and a
RESTful interface with the back end), but an accompanying Android App is on the todo list. While the system was designed
around a forced air zoned system, it should work fine for zoned hydronic systems by simply using the relays to control
zone valves or pumps instead of the zone dampers. It will work as well for single stage, single zone systems if you just
wish to play and learn instead of using an off-the-shelf thermostat.  
<img src="/images/misc/V2_inside.jpg" alt="OpenHVACController" width="400" />
<img src="/images/misc/V2_touchscreen.png" alt="Touchscreen" width="520" />


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
HTML and Javascript, some refactoring is in order before I add the new interface feature pages. Additionally, my only
previous experience with a RESTful API project used MYSQL and I had to figure out how to handle passing data back and
forth without calling the database. I think that could certainly be better, and some refactoring is needed there as
well.

## Hardware/wiring

Please see the wiki for more about the hardware used and wiring diagrams
https://github.com/lbrombach/OpenHVACControl/wiki/Hardware-and-Wiring

## Installation/usage:

Please see the wiki for wiring diagrams, photos, setup, and operating instructions.
https://github.com/lbrombach/OpenHVACControl/wiki/Software-Installation-and-Usage

## System Operation info:

Please see the wiki for operational summary and the control sequence diagram.
https://github.com/lbrombach/OpenHVACControl/wiki/System-Operation-and-Control-Sequence

## Contributing, bug reports, etc:

Please use the bug reporting system for bugs and feature requests. With many projects, a job, a family, I can't promise
to get to feature requests very quickly, but am definitely listening for feedback to make improvements. I am open to
pull requests if you'd like to contribute. 


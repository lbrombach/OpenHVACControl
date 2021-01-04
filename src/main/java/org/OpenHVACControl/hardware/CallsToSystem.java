package org.OpenHVACControl.hardware;

import com.pi4j.io.gpio.Pin;
import org.OpenHVACControl.Zones.Zone;

import java.util.*;

@SuppressWarnings({"ForLoopReplaceableByForEach", "PointlessBooleanExpression"})
public class CallsToSystem {
    private static final int DAMPER_CLOSE_DELAY = 60; //(seconds)
    private static Relay W1;
    private static Relay W2;
    private static Relay Y1;
    private static Relay Y2;
    private static Relay G;


    /**
     * initializes relay objects with their pin assignments and gpiocontrol objects
     * @param w1Pin : Pi4J GPIO pin number
     * @param w2Pin : Pi4J GPIO pin number
     * @param y1Pin : Pi4J GPIO pin number
     * @param y2Pin : Pi4J GPIO pin number
     * @param gPin : Pi4J GPIO pin number
     * @param gpio : PI4J GPIOControl object
     */
    public static void setPins(Pin w1Pin, Pin w2Pin, Pin y1Pin, Pin y2Pin, Pin gPin, GPIOControl gpio) {
        W1 = new Relay(w1Pin, gpio);
        W2 = new Relay(w2Pin, gpio);
        Y1 = new Relay(y1Pin, gpio);
        Y2 = new Relay(y2Pin, gpio);
        G = new Relay(gPin, gpio);
    }

    /**
     * fetches current states of each relay as a list to send to front end
     * eliminate when I figure out how to get a hashmap to the front end javascript and unpack it
     * @return : List of system outputs (which stages of heating/cooling/fan are on or off)
     */
    public static List<Boolean> getRelayStates() {
        return new ArrayList<>(Arrays.asList(W1.getState(), W2.getState(), Y1.getState(), Y2.getState(), G.getState()));
    }

    /**
     * fetches current states of each relay as a hashmap for use in main control logic
     * @return : hashmap of system outputs (which stages of heating/cooling/fan are on or off)
     */
    public static Map<String, Boolean> getRelayStatesMap() {
        Map<String, Boolean> states = new HashMap<>();
        states.put("W1", W1.getState());
        states.put("W2", W2.getState());
        states.put("Y1", Y1.getState());
        states.put("Y2", Y2.getState());
        states.put("G", G.getState());
        return states;
    }


    /**
     * starts stage one heat. opens dampers on zones calling for heat and closes all others
     * @param zones : a list of Zone objects the system is managing
     */
    public static void startStage1Heat(List<Zone> zones) {
        for (int i = 0; i < zones.size(); i++) {
            if (zones.get(i).heatStagesRequested() > 0) {
                zones.get(i).openDamper();
            } else {
                zones.get(i).closeDamper();
            }
        }
        W1.setRelayOn();
    }

    /**
     * starts seconds stage of heating
     */
    public static void startStage2Heat() {
        W2.setRelayOn();
    }

    /**
     * stops second stage of heat
     */
    public static void turnOffStage2Heat() {
        W2.setRelayOff();
    }

    /**
     * turns off heating and closes dampers not needed for the next call
     * closes dampers with delay to allow heat exchanger cooldown
     * @param zones : a list of Zone objects the system is managing
     */
    public static void turnHeatingOff(List<Zone> zones) {
        W1.setRelayOff();
        W2.setRelayOff();        
        System.out.println("Stopping heating. System may appear to be unresponsive for a minute while closing dampers");
        try {
            Thread.sleep(DAMPER_CLOSE_DELAY * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < zones.size(); i++) {
            if (zones.get(i).coolStagesRequested() == 0 && zones.get(i).isFanRequested() == false) {
                zones.get(i).closeDamper();
            }
        }
    }

    /**
     * starts stage one cooling. opens dampers on zones calling for cool and closes all others
     * @param zones : a list of Zone objects the system is managing
     */
    public static void startStage1Cool(List<Zone> zones) {
        for (int i = 0; i < zones.size(); i++) {
            if (zones.get(i).coolStagesRequested() > 0) {
                zones.get(i).openDamper();
            } else {
                zones.get(i).closeDamper();
            }
        }
        Y1.setRelayOn();
        G.setRelayOn();
    }

    /**
     * starts seconds stage of cooling
     */
    public static void startStage2Cool() {
        Y2.setRelayOn();
    }

    /**
     * stops second stage of cooling
     */
    public static void turnOffStage2Cool() {
        Y2.setRelayOff();
    }

    /**
     * turns off cooling and closes dampers not needed for the next call
     * @param zones : a list of Zone objects the system is managing
     */
    public static void turnCoolingOff(List<Zone> zones) {
        Y1.setRelayOff();
        Y2.setRelayOff();        
        System.out.println("Stopping cooling. System may appear to be unresponsive for a minute while closing dampers");
        try {
            Thread.sleep(DAMPER_CLOSE_DELAY * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < zones.size(); i++) {
            if (zones.get(i).heatStagesRequested() == 0 && zones.get(i).isFanRequested() == false) {
                zones.get(i).closeDamper();
            }
        }
        G.setRelayOff();
    }

    /**
     * starts the furnace fan and opens dampers for zones calling for fan only. Closes all others.
     * @param zones : a list of Zone objects the system is managing
     */
    public static void startFanOnly(List<Zone> zones) {
        for (int i = 0; i < zones.size(); i++) {
            if (zones.get(i).isFanRequested() == true) {
                zones.get(i).openDamper();
            } else {
                zones.get(i).closeDamper();
            }
        }
        G.setRelayOn();
    }

    /**
     * turns fan off and closes dampers not needed for the next system call
     * @param zones : a list of Zone objects the system is managing
     */
    public static void turnFanOff(List<Zone> zones) {
        G.setRelayOff();
        for (int i = 0; i < zones.size(); i++) {
            if (zones.get(i).heatStagesRequested() == 0 && zones.get(i).coolStagesRequested() == 0) {
                zones.get(i).closeDamper();
            }
        }
    }

}

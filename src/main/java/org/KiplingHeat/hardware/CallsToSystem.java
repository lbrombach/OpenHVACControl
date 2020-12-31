package org.KiplingHeat.hardware;

import com.pi4j.io.gpio.Pin;
import org.KiplingHeat.Zones.Zone;
import org.KiplingHeat.hardware.Relay;

import java.util.*;

public class CallsToSystem {
    private static final int DAMPER_CLOSE_DELAY = 3; //30 (seconds)
    private static Relay W1;
    private static Relay W2;
    private static Relay Y1;
    private static Relay Y2;
    private static Relay G;

    enum SystemStates {OFF, HEATING, COOLING, FAN_ONLY}

    private CallsToSystem() {
    }

    public CallsToSystem(Pin w1Pin, Pin w2Pin, Pin y1Pin, Pin y2Pin, Pin gPin, GPIOControl gpioControl) {
        W1 = new Relay(w1Pin, gpioControl);
        W2 = new Relay(w2Pin, gpioControl);
        Y1 = new Relay(y1Pin, gpioControl);
        Y2 = new Relay(y2Pin, gpioControl);
        G = new Relay(gPin, gpioControl);
    }

    public static List<Boolean> getRelayStates() {
        return new ArrayList<Boolean>(Arrays.asList(W1.getState(), W2.getState(), Y1.getState(), Y2.getState(), G.getState()));
    }

    public static Map<String, Boolean> getRelayStatesMap() {
        Map states = new HashMap();
        states.put("W1", W1.getState());
        states.put("W2", W2.getState());
        states.put("Y1", Y1.getState());
        states.put("Y2", Y2.getState());
        states.put("G", G.getState());
        return states;
    }

    public static SystemStates getSystemState() {
        if (W1.getState() || W2.getState()) {
            return SystemStates.HEATING;
        } else if (Y1.getState() || Y2.getState()) {
            return SystemStates.COOLING;
        } else if ((G.getState())) {
            return SystemStates.FAN_ONLY;
        }
        return SystemStates.OFF;
    }


    public static void turnHeatingOff(List<Zone> zones) {
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

        W1.setRelayOff();
        W2.setRelayOff();
    }

    public static void turnCoolingOff(List<Zone> zones) {
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

        Y1.setRelayOff();
        Y2.setRelayOff();
        G.setRelayOff();
    }

    public static void turnFanOff(List<Zone> zones) {
        G.setRelayOff();
        for (int i = 0; i < zones.size(); i++) {
            if (zones.get(i).heatStagesRequested() == 0 && zones.get(i).coolStagesRequested() == 0) {
                zones.get(i).closeDamper();
            }
        }
    }

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

    public static void startStage2Heat() {
        W2.setRelayOn();
    }

    public static void turnOffStage2Heat() {
        W2.setRelayOff();
    }

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

    public static void startStage2Cool() {
        Y2.setRelayOn();
    }

    public static void turnOffStage2Cool() {
        Y2.setRelayOff();
    }

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

}

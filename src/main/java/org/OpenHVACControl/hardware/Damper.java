package org.OpenHVACControl.hardware;

import com.pi4j.io.gpio.Pin;

public class Damper{

    private Pin pin;
    private Relay relay;

    private Damper() {

    }

    public Damper(Pin damperPin, GPIOControl gpio){
        this();
        pin = damperPin;
        relay = new Relay(pin, gpio);
        relay.setRelayOff();
    }

    /**
     * Assumes the dampers are wired such that they are open when the relay is coil is energized
     * @return : is the damper open
     */
    public boolean isOpen() {
        return relay.getState();
    }

    /**
     * Assumes the dampers are wired such that they are open when the relay is coil is energized
     */
    public void open() {
        relay.setRelayOn();
    }

    /**
     * Assumes the dampers are wired such that they are open when the relay is coil is energized
     */
    public void close() {
        relay.setRelayOff();
    }

}

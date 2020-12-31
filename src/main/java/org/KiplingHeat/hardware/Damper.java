package org.KiplingHeat.hardware;

import com.pi4j.io.gpio.Pin;

public class Damper extends Thread{

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

    public void setPinNumber(Pin pinNumber) {
        pin = pinNumber;
    }

    //this will reflect relay state, not simply pin state because relays are active low
    public boolean isOpen() {
        return relay.getState();
    }

    public void open() {
        relay.setRelayOn();
    }

    public void close() {
        relay.setRelayOff();
    }

/*
    public void closeWithDelay(int seconds) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis()-startTime < seconds*1000){
            try {
                sleep(1000, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        close();
    }
*/
}

package org.KiplingHeat.hardware;

import com.pi4j.io.gpio.*;

public class Relay{

    private Pin pin;
    GPIOControl gpio;
    GpioPinDigitalOutput outputPin;
    boolean isActive;

    private Relay(){}

    /**
     * general use constructor.
     * @param pin the calling function should pass a PIN of enum type RaspiPin. For example a pin declared like:
     *            private static final Pin pinName = RaspiPin.GPIO_12; **don't forget that Pi4J uses different pin numbers
     * @param gpio only one GPIOControl object should be instantiated for the entire program.
     */
    public Relay(Pin pin, GPIOControl gpio){
        this.pin = pin;
        this.gpio = gpio;
        //initializing HIGH sets relay off (active low relays)
        outputPin = gpio.gpio.provisionDigitalOutputPin(pin, "Relay Pin", PinState.HIGH);
    }


    /**
     * dont forget to set that relay is active if this pin is low...cuz active low relays
     */
    public void setRelayOn() {
        outputPin.low();
        isActive = true;
    }

    /**
     * dont forget to set that relay is not active if this pin is true...cuz active low relays
     */
    public void setRelayOff() {
        outputPin.high();
        isActive = false;
    }

    /**
     * isActive == true indicates relay coil is energized
     * @return
     */
    public boolean getState() {
        return isActive;
    }
}

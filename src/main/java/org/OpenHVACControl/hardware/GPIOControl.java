package org.OpenHVACControl.hardware;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

public class GPIOControl{

    public GpioController gpio;

    /**
     * no more than one of these should be created in a program
     */
    public GPIOControl() {
        gpio = GpioFactory.getInstance();
    }

}

package org.OpenHVACControl.Sensors;


public class InactiveSensorException extends Exception{
    public InactiveSensorException() {}

    //Constructor that accepts a message
    public InactiveSensorException(String message)
    {
        super(message);
    }
}



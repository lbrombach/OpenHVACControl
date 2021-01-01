package org.OpenHVACControl.Sensors;


public class InactiveZoneException extends Exception{
    public InactiveZoneException() {}

    //Constructor that accepts a message
    public InactiveZoneException(String message)
    {
        super(message);
    }
}
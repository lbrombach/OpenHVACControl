package org.KiplingHeat.Sensors;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

public class TempSensorTest extends TestCase {
    TempSensor sensor;


    @Test
    public void testBadName() {
        try {
            sensor = new TempSensor("ThisNameDoesNotExist");
            fail("should have thrown exception");
        } catch (InactiveSensorException e) {
            //this is passing
        }

    }


    @Test
    public void test() throws InactiveSensorException {
        sensor = new TempSensor("TEST");
        String testString = "TempSensor{name='TEST', alias='test', address='28-456A123654xxx', sensorOffset=2, gpio=null, lastReadTime=null, lastTempRead=0}";
        assertEquals(testString,sensor.toString());
    }


}
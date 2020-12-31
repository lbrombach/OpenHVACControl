package org.KiplingHeat.Zones;

import junit.framework.TestCase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

import static org.mockito.Mockito.when;

@SpringBootTest
public class ZoneUtilsTest extends TestCase {

    private HashMap<Zone.Temps, Integer> temps = new HashMap<Zone.Temps, Integer>(16);

    public void setUp() throws Exception {
        super.setUp();
        temps.put(Zone.Temps.PROCESS_TEMP, 70);
        temps.put(Zone.Temps.PROCESS_SP, 68);
        temps.put(Zone.Temps.OCCUPIED_SP_HEAT, 74);
        temps.put(Zone.Temps.OCCUPIED_SP_COOL, 65);
        temps.put(Zone.Temps.UNOCCUPIED_SP_HEAT, 68);
        temps.put(Zone.Temps.UNOCCUPIED_SP_COOL, 78);
        temps.put(Zone.Temps.FIRST_STAGE_DIFF, 2);
        temps.put(Zone.Temps.SECOND_STAGE_DIFF, 3);
    }


    public void testGetProcessSetpoint() {
        int output = ZoneUtils.getProcessSetpoint(Zone.Mode.HEAT, temps, true);
        assertEquals(74, output);
        output = ZoneUtils.getProcessSetpoint(Zone.Mode.HEAT, temps, false);
        assertEquals(68, output);
        output = ZoneUtils.getProcessSetpoint(Zone.Mode.COOL, temps, true);
        assertEquals(65, output);
        output = ZoneUtils.getProcessSetpoint(Zone.Mode.COOL, temps, false);
        assertEquals(78, output);
    }
/*
    public void testGetCoolingStages() {
        temps.replace(Zone.Temps.PROCESS_SP, 70);
        temps.replace(Zone.Temps.PROCESS_TEMP, 69);
        int output = ZoneUtils.getCoolingStages(temps,5, System.currentTimeMillis() - 299999);
        assertEquals(0, output);

        temps.replace(Zone.Temps.PROCESS_TEMP, 70);
        output = ZoneUtils.getCoolingStages(temps,5, System.currentTimeMillis() - 299999);
        assertEquals(0, output);

        temps.replace(Zone.Temps.PROCESS_TEMP, 71);
        output = ZoneUtils.getCoolingStages(temps,5, System.currentTimeMillis() - 299999);
        assertEquals(0, output);

        temps.replace(Zone.Temps.PROCESS_TEMP, 73);
        output = ZoneUtils.getCoolingStages(temps,5, System.currentTimeMillis() - 299999);
        assertEquals(2, output);

        temps.replace(Zone.Temps.PROCESS_TEMP, 72);
        output = ZoneUtils.getCoolingStages(temps,5, System.currentTimeMillis() - 299999);
        assertEquals(1, output);

        temps.replace(Zone.Temps.PROCESS_TEMP, 72);
        output = ZoneUtils.getCoolingStages(temps,5, System.currentTimeMillis() - 300000);
        assertEquals(2, output);
    }

    public void testGetHeatingStages() {
        temps.replace(Zone.Temps.PROCESS_SP, 70);
        temps.replace(Zone.Temps.PROCESS_TEMP, 71);
        int output = ZoneUtils.getHeatingStages(temps,5, System.currentTimeMillis() - 299999);
        assertEquals(0, output);

        temps.replace(Zone.Temps.PROCESS_TEMP, 70);
        output = ZoneUtils.getHeatingStages(temps,5, System.currentTimeMillis() - 299999);
        assertEquals(0, output);

        temps.replace(Zone.Temps.PROCESS_TEMP, 69);
        output = ZoneUtils.getHeatingStages(temps,5, System.currentTimeMillis() - 299999);
        assertEquals(0, output);

        temps.replace(Zone.Temps.PROCESS_TEMP, 67);
        output = ZoneUtils.getHeatingStages(temps,5, System.currentTimeMillis() - 299999);
        assertEquals(2, output);

        temps.replace(Zone.Temps.PROCESS_TEMP, 68);
        output = ZoneUtils.getHeatingStages(temps,5, System.currentTimeMillis() - 299999);
        assertEquals(1, output);

        temps.replace(Zone.Temps.PROCESS_TEMP, 68);
        output = ZoneUtils.getHeatingStages(temps,5, System.currentTimeMillis() - 300000);
        assertEquals(2, output);

    }
*/
    public void testMinutesElapsed() {
        assertEquals(4, ZoneUtils .minutesElapsed(System.currentTimeMillis()-299999));
        assertEquals(5, ZoneUtils .minutesElapsed(System.currentTimeMillis()-300000));
        assertEquals(5, ZoneUtils .minutesElapsed(System.currentTimeMillis()-359999));
    }
}

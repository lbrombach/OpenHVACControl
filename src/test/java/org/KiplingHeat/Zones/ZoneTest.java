package org.KiplingHeat.Zones;

import com.pi4j.io.gpio.RaspiPin;
import junit.framework.TestCase;
import org.KiplingHeat.hardware.GPIOControl;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;

@SpringBootTest
public class ZoneTest extends TestCase {
    Zone zone;
    private HashMap<Zone.Temps, Integer> temps = new HashMap<Zone.Temps, Integer>(16);

    @Override
    public void setUp() throws Exception {
        zone = new Zone("TEST", new GPIOControl());
        temps.put(Zone.Temps.PROCESS_TEMP, -999);
        zone.setTemp(Zone.Temps.PROCESS_SP, 70);
        zone.setTemp(Zone.Temps.OCCUPIED_SP_HEAT, 70);
        zone.setTemp(Zone.Temps.OCCUPIED_SP_COOL, 70);
        zone.setTemp(Zone.Temps.UNOCCUPIED_SP_HEAT, 70);
        zone.setTemp(Zone.Temps.UNOCCUPIED_SP_COOL, 70);
        zone.setTemp(Zone.Temps.FIRST_STAGE_DIFF, 2);
        zone.setTemp(Zone.Temps.SECOND_STAGE_DIFF, 3);

     }

    @Test
    public void testGetRequest() throws Exception {
        zone.setMode(Zone.Mode.HEAT);
        zone.setLastValidSensorRead(System.currentTimeMillis());
        zone.setTemp(Zone.Temps.PROCESS_TEMP, 66);
        Request req = zone.getRequest();
        assertEquals(0, req.getCoolingStages());
        assertEquals(2, req.getHeatingStages());

        zone.setMode(Zone.Mode.COOL);
        zone.setTemp(Zone.Temps.PROCESS_TEMP, 71);
        req = zone.getRequest();
        assertEquals(0, req.getCoolingStages());
        assertEquals(0, req.getHeatingStages());

        zone.setTemp(Zone.Temps.PROCESS_TEMP, 72);
        req = zone.getRequest();
        assertEquals(1, req.getCoolingStages());
        assertEquals(0, req.getHeatingStages());

        zone.setTemp(Zone.Temps.PROCESS_TEMP, 73);
        req = zone.getRequest();
        assertEquals(2, req.getCoolingStages());
        assertEquals(0, req.getHeatingStages());

        zone.setMode(Zone.Mode.OFF);
        req = zone.getRequest();
        assertEquals(0, req.getCoolingStages());
        assertEquals(0, req.getHeatingStages());

        zone.setTemp(Zone.Temps.PROCESS_TEMP, 65);
        req = zone.getRequest();
        assertEquals(0, req.getCoolingStages());
        assertEquals(0, req.getHeatingStages());

    }
}
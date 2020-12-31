package org.KiplingHeat;

import junit.framework.TestCase;
import org.KiplingHeat.Zones.Request;
import org.KiplingHeat.Zones.Zone;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@SpringBootTest
public class MainControllerUtilsTest extends TestCase {

    Request zone1Req = new Request(1,2,false);
    Request zone2Req = new Request(0,2,true);
    Request zone3Req = new Request(0,0,true);
    List<Request> requests = new ArrayList<>(Arrays.asList(zone1Req, zone2Req, zone3Req));


    @Test
    public void testGetZonesRequestingHeat() {
        Request totalReq = MainControllerUtils.totalRequests(requests);
        assertEquals(1, totalReq.getHeatingStages());
        assertEquals(4, totalReq.getCoolingStages());
        assertEquals(true, totalReq.isFanRequested());
    }

    public void testGetAllRequests() {
    }

    public void testTotalRequests() {

    }

    public void testIsCallingSecondStageHeat() {
    }

    public void testIsCallingSecondStageCool() {
    }

    public void testInitializeZones() {
    }

    public void testValidateZones() {
    }
}
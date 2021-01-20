package org.OpenHVACControl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pi4j.io.gpio.Pin;
import org.OpenHVACControl.Zones.Request;
import org.OpenHVACControl.Zones.Zone;
import org.OpenHVACControl.hardware.GPIOControl;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainControllerUtils {

    /**
     * calls each zone to update it's own request object and compiles a list of them for use in main controller
     * @param zones : a list of Zone objects the system is managing
     * @return list of requests from all zones
     */
    public static List<Request> getAllRequests(List<Zone> zones) {
        List<Request> requests = new ArrayList<>();
        for (int i = 0; i < zones.size(); i++) {
            requests.add(zones.get(i).getRequest());
        }
        return requests;
    }

    /**
     * Combines list of requests into a single request used by main control logic to manage system outputs
     * @param requests : a list of request objects from each zone
     * @return request object
     */
    public static Request totalRequests(List<Request> requests) {

        Request total = new Request();

        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getHeatingStages() == 1 && total.getHeatingStages() != 2) {
                total.setHeatingStages(1);
            } else if (requests.get(i).getHeatingStages() == 2) {
                total.setHeatingStages(2);
            }

            if (requests.get(i).getCoolingStages() == 1 && total.getCoolingStages() != 2) {
                total.setCoolingStages(1);
            } else if (requests.get(i).getCoolingStages() == 2) {
                total.setCoolingStages(2);
            }

            if (requests.get(i).isFanRequested() == true) {
                total.setFanRequest(true);
            }
        }
        return total;
    }

    /**
     * main control loop calls this to update each zone with the
     * latest settings fetched from the front end
     * @param setpoints : a list of setpoints user has each zone set to
     * @param modes : a list of modes user has each zone set to
     * @param fansRequests : a list of fan on/auto settings user has each zone set to
     * @param zones : a list of Zone objects the system is managing
     */


    /**
     * Called once during startup. Passes pin assignments and gpio control object
     * to zones for their own initialization
     * @param ZONE_1_DAMPER_PIN  : Pi4J GPIO pin number for this damper relay
     * @param ZONE_2_DAMPER_PIN  : Pi4J GPIO pin number for this damper relay
     * @param ZONE_3_DAMPER_PIN  : Pi4J GPIO pin number for this damper relay
     * @param gpio  : PI4J GPIOControl object
     * @return
     * @throws Exception
     */
    public static List<Zone> initializeZones(Pin ZONE_1_DAMPER_PIN, Pin ZONE_2_DAMPER_PIN,
                                             Pin ZONE_3_DAMPER_PIN, GPIOControl gpio) throws Exception {
        List<Zone> zones;
        try {
            Zone zone1 = new Zone(ZONE_1_DAMPER_PIN, "zone1", gpio);
            //  Zone  zone1 = new Zone("zone1", gpio);

            System.out.println("XXXXXXXXXXXXXXXXX");

            Zone zone2 = new Zone(ZONE_2_DAMPER_PIN, "zone2", gpio);
            //   Zone zone2 = new Zone("zone2", gpio);
            System.out.println("XXXXXXXXXXXXXXXXX");

            Zone zone3 = new Zone(ZONE_3_DAMPER_PIN, "zone3", gpio);
            //  Zone  zone3 = new Zone("zone3", gpio);
            zones = new ArrayList<>(Arrays.asList(zone1, zone2, zone3));
        } catch (Exception e) {
            zones = new ArrayList<>();
            zones.add(new Zone("Error", gpio));
            e.printStackTrace();
        }
        return zones;
    }

    /**
     * zones should name themselves "error" if they can't find themselves in the json file.
     * this checks that all zones are properly initialized
     * @param zones : a list of Zone objects the system is managing
     * @return
     */
    public static boolean validateZones(List<Zone> zones) {
        for (int i = 0; i < zones.size(); i++) {
            if (zones.get(i).getName().equals("Error"))
                return false;
        }
        return true;
    }

    public static boolean saveZoneJson(List<Zone> zones){
        try (Writer outFile = new FileWriter("src/main/resources/zones.json")) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            gson.toJson(zones, outFile);
            outFile.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

}

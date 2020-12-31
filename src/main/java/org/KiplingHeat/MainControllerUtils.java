package org.KiplingHeat;

import com.pi4j.io.gpio.Pin;
import org.KiplingHeat.Zones.Request;
import org.KiplingHeat.Zones.Zone;
import org.KiplingHeat.hardware.GPIOControl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainControllerUtils {

    enum SystemStatus {OFF, HEATING, COOLING, FAN_ON}

    public static List<Zone> getZonesRequestingHeat(List<Zone> zones) {
        List<Zone> zonesCalling = new ArrayList<Zone>();
        for (int i = 0; i < zones.size(); i++) {
            zones.get(i).getRequest();
            if (zones.get(i).heatStagesRequested() > 0) {
                zonesCalling.add(zones.get(i));
            }
        }
        return zonesCalling;
    }


    public static List<Request> getAllRequests(List<Zone> zones) {
        List<Request> requests = new ArrayList<Request>();
        for (int i = 0; i < zones.size(); i++) {
            requests.add(zones.get(i).getRequest());
        }
        return requests;
    }

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

    public static boolean isCallingSecondStageHeat(List<Request> requests) {
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getHeatingStages() >= 2) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCallingSecondStageCool(List<Request> requests) {
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getCoolingStages() >= 2) {
                return true;
            }
        }
        return false;
    }

    public static boolean getIsFanOnlyRequested(List<Request> requests) {
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).isFanRequested() == true) {
                return true;
            }
        }
        return false;
    }


    public static void updateFromFrontEnd(List<Integer> setpoints, List<String> modes,
                                          List<Boolean> fansRequests, List<Zone> zones) {
        for (int i = 0; i < zones.size(); i++) {
            zones.get(i).setTemp(Zone.Temps.OCCUPIED_SP_HEAT, setpoints.get(i));
            zones.get(i).setTemp(Zone.Temps.OCCUPIED_SP_COOL, setpoints.get(i));
            zones.get(i).setTemp(Zone.Temps.UNOCCUPIED_SP_HEAT, setpoints.get(i + 3));
            zones.get(i).setTemp(Zone.Temps.UNOCCUPIED_SP_COOL, setpoints.get(i + 3));

            zones.get(i).setFanRequest(fansRequests.get(i));

            switch (modes.get(i)) {
                case "HEAT":
                    zones.get(i).setMode(Zone.Mode.HEAT);
                    break;

                case "COOL":
                    zones.get(i).setMode(Zone.Mode.COOL);
                    break;

                case "OFF":
                    zones.get(i).setMode(Zone.Mode.OFF);
            }
        }
    }


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

    public static boolean validateZones(List<Zone> zones) {
        for (int i = 0; i < zones.size(); i++) {
            if (zones.get(i).getName() == "Error")
                return false;
        }
        return true;
    }

}

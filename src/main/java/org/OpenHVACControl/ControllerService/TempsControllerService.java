package org.OpenHVACControl.ControllerService;

import org.OpenHVACControl.Zones.Zone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TempsControllerService {

    private static int zone1PrimaryTemp = 70;
    private static int zone2PrimaryTemp = 72;
    private static int zone3PrimaryTemp = 74;
    private static int zone1SecondaryTemp = -999;
    private static int zone2SecondaryTemp = 73;
    private static int zone3SecondaryTemp = -999;
    private static int zone1UsingTemp = 70;
    private static int zone2UsingTemp = 72;
    private static int zone3UsingTemp = 74;

    //set each update in MainController() while loop

    /**
     * MainController() calls this to update the most recent sensor and process temperatures
     * @param zones : list of zones
     */
    public static void setTemps(List<Zone> zones) {

            zone1PrimaryTemp = zones.get(0).getTemp(Zone.Temps.CURRENT_PRIMARY_TEMP);
            zone2PrimaryTemp = zones.get(1).getTemp(Zone.Temps.CURRENT_PRIMARY_TEMP);
            zone3PrimaryTemp = zones.get(2).getTemp(Zone.Temps.CURRENT_PRIMARY_TEMP);
            zone1SecondaryTemp = zones.get(0).getTemp(Zone.Temps.CURRENT_SECONDARY_TEMP);
            zone2SecondaryTemp = zones.get(1).getTemp(Zone.Temps.CURRENT_SECONDARY_TEMP);
            zone3SecondaryTemp = zones.get(2).getTemp(Zone.Temps.CURRENT_SECONDARY_TEMP);
            zone1UsingTemp = zones.get(0).getTemp(Zone.Temps.PROCESS_TEMP);
            zone2UsingTemp = zones.get(1).getTemp(Zone.Temps.PROCESS_TEMP);
            zone3UsingTemp = zones.get(2).getTemp(Zone.Temps.PROCESS_TEMP);

    }


    /**
     * front end call this to retrieve list of sensor and process temperatures
     * @return
     */
    public static List<Integer> getTemps() {
        return new ArrayList<>(Arrays.asList(zone1PrimaryTemp, zone2PrimaryTemp, zone3PrimaryTemp,
                zone1SecondaryTemp, zone2SecondaryTemp, zone3SecondaryTemp,
                zone1UsingTemp, zone2UsingTemp, zone3UsingTemp));
    }


}

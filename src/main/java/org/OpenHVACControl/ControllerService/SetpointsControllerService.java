package org.OpenHVACControl.ControllerService;

import com.google.common.util.concurrent.Monitor;
import org.OpenHVACControl.Zones.Zone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SetpointsControllerService {

    private static final Monitor monitor = new Monitor();

    private static int zone1OccupiedSP = 71;
    private static int zone2OccupiedSP = 72;
    private static int zone3OccupiedSP = 73;
    private static int zone1UnoccupiedSP = 74;
    private static int zone2UnoccupiedSP = 74;
    private static int zone3UnoccupiedSP = 74;

    /**
     * sets initial setpoints during initialization. Called once by MainController()
     *
     * @param zones : list of zones (that have been initialized with data zones.json file)
     */
    public static void initializeSetpoints(List<Zone> zones) {
        monitor.enter();
        try {
            zone1OccupiedSP = zones.get(0).getTemp(Zone.Temps.OCCUPIED_SP_HEAT);
            zone2OccupiedSP = zones.get(1).getTemp(Zone.Temps.OCCUPIED_SP_HEAT);
            zone3OccupiedSP = zones.get(2).getTemp(Zone.Temps.OCCUPIED_SP_HEAT);
            zone1UnoccupiedSP = zones.get(0).getTemp(Zone.Temps.UNOCCUPIED_SP_HEAT);
            zone2UnoccupiedSP = zones.get(1).getTemp(Zone.Temps.UNOCCUPIED_SP_HEAT);
            zone3UnoccupiedSP = zones.get(2).getTemp(Zone.Temps.UNOCCUPIED_SP_HEAT);
        } finally {
            monitor.leave();
        }
    }

    /**
     * MainController() calls this every cycle to get the most recent setpoints
     *
     * @return : list of setpoints
     */
    @SuppressWarnings("UnusedAssignment")
    public static List<Integer> getSetpoints() {
        List<Integer> setpoints = null;
        monitor.enter();
        try {
            setpoints = new ArrayList<>(Arrays.asList(zone1OccupiedSP, zone2OccupiedSP, zone3OccupiedSP,
                    zone1UnoccupiedSP, zone2UnoccupiedSP, zone3UnoccupiedSP));
        }
        finally {
            monitor.leave();
        }
        return setpoints;
    }

    /**
     * front end uses to increment the setpoint for the zone passed in
     *
     * @param zoneNum : The number of the zone
     * @return : the new setpoint
     */
    public static Integer increaseSP(int zoneNum) {
        monitor.enter();
        try {
            switch (zoneNum) {
                case 1:
                    zone1OccupiedSP++;
                    return zone1OccupiedSP;
                case 2:
                    zone2OccupiedSP++;
                    return zone2OccupiedSP;
                case 3:
                    zone3OccupiedSP++;
                    return zone3OccupiedSP;
            }
        } finally {
            monitor.leave();
        }
        return zone1OccupiedSP;
    }

    /**
     * front end uses to decrement the setpoint for the zone passed in
     *
     * @param zoneNum : The number of the zone
     * @return : the new setpoint
     */
    public static Integer decreaseSP(int zoneNum) {
        monitor.enter();
        try {
            switch (zoneNum) {
                case 1:
                    zone1OccupiedSP--;
                    return zone1OccupiedSP;
                case 2:
                    zone2OccupiedSP--;
                    return zone2OccupiedSP;
                case 3:
                    zone3OccupiedSP--;
                    return zone3OccupiedSP;
            }
        }
        finally {
            monitor.leave();
        }
        return zone1OccupiedSP;
    }

}

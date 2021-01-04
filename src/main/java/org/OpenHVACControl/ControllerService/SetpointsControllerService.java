package org.OpenHVACControl.ControllerService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetpointsControllerService {

    private static int zone1OccupiedSP = 71;
    private static int zone2OccupiedSP = 72;
    private static int zone3OccupiedSP = 73;
    private static int zone1UnoccupiedSP = 74;
    private static int zone2UnoccupiedSP = 74;
    private static int zone3UnoccupiedSP = 74;

    /**
     * MainController() calls this to get the most recent setpoints
     * @return : list of setpoints
     */
    public static List<Integer> getSetpoints(){
        return new ArrayList<>(Arrays.asList(zone1OccupiedSP, zone2OccupiedSP, zone3OccupiedSP,
                zone1UnoccupiedSP, zone2UnoccupiedSP, zone3UnoccupiedSP));
    }

    /**
     * front end uses to increment the setpoint for teh zone passed in
     * @param zoneNum : The number of the zone
     * @return : the new setpoint
     */
    public static Integer increaseSP(int zoneNum){
        switch(zoneNum){
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
        return zone1OccupiedSP;
    }

    /**
     * front end uses to decrement the setpoint for teh zone passed in
     * @param zoneNum : The number of the zone
     * @return : the new setpoint
     */
    public static Integer decreaseSP(int zoneNum){
        switch(zoneNum){
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
        return zone1OccupiedSP;
    }

}

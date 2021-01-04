package org.OpenHVACControl.ControllerService;

import org.OpenHVACControl.Zones.Zone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ZoneDataControllerService {

    //parallel Lists of zone data, where i = zone number -1 (zone 1 = list[0], etc)
    static List<String> aliases = new ArrayList<>(Arrays.asList("", "", ""));
    static List<String> modes = new ArrayList<>(Arrays.asList("", "", ""));
    static List<Boolean> fansRequested = new ArrayList<>(Arrays.asList(false, false, false));


    public static List<String> getAliases(){
        return aliases;
    }

    /**
     * Used to get most recent mode settings
     * @return : list of modes
     */
    public static List<String> getModes(){
        return modes;
    }

    /**
     * Used to get most recent fan requests
     * @return : list of fan requests
     */
    public static List<Boolean> getFanRequests(){
        return fansRequested;
    }


    /**
     * MainController() uses this during initialization to set initial modes and aliases
     * @param zones : list of zones
     */
    public static void sendZoneDatatoFrontEnd(List<Zone> zones){

        for (int i = 0; i < zones.size(); i++) {
            aliases.set(i, zones.get(i).getAlias());

            switch(zones.get(i).getMode()){
                case HEAT:
                    modes.set(i, "HEAT");
                    break;

                case COOL:
                    modes.set(i, "COOL");
                    break;

                case OFF:
                    modes.set(i, "OFF");
                    break;
            }
        }
    }


    /**
     * Front end uses to keep modes updated
     * @param zoneNum : which zone to update
     * @param newMode : the new mode - must be "HEAT" "COOL" or "OFF"
     */
    public static void setMode(Integer zoneNum, String newMode) {
        modes.set(zoneNum-1, newMode);
    }

    /**
     * Front end uses to keep fan requests updated
     * @param zoneNum
     */
    public static void setFanRequests(Integer zoneNum) {
        fansRequested.set(zoneNum-1, !fansRequested.get(zoneNum-1));
    }

}

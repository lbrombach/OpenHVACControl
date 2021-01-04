package org.OpenHVACControl.ControllerService;

import com.google.common.util.concurrent.Monitor;
import org.OpenHVACControl.Zones.Zone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "UnusedAssignment"})
public class ZoneDataControllerService {

    private static final Monitor monitor = new Monitor();
    //parallel Lists of zone data, where i = zone number -1 (zone 1 = list[0], etc)
    static List<String> aliases = new ArrayList<>(Arrays.asList("", "", ""));
    static List<String> modes = new ArrayList<>(Arrays.asList("", "", ""));
    static List<Boolean> fansRequested = new ArrayList<>(Arrays.asList(false, false, false));


    /**
     * Used to get zone aliases
     * @return : list of aliases
     */
    public static List<String> getAliases() {
        List<String> aliasList = null;
        monitor.enter();
        try {
            aliasList = aliases;
        }
        finally {
            monitor.leave();
        }
        return aliasList;
    }

    /**
     * Used to get most recent mode settings
     *
     * @return : list of modes
     */
    public static List<String> getModes() {
        List<String> modesList = null;
        monitor.enter();
        try {
            modesList = modes;
        }
        finally {
            monitor.leave();
        }
        return modesList;
    }

    /**
     * Used to get most recent fan requests
     *
     * @return : list of fan requests
     */
    public static List<Boolean> getFanRequests() {
        List<Boolean> fansRequestedList = null;
        monitor.enter();
        try {
            fansRequestedList = fansRequested;
        }
        finally {
            monitor.leave();
        }
        return fansRequestedList;

    }


    /**
     * MainController() uses this during initialization to set initial modes and aliases
     *
     * @param zones : list of zones
     */
    public static void sendZoneDatatoFrontEnd(List<Zone> zones) {

        monitor.enter();
        try {
            for (int i = 0; i < zones.size(); i++) {
                aliases.set(i, zones.get(i).getAlias());

                switch (zones.get(i).getMode()) {
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
        finally {
            monitor.leave();
        }
    }


    /**
     * Front end uses to keep modes updated
     *
     * @param zoneNum : which zone to update
     * @param newMode : the new mode - must be "HEAT" "COOL" or "OFF"
     */
    public static void setMode(Integer zoneNum, String newMode) {
        monitor.enter();
        try {
            modes.set(zoneNum - 1, newMode);
        }
        finally {
            monitor.leave();
        }
    }

    /**
     * Front end uses to keep fan requests updated
     *
     * @param zoneNum the number designation of the zone to apply change to (ie: 1 for zone1, 2 for zone2, etc)
     */
    public static void setFanRequests(Integer zoneNum) {
        monitor.enter();
        try {
            fansRequested.set(zoneNum - 1, !fansRequested.get(zoneNum - 1));
        }
        finally {
            monitor.leave();
        }
    }

}

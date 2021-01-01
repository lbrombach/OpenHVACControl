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
    
    public static List<String> getModes(){
        return modes;
    }

    public static List<Boolean> getFanRequests(){
        return fansRequested;
    }

    
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

    //this comes from front end to set variable here. Back end need to call and apply getModes() for this to take effect
    public static void setMode(Integer zoneNum, String newMode) {
        modes.set(zoneNum-1, newMode);
    }

    //this comes from front end to set variable here. Back end need to call and apply getFanRequests() for this to take effect
    public static void setFanRequests(Integer zoneNum) {
        fansRequested.set(zoneNum-1, !fansRequested.get(zoneNum-1));
    }

}

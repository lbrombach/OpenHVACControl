package org.OpenHVACControl.ControllerService;

import com.google.common.util.concurrent.Monitor;
import com.google.gson.Gson;
import org.OpenHVACControl.Zones.Zone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "UnusedAssignment"})
public class ZoneDataControllerService {

    //parallel Lists of zone data, where i = zone number -1 (zone 1 = list[0], etc)
    static List<String> aliases = new ArrayList<>(Arrays.asList("", "", ""));
    static List<String> modes = new ArrayList<>(Arrays.asList("", "", ""));
    static List<Boolean> fansRequested = new ArrayList<>(Arrays.asList(false, false, false));
    static List<Integer> firstStageDiffs = new ArrayList<>(Arrays.asList(1, 1, 1));
    static List<Integer> secondStageDiffs = new ArrayList<>(Arrays.asList(1, 1, 1));

    static private boolean hasChanged = false;
    static public boolean getHasChanged(){return hasChanged;}
    static public void resetHasChanged(){hasChanged = false;}
    static private boolean needsSave = false;
    static public boolean getNeedsSave(){return needsSave;}
    static public void resetNeedsSave(){needsSave = false;}


    public static class ZoneSettings {
        String name;
        String alias;
        String mode;
        String OCCUPIEDHEAT;
        String UNOCCUPIEDHEAT;
        String OCCUPIEDCOOL;
        String UNOCCUPIEDCOOL;
        String FIRST_STAGE_DIFF;
        String SECOND_STAGE_DIFF;
        String PRIMARYTEMP;
        String SECONDARYTEMP;
        String PROCESSTEMP;
        String PROCESSSETPOINT;
        String fansRequested;
        String isOccupied;
    }

    static List<ZoneSettings> zoneSettings = new ArrayList<>(Arrays.asList(new ZoneSettings(), new ZoneSettings(), new ZoneSettings()));

    /**
     * MainController() calls this every cycle to get the most recent setpoints
     *
     * @return : list of setpoints
     */
    @SuppressWarnings("UnusedAssignment")
    public static synchronized List<Integer> getSetpoints() {
        List<Integer> setpoints = new ArrayList<>();

            for (int i = 0; i < zoneSettings.size(); i++) {
                switch (zoneSettings.get(i).mode) {
                    case "HEAT":
                        if (Boolean.parseBoolean(zoneSettings.get(i).isOccupied)){
                            setpoints.add(Integer.parseInt(zoneSettings.get(i).OCCUPIEDHEAT));
                        }
                        else {
                            setpoints.add(Integer.parseInt(zoneSettings.get(i).UNOCCUPIEDHEAT));
                        }
                        break;
                    case "COOL":
                        if (Boolean.parseBoolean(zoneSettings.get(i).isOccupied)){
                            setpoints.add(Integer.parseInt(zoneSettings.get(i).OCCUPIEDCOOL));
                        }
                        else {
                            setpoints.add(Integer.parseInt(zoneSettings.get(i).UNOCCUPIEDCOOL));
                        }
                        break;
                    case "OFF":
                        setpoints.add(-999);

                }

            }
        return setpoints;
    }


    /**
     * front end uses to increment the setpoint for the zone passed in
     *
     * @param zoneNum     : The number of the zone
     * @param newSetpoint : The new setpoint
     * @return : the new setpoint
     */
    public static synchronized Integer setSP(int zoneNum, int newSetpoint) {
            int i = zoneNum-1;
            switch (zoneSettings.get(i).mode) {
                case "HEAT":
                    if (Boolean.parseBoolean(zoneSettings.get(i).isOccupied)){
                        zoneSettings.get(i).OCCUPIEDHEAT = String.valueOf(newSetpoint);
                    }
                    else {
                        zoneSettings.get(i).UNOCCUPIEDHEAT = String.valueOf(newSetpoint);
                    }
                    hasChanged = true;
                    return newSetpoint;
                case "COOL":
                    if (Boolean.parseBoolean(zoneSettings.get(i).isOccupied)){
                        zoneSettings.get(i).OCCUPIEDCOOL = String.valueOf(newSetpoint);
                    }
                    else {
                        zoneSettings.get(i).UNOCCUPIEDCOOL = String.valueOf(newSetpoint);
                    }
                    hasChanged = true;
                    return newSetpoint;
            }
        return -999;
    }


    /**
     * front end uses to increment the setpoint for the zone passed in
     *
     * @param zoneNum : The number of the zone
     * @return : the new setpoint
     */
    public static synchronized Integer increaseSP(int zoneNum) {
        int i = zoneNum-1;
        switch (zoneSettings.get(i).mode) {
            case "HEAT":
                if (Boolean.parseBoolean(zoneSettings.get(i).isOccupied)){
                    zoneSettings.get(i).OCCUPIEDHEAT = String.valueOf(Integer.parseInt(zoneSettings.get(i).OCCUPIEDHEAT)+1);
                    hasChanged = true;
                    return Integer.parseInt(zoneSettings.get(i).OCCUPIEDHEAT);
                }
                else {
                    zoneSettings.get(i).UNOCCUPIEDHEAT = String.valueOf(Integer.parseInt(zoneSettings.get(i).UNOCCUPIEDHEAT)+1);
                    hasChanged = true;
                    return Integer.parseInt(zoneSettings.get(i).UNOCCUPIEDHEAT);
                }

            case "COOL":
                if (Boolean.parseBoolean(zoneSettings.get(i).isOccupied)){
                    zoneSettings.get(i).OCCUPIEDCOOL = String.valueOf(Integer.parseInt(zoneSettings.get(i).OCCUPIEDCOOL)+1);
                    hasChanged = true;
                    return Integer.parseInt(zoneSettings.get(i).OCCUPIEDCOOL);
                }
                else {
                    zoneSettings.get(i).UNOCCUPIEDCOOL = String.valueOf(Integer.parseInt(zoneSettings.get(i).UNOCCUPIEDCOOL)+1);
                    hasChanged = true;
                    return Integer.parseInt(zoneSettings.get(i).UNOCCUPIEDCOOL);
                }
        }
        return -999;
    }

    /**
     * front end uses to increment the setpoint for the zone passed in
     *
     * @param zoneNum : The number of the zone
     * @return : the new setpoint
     */
    public static synchronized Integer decreaseSP(int zoneNum) {
        int i = zoneNum-1;
        switch (zoneSettings.get(i).mode) {
            case "HEAT":
                if (Boolean.parseBoolean(zoneSettings.get(i).isOccupied)){
                    zoneSettings.get(i).OCCUPIEDHEAT = String.valueOf(Integer.parseInt(zoneSettings.get(i).OCCUPIEDHEAT)-1);
                    hasChanged = true;
                    return Integer.parseInt(zoneSettings.get(i).OCCUPIEDHEAT);
                }
                else {
                    zoneSettings.get(i).UNOCCUPIEDHEAT = String.valueOf(Integer.parseInt(zoneSettings.get(i).UNOCCUPIEDHEAT)-1);
                    hasChanged = true;
                    return Integer.parseInt(zoneSettings.get(i).UNOCCUPIEDHEAT);
                }

            case "COOL":
                if (Boolean.parseBoolean(zoneSettings.get(i).isOccupied)){
                    zoneSettings.get(i).OCCUPIEDCOOL = String.valueOf(Integer.parseInt(zoneSettings.get(i).OCCUPIEDCOOL)-1);
                    hasChanged = true;
                    return Integer.parseInt(zoneSettings.get(i).OCCUPIEDCOOL);
                }
                else {
                    zoneSettings.get(i).UNOCCUPIEDCOOL = String.valueOf(Integer.parseInt(zoneSettings.get(i).UNOCCUPIEDCOOL)-1);
                    hasChanged = true;
                    return Integer.parseInt(zoneSettings.get(i).UNOCCUPIEDCOOL);
                }
        }
        return -999;
    }

    /**
     * Used to get zone aliases
     *
     * @return : list of aliases
     */
    public static synchronized List<String> getAliases() {
        return aliases;
    }

    /**
     * Used to get zone first or second stage differentials
     *
     * @param stage : which stage diffs to return
     * @return : list of first stage differentials
     */
    public static synchronized List<Integer> getStageDiffs(int stage) {
        List<Integer> diffList = null;
            diffList = (stage == 1) ? firstStageDiffs : secondStageDiffs;
        return diffList;
    }

    /**
     * Used to get most recent mode settings
     *
     * @return : list of modes
     */
    public static synchronized List<String> getModes() {
        return modes;
    }

    /**
     * Used to get most recent fan requests
     *
     * @return : list of fan requests
     */
    public static synchronized List<Boolean> getFanRequests() {
        return fansRequested;
    }


    /**
     * MainController() uses this during initialization to set initial modes and aliases
     *
     * @param zones : list of zones
     */
    public static synchronized void setZoneSettings(List<Zone> zones) {

            for (int i = 0; i < zones.size(); i++) {
                zoneSettings.get(i).name = zones.get(i).getName();
                zoneSettings.get(i).alias = zones.get(i).getAlias();
                zoneSettings.get(i).OCCUPIEDHEAT =  Integer.toString(zones.get(i).getTemp(Zone.Temps.OCCUPIED_SP_HEAT));
                zoneSettings.get(i).UNOCCUPIEDHEAT =  Integer.toString(zones.get(i).getTemp(Zone.Temps.UNOCCUPIED_SP_HEAT));
                zoneSettings.get(i).OCCUPIEDCOOL =  Integer.toString(zones.get(i).getTemp(Zone.Temps.OCCUPIED_SP_COOL));
                zoneSettings.get(i).UNOCCUPIEDCOOL =  Integer.toString(zones.get(i).getTemp(Zone.Temps.UNOCCUPIED_SP_COOL));
                zoneSettings.get(i).FIRST_STAGE_DIFF =  Integer.toString(zones.get(i).getTemp(Zone.Temps.FIRST_STAGE_DIFF));
                zoneSettings.get(i).SECOND_STAGE_DIFF =  Integer.toString(zones.get(i).getTemp(Zone.Temps.SECOND_STAGE_DIFF));
                zoneSettings.get(i).PRIMARYTEMP =  Integer.toString(zones.get(i).getTemp(Zone.Temps.CURRENT_PRIMARY_TEMP));
                zoneSettings.get(i).SECONDARYTEMP =  Integer.toString(zones.get(i).getTemp(Zone.Temps.CURRENT_SECONDARY_TEMP));
                zoneSettings.get(i).PROCESSTEMP =  Integer.toString(zones.get(i).getTemp(Zone.Temps.PROCESS_TEMP));
                zoneSettings.get(i).PROCESSSETPOINT =  Integer.toString(zones.get(i).getTemp(Zone.Temps.PROCESS_SP));
                zoneSettings.get(i).fansRequested =  Boolean.toString(zones.get(i).isFanRequested());
                zoneSettings.get(i).isOccupied =  Boolean.toString(zones.get(i).isOccupied());
                zoneSettings.get(i).mode = zones.get(i).getMode().name();


                //methods using these below should be migrated to use zoneSettings object
                aliases.set(i, zones.get(i).getAlias());
                firstStageDiffs.set(i, zones.get(i).getTemp(Zone.Temps.FIRST_STAGE_DIFF));
                secondStageDiffs.set(i, zones.get(i).getTemp(Zone.Temps.SECOND_STAGE_DIFF));
                modes.set(i, zones.get(i).getMode().name());
            }
    }

    /**
     * get the list of zones and their settings for the front end
     * (all values will be strings - not appropriate for updating back end)
     * @return
     */
    public static synchronized List<String> getZoneSettings()
    {
        List<String> settingsList = new ArrayList<>();
        for (int i = 0; i < zoneSettings.size() ; i++) {
                switch (zoneSettings.get(i).mode) {
                    case "HEAT":
                        if (Boolean.parseBoolean(zoneSettings.get(i).isOccupied)){
                            zoneSettings.get(i).PROCESSSETPOINT = zoneSettings.get(i).OCCUPIEDHEAT;
                        }
                        else {
                            zoneSettings.get(i).PROCESSSETPOINT = zoneSettings.get(i).UNOCCUPIEDHEAT;
                        }
                        break;
                    case "COOL":
                        if (Boolean.parseBoolean(zoneSettings.get(i).isOccupied)){
                            zoneSettings.get(i).PROCESSSETPOINT = zoneSettings.get(i).OCCUPIEDCOOL;
                        }
                        else {
                            zoneSettings.get(i).PROCESSSETPOINT = zoneSettings.get(i).UNOCCUPIEDCOOL;
                        }
                        break;
                    case "OFF":
                        zoneSettings.get(i).PROCESSSETPOINT = "-999";

                }
            settingsList.add(new Gson().toJson(zoneSettings.get(i)));
        }
        return settingsList;
    }



    /**
     * get a single zone's settings
     * (all values will be strings - not appropriate for updating back end)
     * @param zoneNum : the zone number
     * @return : the zone's data from the list, where the index = zoneNum-1
     */
    public static synchronized String getZoneSettings(int zoneNum) {
        getZoneSettings();
        return new Gson().toJson(zoneSettings.get(zoneNum-1));
    }

    /**
     * back end uses to update back end zone settings
     * converts String data member to appropriate type for back end
     * @param zones : list of all zones to be updated
     */
    public static synchronized void updateZoneSettings(List<Zone> zones){
        for(int i =0; i<zones.size();i++){
            //get sensor data from back end while we're here
            zoneSettings.get(i).PRIMARYTEMP =  Integer.toString(zones.get(i).getTemp(Zone.Temps.CURRENT_PRIMARY_TEMP));
            zoneSettings.get(i).SECONDARYTEMP =  Integer.toString(zones.get(i).getTemp(Zone.Temps.CURRENT_SECONDARY_TEMP));
            zoneSettings.get(i).PROCESSTEMP =  Integer.toString(zones.get(i).getTemp(Zone.Temps.PROCESS_TEMP));            
            
            //get zone settings from front end
            zones.get(i).setAlias(zoneSettings.get(i).alias);
            zones.get(i).setTemp(Zone.Temps.OCCUPIED_SP_HEAT, Integer.parseInt(zoneSettings.get(i).OCCUPIEDHEAT));
            zones.get(i).setTemp(Zone.Temps.UNOCCUPIED_SP_HEAT, Integer.parseInt(zoneSettings.get(i).UNOCCUPIEDHEAT));
            zones.get(i).setTemp(Zone.Temps.OCCUPIED_SP_COOL, Integer.parseInt(zoneSettings.get(i).OCCUPIEDCOOL));
            zones.get(i).setTemp(Zone.Temps.UNOCCUPIED_SP_COOL, Integer.parseInt(zoneSettings.get(i).UNOCCUPIEDCOOL));
            zones.get(i).setTemp(Zone.Temps.FIRST_STAGE_DIFF, Integer.parseInt(zoneSettings.get(i).FIRST_STAGE_DIFF));
            zones.get(i).setTemp(Zone.Temps.SECOND_STAGE_DIFF, Integer.parseInt(zoneSettings.get(i).SECOND_STAGE_DIFF));
            zones.get(i).setFanRequest(Boolean.parseBoolean(zoneSettings.get(i).fansRequested));
            zones.get(i).setOccupied (Boolean.parseBoolean(zoneSettings.get(i).isOccupied));
            zones.get(i).setMode(Zone.Mode.valueOf(zoneSettings.get(i).mode));
        }
    }

    public static synchronized Boolean applySettings(String zoneData) {
        Gson gsonObj = new Gson();
        ZoneSettings newZoneSettings = gsonObj.fromJson(zoneData, ZoneSettings.class);
        for (int  i = 0;  i < zoneSettings.size();  i++) {
            if(newZoneSettings.name.equals(zoneSettings.get(i).name)){

                //these don't come with applySettings(call and must be preserved)
                String mode = zoneSettings.get(i).mode;
                String isOccupied = zoneSettings.get(i).isOccupied;
                String fansRequested = zoneSettings.get(i).fansRequested;

                zoneSettings.set(i, newZoneSettings);

                zoneSettings.get(i).mode = mode;
                zoneSettings.get(i).isOccupied = isOccupied;
                zoneSettings.get(i).fansRequested = fansRequested;
                hasChanged = true;
                needsSave = true;
                return true;
            }
        }
        return false;
    }

    /**
     * Front end uses to keep modes updated
     *
     * @param zoneNum : which zone to update
     * @param newMode : the new mode - must be "HEAT" "COOL" or "OFF"
     */
    public static synchronized void setMode(Integer zoneNum, String newMode) {
            zoneSettings.get(zoneNum - 1).mode=newMode;
            modes.set(zoneNum - 1, newMode);

            hasChanged = true;
    }

    /**
     * Front end uses to keep fan requests updated
     *
     * @param zoneNum the number designation of the zone to apply change to (ie: 1 for zone1, 2 for zone2, etc)
     */
    public static synchronized void setFanRequests(Integer zoneNum) {
            if(Boolean.parseBoolean(zoneSettings.get(zoneNum - 1).fansRequested) ){
                zoneSettings.get(zoneNum - 1).fansRequested = "false";
            }
            else{
                zoneSettings.get(zoneNum - 1).fansRequested = "true";
            }
            fansRequested.set(zoneNum - 1, !fansRequested.get(zoneNum - 1));
            hasChanged = true;
    }

    public static synchronized void setOccupancy(int zoneNum, String isOccupied){
        zoneSettings.get(zoneNum-1).isOccupied = isOccupied;
        hasChanged = true;
    }


}

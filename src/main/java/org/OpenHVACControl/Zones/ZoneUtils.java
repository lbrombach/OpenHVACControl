package org.OpenHVACControl.Zones;

import org.OpenHVACControl.ControllerService.SetpointsControllerService;
import org.OpenHVACControl.Sensors.TempSensor;

import java.util.HashMap;
import java.util.List;

class ZoneUtils {

    /**
     * is the value an error code
     *
     * @param temp : the temperature value to be checked
     * @return : is it an error. -999 indicates a problem communicating with sensor, 185 indicates value returned by sensor no good
     */
    static boolean isError(int temp) {
        return (temp == 185 || temp == -999);
    }

    /**
     * updates the zone's temperature data hashmap with fresh sensor readings
     *
     * @param primary      : the zone's primary temperature sensor object
     * @param secondary    : the zone's secondary sensor object
     * @param temps        : the zone's hashmap of temperature values
     * @param hasSecondary : did the system find a secondary sensor during zone initialization
     */
    static void getSensorData(TempSensor primary, TempSensor secondary,
                              HashMap<Zone.Temps, Integer> temps,
                              boolean hasSecondary) {

        temps.replace(Zone.Temps.CURRENT_PRIMARY_TEMP, (int) primary.getTemp());

        if (hasSecondary) {
            temps.replace(Zone.Temps.CURRENT_SECONDARY_TEMP, (int) secondary.getTemp());
        }
        temps.replace(Zone.Temps.PROCESS_TEMP, temps.get(Zone.Temps.CURRENT_PRIMARY_TEMP));

        if (isError(temps.get(Zone.Temps.PROCESS_TEMP))) {
            //text or email or alert of sensor problem hand attempting to use alternate sensor here
            if (temps.get(Zone.Temps.PROCESS_TEMP) == temps.get(Zone.Temps.CURRENT_PRIMARY_TEMP) && hasSecondary) {
                temps.replace(Zone.Temps.PROCESS_TEMP, temps.get(Zone.Temps.CURRENT_SECONDARY_TEMP));
            } else if (temps.get(Zone.Temps.PROCESS_TEMP) == temps.get(Zone.Temps.CURRENT_SECONDARY_TEMP)) {
                temps.replace(Zone.Temps.PROCESS_TEMP, temps.get(Zone.Temps.CURRENT_PRIMARY_TEMP));
            }
        }

    }


    /**
     * Checks with the front end in case user has changed setpoints
     *
     * @param name  : the name of the zone
     * @param temps : the zone's hashmap of temperature values
     */
    static void updateSetpoints(String name, HashMap<Zone.Temps, Integer> temps) {
        List<Integer> setpoints = SetpointsControllerService.getSetpoints();
        int newOccupiedSP = -999;
        int newUnoccupiedSP = -999;
        switch (name) {
            case "zone1":
                newOccupiedSP = setpoints.get(0);
                newUnoccupiedSP = setpoints.get(3);
                break;
            case "zone2":
                newOccupiedSP = setpoints.get(1);
                newUnoccupiedSP = setpoints.get(4);
                break;
            case "zone3":
                newOccupiedSP = setpoints.get(2);
                newUnoccupiedSP = setpoints.get(5);
                break;
        }

        temps.replace(Zone.Temps.OCCUPIED_SP_HEAT, newOccupiedSP);
        temps.replace(Zone.Temps.OCCUPIED_SP_COOL, newOccupiedSP);
        temps.replace(Zone.Temps.UNOCCUPIED_SP_HEAT, newUnoccupiedSP);
        temps.replace(Zone.Temps.UNOCCUPIED_SP_COOL, newUnoccupiedSP);
    }

    /**
     * The process setpoint is the setpoint the system will use to calculates how many stages to request.
     * This method returns the appropriate value based on the mode and occupancy status
     *
     * @param mode       : The Zone's mode - enum defined in Zone.java
     * @param temps      : the zone's hashmap of temperature values
     * @param isOccupied : the zone's occupancy status
     * @return : the process setpoint for the cycle
     */
    static int getProcessSetpoint(Zone.Mode mode, HashMap<Zone.Temps, Integer> temps, boolean isOccupied) {
        switch (mode) {
            case COOL:
                return (isOccupied) ? temps.get(Zone.Temps.OCCUPIED_SP_COOL) : temps.get(Zone.Temps.UNOCCUPIED_SP_COOL);

            case HEAT:
                return (isOccupied) ? temps.get(Zone.Temps.OCCUPIED_SP_HEAT) : temps.get(Zone.Temps.UNOCCUPIED_SP_HEAT);

            default:
                return -999; //error - should not be possible
        }
    }

    /**
     * Calculates how many stages of cooling to request
     *
     * @param temps                    : the zone's hashmap of temperature values
     * @param stageDelay               : if zone not satisfied after running one stage for this many minutes, start second stage
     * @param timeInState              : how long has zone been running current number of stages
     * @param numExistingCoolingStages : how many cooling stages are already running
     * @return : how many cooling stages the zone should request
     */
    static int getCoolingStages(HashMap<Zone.Temps, Integer> temps, int stageDelay, long timeInState, int numExistingCoolingStages) {
        int stages = 0;
        int setPoint = temps.get(Zone.Temps.PROCESS_SP);
        int temperature = temps.get(Zone.Temps.PROCESS_TEMP);

        if (temperature - setPoint >= temps.get(Zone.Temps.FIRST_STAGE_DIFF)) {
            stages += 1;
            if (temperature - setPoint >= temps.get(Zone.Temps.SECOND_STAGE_DIFF)
                    || minutesElapsed(timeInState) >= stageDelay
                    || numExistingCoolingStages == 2) {
                stages += 1;
            }
        } else if (temperature - setPoint > 0 && numExistingCoolingStages > 0) {
            stages = numExistingCoolingStages;
        }
        return stages;
    }

    /**
     * Calculates how many stages of heating to request
     *
     * @param temps                    : the zone's hashmap of temperature values
     * @param stageDelay               : if zone not satisfied after running one stage for this many minutes, start second stage
     * @param timeInState              : how long has zone been running current number of stages
     * @param numExistingHeatingStages : how many heating stages are already running
     * @return : how many heating stages the zone should request
     */
    static int getHeatingStages(HashMap<Zone.Temps, Integer> temps, int stageDelay, long timeInState, int numExistingHeatingStages) {
        int stages = 0;
        int setPoint = temps.get(Zone.Temps.PROCESS_SP);
        int temperature = temps.get(Zone.Temps.PROCESS_TEMP);

        if (setPoint - temperature >= temps.get(Zone.Temps.FIRST_STAGE_DIFF)) {
            stages += 1;
            if (setPoint - temperature >= temps.get(Zone.Temps.SECOND_STAGE_DIFF)
                    || minutesElapsed(timeInState) >= stageDelay
                    || numExistingHeatingStages == 2) {
                stages += 1;
            }
        } else if (setPoint - temperature > 0 && numExistingHeatingStages > 0) {
            stages = numExistingHeatingStages;
        }
        return stages;
    }

    /**
     * helper to convert milliseconds elapsed to minutes
     *
     * @param startTime : time in milliseconds since epoch
     * @return : number of minutes elapsed from startTime until current time
     */
    static int minutesElapsed(long startTime) {
        long millisecondsPerMinute = 60000;
        return (int) ((System.currentTimeMillis() - startTime) / millisecondsPerMinute);
    }

}

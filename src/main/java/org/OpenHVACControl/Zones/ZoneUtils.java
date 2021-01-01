package org.OpenHVACControl.Zones;

import org.OpenHVACControl.ControllerService.SetpointsControllerService;
import org.OpenHVACControl.Sensors.TempSensor;

import java.util.HashMap;
import java.util.List;

class ZoneUtils {

    static void getSensorData(TempSensor primary, TempSensor secondary,
                              HashMap<Zone.Temps, Integer> temps,
                              boolean hasSecondary) throws Exception{

        temps.replace(Zone.Temps.CURRENT_PRIMARY_TEMP, (int)primary.getTemp());

        if (hasSecondary) {
            temps.replace(Zone.Temps.CURRENT_SECONDARY_TEMP, (int)secondary.getTemp());
        }
        temps.replace (Zone.Temps.PROCESS_TEMP, temps.get(Zone.Temps.CURRENT_PRIMARY_TEMP));
        
        if (temps.get(Zone.Temps.CURRENT_PRIMARY_TEMP) == -999 && hasSecondary) {
                temps.replace (Zone.Temps.PROCESS_TEMP, temps.get(Zone.Temps.CURRENT_SECONDARY_TEMP));
        }
        
        System.out.println(temps.get(Zone.Temps.CURRENT_PRIMARY_TEMP) 
                + "  " + temps.get(Zone.Temps.CURRENT_SECONDARY_TEMP)
                +"  " + temps.get(Zone.Temps.PROCESS_TEMP));
    }


    static void updateSetpoints(String name, HashMap<Zone.Temps, Integer> temps){
        List<Integer> setpoints = SetpointsControllerService.getSetpoints();
        int newOccupiedSP = -999;
        int newUnoccupiedSP = -999;
        switch (name){
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

    static int getCoolingStages(HashMap<Zone.Temps, Integer> temps, int stageDelay, long timeInState, int numExistingCoolingStages) {
        int stages = 0;
        int setPoint = temps.get(Zone.Temps.PROCESS_SP);
        int temperature = temps.get(Zone.Temps.PROCESS_TEMP);

        if (temperature - setPoint >= temps.get(Zone.Temps.FIRST_STAGE_DIFF)){
            stages += 1;
            if (temperature - setPoint >= temps.get(Zone.Temps.SECOND_STAGE_DIFF)
                    || minutesElapsed(timeInState) >= stageDelay 
                    || numExistingCoolingStages == 2){
                stages += 1;
            }
        }
        else if(temperature - setPoint > 0 && numExistingCoolingStages > 0){
            stages = numExistingCoolingStages;
        }
        return stages;
    }

    static int getHeatingStages(HashMap<Zone.Temps, Integer> temps, int stageDelay, long timeInState, int numExistingHeatingStages) {
        int stages = 0;
        int setPoint = temps.get(Zone.Temps.PROCESS_SP);
        int temperature = temps.get(Zone.Temps.PROCESS_TEMP);

        if (setPoint - temperature >= temps.get(Zone.Temps.FIRST_STAGE_DIFF)){
            stages += 1;
            if (setPoint - temperature >= temps.get(Zone.Temps.SECOND_STAGE_DIFF)
                    || minutesElapsed(timeInState) >= stageDelay 
                    || numExistingHeatingStages == 2 ){
                stages += 1;
            }
        }
        else if(setPoint - temperature > 0 && numExistingHeatingStages > 0){
            stages = numExistingHeatingStages;
        }
        return stages;
    }

    static int minutesElapsed(long startTime) {
        long millisecondsPerMinute = 60000;
        return (int) ((System.currentTimeMillis() - startTime) / millisecondsPerMinute);
    }

}

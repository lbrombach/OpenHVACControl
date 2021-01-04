package org.OpenHVACControl.Zones;


import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import org.OpenHVACControl.Sensors.InactiveSensorException;
import org.OpenHVACControl.Sensors.InactiveZoneException;
import org.OpenHVACControl.Sensors.TempSensor;
import org.OpenHVACControl.hardware.Damper;
import org.OpenHVACControl.hardware.GPIOControl;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class Zone {
    static final String filePath = "src/main/resources/zones.json";

    public enum Mode {HEAT, COOL, OFF}

    public enum Temps {
        PROCESS_SP,   //The process setpoint. This is the temp that will be used during a decision-cycle.
        PROCESS_TEMP, //process temperature. This is the temperature that will be compared against the process setpoint during a decision cycle
        OCCUPIED_SP_HEAT,
        OCCUPIED_SP_COOL,
        UNOCCUPIED_SP_HEAT,
        UNOCCUPIED_SP_COOL,
        FIRST_STAGE_DIFF, //the number of degrees (F) from PROCESS_SP can get from PROCESS_TEMP before system starts stage 1
        SECOND_STAGE_DIFF, //the number of degrees (F) from PROCESS_SP can get from PROCESS_TEMP before system starts stage 2
        CURRENT_PRIMARY_TEMP, //most recent primary sensor read
        CURRENT_SECONDARY_TEMP //most recent secondary sensor read (if present)
    }

    private static final Pin ZONE_1_DAMPER_PIN = RaspiPin.GPIO_12;
    private static final Pin ZONE_2_DAMPER_PIN = RaspiPin.GPIO_13;
    private static final Pin ZONE_3_DAMPER_PIN = RaspiPin.GPIO_14;
    @Expose
    private String name;
    @Expose
    private String alias;
    private TempSensor primaryTempSensor;
    private TempSensor secondaryTempSensor;
    @Expose
    private Mode mode;
    @Expose
    private HashMap<Temps, Integer> temps = new HashMap<>(16);
    @Expose
    private int secondaryIsPrimaryTime; //how long to make controller use secondary sensor instead of primary
    @Expose
    private int secondStageTimeDelay; //if setpoint is not made after this long at stage 1, system will start stage 2 even if SECOND_STAGE_DIFF is not met
    @Expose
    private boolean isOccupied;
    @Expose
    private boolean isActive;
    @Expose
    private boolean usingSecondarySensor;
    private long lastValidSensorRead;  //the time of the last non-error sensor read (milliseconds since epoch)
    private Request request;
    private Damper damper;


    public Zone() {
    }

    public Zone(String name, GPIOControl gpio) throws InactiveSensorException {
        this.name = name;
        if (name.equals("zone1")) {
            this.alias = "Main Floor";
            damper = new Damper(ZONE_1_DAMPER_PIN, gpio);

        } else if (name.equals("zone2")) {
            this.alias = "Upper Floor";
            damper = new Damper(ZONE_2_DAMPER_PIN, gpio);
        } else {
            this.alias = "Basement";
            damper = new Damper(ZONE_3_DAMPER_PIN, gpio);
        }
        try {
            primaryTempSensor = new TempSensor(name + "Primary");
            System.out.println(name + " Primary sensor alias: " + primaryTempSensor.getAlias());
            isActive = true;
        } catch (InactiveSensorException e) {
            isActive = false;
        }
        try {
            secondaryTempSensor = new TempSensor(name + "Secondary");
            System.out.println(name + " Secondary sensor alias: " + secondaryTempSensor.getAlias());
            usingSecondarySensor = true;
        } catch (InactiveSensorException e) {
            usingSecondarySensor = false;
        }
        mode = Mode.HEAT;
        secondaryIsPrimaryTime = 30;
        temps.put(Temps.PROCESS_TEMP, -999);
        temps.put(Temps.PROCESS_SP, 70);
        temps.put(Temps.OCCUPIED_SP_HEAT, 70);
        temps.put(Temps.OCCUPIED_SP_COOL, 70);
        temps.put(Temps.UNOCCUPIED_SP_HEAT, 70);
        temps.put(Temps.UNOCCUPIED_SP_COOL, 70);
        temps.put(Temps.FIRST_STAGE_DIFF, 2);
        temps.put(Temps.SECOND_STAGE_DIFF, 3);
        temps.put(Temps.CURRENT_PRIMARY_TEMP, 70);
        temps.put(Temps.CURRENT_SECONDARY_TEMP, -999);

        secondStageTimeDelay = 10;
        isOccupied = true;
        request = new Request();
    }

    public Zone(Pin damperPin, String name, GPIOControl gpio) throws Exception {

        boolean isMatch = false;
        try {
            this.name = name;
            Reader inFile = Files.newBufferedReader(Paths.get(filePath));
            Gson gson = new Gson();
            List<Zone> zones = gson.fromJson(inFile, new TypeToken<List<Zone>>() {
            }.getType());

            for (int i = 0; i < zones.size() && isMatch == false; i++) {
                if (zones.get(i).name.contains(this.name)) {
                    System.out.println("Got zone match for " + this.name + "process temp = " + zones.get(i).temps.get(Temps.PROCESS_SP));
                    this.name = zones.get(i).name;
                    this.alias = zones.get(i).alias;
                    this.mode = zones.get(i).mode;
                    this.secondaryIsPrimaryTime = zones.get(i).secondaryIsPrimaryTime;
                    temps.put(Temps.PROCESS_TEMP, -999);
                    temps.put(Temps.PROCESS_SP, zones.get(i).temps.get(Temps.PROCESS_SP));
                    temps.put(Temps.OCCUPIED_SP_HEAT, zones.get(i).temps.get(Temps.OCCUPIED_SP_HEAT));
                    temps.put(Temps.OCCUPIED_SP_COOL, zones.get(i).temps.get(Temps.OCCUPIED_SP_COOL));
                    temps.put(Temps.UNOCCUPIED_SP_HEAT, zones.get(i).temps.get(Temps.UNOCCUPIED_SP_HEAT));
                    temps.put(Temps.UNOCCUPIED_SP_COOL, zones.get(i).temps.get(Temps.UNOCCUPIED_SP_COOL));
                    temps.put(Temps.FIRST_STAGE_DIFF, zones.get(i).temps.get(Temps.FIRST_STAGE_DIFF));
                    temps.put(Temps.SECOND_STAGE_DIFF, zones.get(i).temps.get(Temps.SECOND_STAGE_DIFF));
                    temps.put(Temps.CURRENT_PRIMARY_TEMP, 70);
                    temps.put(Temps.CURRENT_SECONDARY_TEMP, -999);
                    this.secondStageTimeDelay = zones.get(i).secondStageTimeDelay;
                    this.isOccupied = true;
                    isMatch = true;
                }
            }

            inFile.close();
        } finally {
            if (isMatch == false) {
                throw new InactiveZoneException("Zone " + name + " not in config file at: " + filePath);
            } else {
                try {
                    primaryTempSensor = new TempSensor(name + "Primary");
                    System.out.println(name + " Primary sensor alias: " + primaryTempSensor.getAlias());
                    isActive = true;
                } catch (InactiveSensorException e) {
                    isActive = false;
                }
                try {
                    secondaryTempSensor = new TempSensor(name + "Secondary");
                    System.out.println(name + " Secondary sensor alias: " + secondaryTempSensor.getAlias());
                    usingSecondarySensor = true;
                } catch (InactiveSensorException e) {
                    usingSecondarySensor = false;
                }
                damper = new Damper(damperPin, gpio);
                request = new Request();
            }
        }


    }


    /**
     * This method is responsible for keeping the zone's data up to date by
     * requesting the latest from the front end and sensors, then setting the request object's
     * number of stages requested based on mode and feedback from utility methods
     *
     * @return : the zone's updated request object
     */
    public Request getRequest() {
        ZoneUtils.updateSetpoints(name, temps);
        temps.replace(Temps.PROCESS_SP, ZoneUtils.getProcessSetpoint(mode, temps, isOccupied));

        try {
            ZoneUtils.getSensorData(primaryTempSensor, secondaryTempSensor, temps, usingSecondarySensor);
            if (temps.get(Temps.PROCESS_TEMP) == -999) {
                throw new Exception("INVALID SENSOR DATA in Zone.getRequest() " + name);
            }
            lastValidSensorRead = System.currentTimeMillis();
        } catch (Exception e) {
            mode = Mode.OFF;
            System.out.println(e.getMessage() + "Setting zone mode to OFF internally");
            //add email, text, or alert sensor problem here
        }


        int stages = 0;
        switch (mode) {
            case COOL:
                request.setHeatingStages(0);
                if (request.getCoolingStages() == 0) {
                    request.setTimeStateStarted(System.currentTimeMillis());
                }
                stages = ZoneUtils.getCoolingStages(temps, secondStageTimeDelay, request.getTimeStateStarted(), request.getCoolingStages());
                request.setCoolingStages(stages);
                break;

            case HEAT:
                request.setCoolingStages(0);
                if (request.getHeatingStages() == 0) {
                    request.setTimeStateStarted(System.currentTimeMillis());
                }
                stages = ZoneUtils.getHeatingStages(temps, secondStageTimeDelay, request.getTimeStateStarted(), request.getHeatingStages());
                request.setHeatingStages(stages);
                break;

            default: //handles "OFF" mode
                request.setHeatingStages(0);
                request.setCoolingStages(0);

        }

        return request;

    }


    void deactivate() {
        isActive = false;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setFanRequest(boolean isRequested) {
        request.setFanRequest(isRequested);
    }

    public boolean isFanRequested() {
        return request.isFanRequested();
    }

    public int getTemp(Temps key) {
        return temps.get(key);
    }

    public void setTemp(Temps key, int value) {
        temps.replace(key, value);
    }

    public int getSecondaryIsPrimaryTime() {
        return secondaryIsPrimaryTime;
    }

    public void setSecondaryIsPrimaryTime(int secondaryIsPrimaryTime) {
        this.secondaryIsPrimaryTime = secondaryIsPrimaryTime;
    }

    public int getSecondStageTimeDelay() {
        return secondStageTimeDelay;
    }

    public void setSecondStageTimeDelay(int secondStageTimeDelay) {
        this.secondStageTimeDelay = secondStageTimeDelay;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public boolean isDamperIsOpen() {
        return damper.isOpen();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isUsingSecondarySensor() {
        return usingSecondarySensor;
    }

    public void setUsingSecondarySensor(boolean usingSecondarySensor) {
        this.usingSecondarySensor = usingSecondarySensor;
    }

    public long getLastValidSensorRead() {
        return lastValidSensorRead;
    }

    public void setLastValidSensorRead(long lastValidSensorRead) {
        this.lastValidSensorRead = lastValidSensorRead;
    }

    public void openDamper() {
        damper.open();
    }

    public void closeDamper() {
        damper.close();
    }

    public int heatStagesRequested() {
        return request.getHeatingStages();
    }

    public int coolStagesRequested() {
        return request.getCoolingStages();
    }


    @Override
    public String toString() {
        return "Zone{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", secondaryIsPrimaryTime=" + secondaryIsPrimaryTime +
                ", processTemp= " + temps.get(Temps.PROCESS_TEMP) +
                ", processSP= " + temps.get(Temps.PROCESS_SP) +
                ", occupiedSPHeat=" + temps.get(Temps.OCCUPIED_SP_HEAT) +
                ", occupiedSPCool=" + temps.get(Temps.OCCUPIED_SP_COOL) +
                ", unoccupiedSPHeat=" + temps.get(Temps.UNOCCUPIED_SP_HEAT) +
                ", unoccupiedSPCool=" + temps.get(Temps.UNOCCUPIED_SP_COOL) +
                ", firstStageTempDifferential=" + temps.get(Temps.FIRST_STAGE_DIFF) +
                ", secondStageTempDifferential=" + temps.get(Temps.SECOND_STAGE_DIFF) +
                ", secondStageTimeDelay=" + secondStageTimeDelay +
                ", isActive=" + isActive +
                ", usingSecondarySensor=" + usingSecondarySensor +
                ", lastValidSensorRead=" + lastValidSensorRead +
                ", request=" + request +
                ", damper=" + damper +
                '}';
    }
}

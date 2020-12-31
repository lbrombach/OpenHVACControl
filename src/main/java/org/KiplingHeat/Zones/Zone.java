package org.KiplingHeat.Zones;


import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.impl.PinImpl;
import org.KiplingHeat.Sensors.InactiveSensorException;
import org.KiplingHeat.Sensors.InactiveZoneException;
import org.KiplingHeat.Sensors.TempSensor;
import org.KiplingHeat.hardware.Damper;
import org.KiplingHeat.hardware.GPIOControl;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Zone {
    static final String filePath = "src/main/resources/zones.json";

    public enum Mode {HEAT, COOL, OFF}

    public enum Temps {
        PROCESS_SP,
        PROCESS_TEMP,
        OCCUPIED_SP_HEAT,
        OCCUPIED_SP_COOL,
        UNOCCUPIED_SP_HEAT,
        UNOCCUPIED_SP_COOL,
        FIRST_STAGE_DIFF,
        SECOND_STAGE_DIFF,
        CURRENT_PRIMARY_TEMP,
        CURRENT_SECONDARY_TEMP
    }

    private static final Pin ZONE_1_DAMPER_PIN = RaspiPin.GPIO_12;
    private static final Pin ZONE_2_DAMPER_PIN = RaspiPin.GPIO_13;
    private static final Pin ZONE_3_DAMPER_PIN = RaspiPin.GPIO_14;
//    private static final int DAMPER_CLOSE_DELAY = 30;  //seconds to delay closing damper if stopping last heating stage
    @Expose
    private String name;
    @Expose
    private String alias;
    private TempSensor primaryTempSensor;
    private TempSensor secondaryTempSensor;
    @Expose
    private Mode mode;
    @Expose
    private HashMap<Temps, Integer> temps = new HashMap<Temps, Integer>(16);
    @Expose
    private int secondaryIsPrimaryTime; //how long to make controller use secondary sensor instead of primary
    @Expose
    private int secondStageTimeDelay;
    @Expose
    private int priority;
    @Expose
    private boolean isOccupied;
    private boolean damperIsOpen;
    private int outsideAirTemp;
    @Expose
    private boolean isActive;
    @Expose
    private boolean usingSecondarySensor;
    private long lastValidSensorRead;
    private Request request;
    private Damper damper;


    public Zone() {
    }

    public Zone(String name, GPIOControl gpio) throws InactiveSensorException {
        this.name = name;
        if (name == "zone1") {
            this.alias = "Main Floor";
            damper = new Damper(ZONE_1_DAMPER_PIN, gpio);

        } else if (name == "zone2") {
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
        priority = 1;
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
                    this.priority = zones.get(i).priority;
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


    void deactivate() {
        isActive = false;
    }


    public Request getRequest() {
        ZoneUtils.updateSetpoints(name, temps);
        temps.replace(Temps.PROCESS_SP, ZoneUtils.getProcessSetpoint(mode, temps, isOccupied));

        try {
            ZoneUtils.getSensorData(primaryTempSensor, secondaryTempSensor, temps, usingSecondarySensor);
            if (temps.get(Temps.PROCESS_TEMP) == -999) {
                            System.out.println("throwing exception 11 in getrequest: " );

                throw new Exception("INVALID SENSOR DATA " + name);
            }
            lastValidSensorRead = System.currentTimeMillis();
        } catch (Exception e) {
            mode = Mode.OFF;
            System.out.println(e.getMessage() + "Setting zone mode to OFF internally");
            //email, text, or alert sensor problem here
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

            default:
                request.setHeatingStages(0);
                request.setCoolingStages(0);

        }

        return request;

    }


    public static String getFilePath() {
        return filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getTimeStateStarted() {
        return request.getTimeStateStarted();
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

    public int getOutsideAirTemp() {
        return outsideAirTemp;
    }

    public void setOutsideAirTemp(int outsideAirTemp) {
        this.outsideAirTemp = outsideAirTemp;
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

    public int getCurrentPrimaryTemp() {
        return temps.get(Temps.CURRENT_PRIMARY_TEMP);
    }

    public int getCurrentSecondaryTemp() {
        return temps.get(Temps.CURRENT_SECONDARY_TEMP);
    }

    public void openDamper() {
        damper.open();
    }

    public void closeDamper() {
        damper.close();
    }

//    public void closeDamperWithDelay() {
//        damper.closeWithDelay(DAMPER_CLOSE_DELAY);
//    }

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
                ", priority=" + priority +
                ", damperIsOpen=" + damperIsOpen +
                ", outsideAirTemp=" + outsideAirTemp +
                ", isActive=" + isActive +
                ", usingSecondarySensor=" + usingSecondarySensor +
                ", lastValidSensorRead=" + lastValidSensorRead +
                ", request=" + request +
                ", damper=" + damper +
                '}';
    }
}

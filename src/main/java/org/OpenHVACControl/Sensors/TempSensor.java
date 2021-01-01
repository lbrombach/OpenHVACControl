package org.OpenHVACControl.Sensors;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class TempSensor extends AbstractSensor {

    private long lastReadTime;
    private int lastTempRead;
    private TempSensor newSetupConfig;


    public TempSensor(String name) throws InactiveSensorException {
        super(name);
    }

    public float getTemp() throws Exception {
//        System.out.println("Getting temperature from sensor " + name);
        String devicePath = "/sys/bus/w1/devices/" + address + "/w1_slave";
        float temp = -999;
        try {
            String data = String.valueOf(new Scanner(new File(devicePath)).useDelimiter("\\Z").next());
            String subStrings[] = data.split("t="); //temp data comes after "t="
            temp = Float.valueOf(subStrings[subStrings.length - 1]); //temp data is last item in file string
            temp = temp / 1000.0f * 1.8f + 32.0f + sensorOffset;  //temp data comes as millidegrees celsius
            lastTempRead = (int) temp;
            lastReadTime = System.currentTimeMillis();
//            System.out.println("TEMP READ = " + temp + " deg F");
        } catch (IOException e) {
            System.out.println("IO exception reading temp? Are you simulating (not on a PI) or is sensor bad?");
            throw new Exception(name + " temp read error");
        } finally {
            if(temp == -999 && System.currentTimeMillis() - lastReadTime < 10000)
            {
                temp = lastTempRead;
            }
            return temp;
        }
    }

    @Override
    protected void applyNewConfig(AbstractSensor newSetupConfig) {
        this.name = newSetupConfig.getName();
        this.alias = newSetupConfig.getAlias();
        this.address = newSetupConfig.getAddress();
        this.sensorOffset = newSetupConfig.getSensorOffset();
    }

    @Override
    public String toString() {
        return "TempSensor{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", address='" + address + '\'' +
                ", sensorOffset=" + sensorOffset +
                ", gpio=" + gpio +
                ", lastReadTime=" + lastReadTime +
                ", lastTempRead=" + lastTempRead +
                '}';
    }

}

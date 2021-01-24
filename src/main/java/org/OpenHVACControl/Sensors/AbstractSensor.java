package org.OpenHVACControl.Sensors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.OpenHVACControl.hardware.GPIOControl;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public abstract class AbstractSensor {
    static final String filePath = "src/main/resources/tempSensors.json";
    protected String name;
    protected String alias;
    protected String address = null; //either an address or a GPIO pin number
    protected int sensorOffset;


    protected AbstractSensor(){}

    /**
     * constructor - initializes sensor with data from tempSensors.json based on the name passed in
     * @param name : the sensor name
     * @throws InactiveSensorException :
     */
    public AbstractSensor (String name)  throws InactiveSensorException {
        try {
            this.name = name;
            boolean isMatch = false;
            Reader inFile = Files.newBufferedReader(Paths.get(filePath));
            Gson gson = new Gson();
            List<TempSensor> sensors = gson.fromJson(inFile, new TypeToken<List<TempSensor>>() {
            }.getType());

            for (int i = 0; i < sensors.size() && isMatch == false; i++) {
                if (sensors.get(i).name.contains(this.name)) {
                    this.name = sensors.get(i).name;
                    this.alias = sensors.get(i).alias;
                    this.address = sensors.get(i).address;
                    this.sensorOffset = sensors.get(i).sensorOffset;
                    isMatch = true;
                    System.out.println("Got sensor match for " + this.name + ".."+this.address + " at " + filePath);
                }
            }

            inFile.close();
            if (isMatch == false) {
                throw new InactiveSensorException("Sensor not in config file at: "+ filePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected abstract void applyNewConfig(AbstractSensor newSetupConfig);

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public int getSensorOffset() {
        return sensorOffset;
    }

    public void setSensorOffset(int sensorOffset) {
        this.sensorOffset = sensorOffset;
    }

    @Override
    public String toString() {
        return (name + "/n    " + address + "/n    " + sensorOffset);
    }

}

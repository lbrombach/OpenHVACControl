package org.OpenHVACControl.Sensors;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import java.sql.Timestamp;  ///////////
import java.text.SimpleDateFormat; ///////
import java.util.*; ///////////////
import java.io.*; ////////////////////////
import com.google.gson.Gson; ///////////
import com.google.gson.GsonBuilder; //////

public class TempSensor extends AbstractSensor {

    private long lastReadTime;
    private int lastTempRead;
    
    class Logger{
        Logger(){
            lastLog = System.currentTimeMillis();
            conseqFailedReads = new ArrayList<>();
        }
        public int totalReads;
        public int goodFirstReads;
        public int goodSecondReads;
        public int usedLastGoodRead;
        public int failedRead;
        public long lastLog;
        public List<Integer> conseqFailedReads;
        public boolean lastFailed = false;
        Timestamp timestamp;
    }
    Logger logger = new Logger();
    
    void log(String name){
        String fileName = "/home/pi/logs/" + name + ".json";
        try (Writer outFile = new FileWriter(fileName, true)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(logger, outFile);
            outFile.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }    

    public TempSensor(String name) throws InactiveSensorException {
        super(name);
    }


    /**
     * reads the sensor data
     * @return : raw sensor reading
     * @throws Exception : IO error
     */
    private float readSensor() throws Exception {
        String devicePath = "/sys/bus/w1/devices/" + address + "/w1_slave";

        String data = String.valueOf(new Scanner(new File(devicePath)).useDelimiter("\\Z").next());
        String subStrings[] = data.split("t="); //temp data comes after "t="
        return Float.valueOf(subStrings[subStrings.length - 1]); //temp data is last item in file string
    }

    /**
     * @return : the floating point temperature in degrees Fahrenheit. Returns -999 in case of read failure.
     * @throws Exception : temperature read error
     */
    public float getTemp(){

        logger.totalReads ++;//////////////////////////////////////////////////////////////////////////////////

        float temp = -999;
        try {
            temp = readSensor();
            temp = temp / 1000.0f * 1.8f + 32.0f;  //temp data comes as millidegrees celsius. Convert to F

            if(temp != 185 && temp != -999) {logger.goodFirstReads++;}///////////////////////////////////

            
            if(temp == 185 || temp == -999){ //185 indicates error data from reading sensor. -999 more likely sensor cannot be read
                Thread.sleep(1000);
                temp = readSensor() / 1000.0f * 1.8f + 32.0f;

                if(temp != 185 && temp != -999) {logger.goodSecondReads++;}///////////////////////////////////

            }
            else {
                lastTempRead = (int) temp;
                lastReadTime = System.currentTimeMillis();

                

            }
        } catch (IOException e) {
            System.out.println("IO exception reading temp? Are you simulating (not on a PI) or is sensor bad?");
        } finally {
            if ((temp == 185 || temp == -999) && System.currentTimeMillis() - lastReadTime < 10000) {
                temp = lastTempRead;
                logger.usedLastGoodRead++;////////////////////////////////////////////////////////////
            }
            if (temp == 185 || temp == -999){        ////////////////////////////////////
                logger.failedRead++;                //////////////////////////////////////////
                if(logger.lastFailed == true && logger.conseqFailedReads.size() == 0){               //////////////////////////////////////////
                    logger.conseqFailedReads.add(1);               //////////////////////////////////////////
                }else if (logger.lastFailed == true){               //////////////////////////////////////////
                    int i = logger.conseqFailedReads.size()-1;               //////////////////////////////////////////
                    logger.conseqFailedReads.set(i, logger.conseqFailedReads.get(i)+1);     //////////////////////////////////////////
                }                             //////////////////////////////////////////
                logger.lastFailed = true;                 //////////////////////////////////////////
            }                           ///////////////////////////////////////
            else {logger.lastFailed = false;}             ///////////////////////////////////////////
               

            if(System.currentTimeMillis() - logger.lastLog > 14400000*6*7){/////////////// 14400000 ms = 4 hours
                logger.timestamp = new Timestamp(System.currentTimeMillis()); //////////////////////
                log(this.name);                                           ///////////////////////////
                logger = new Logger();                               /////////////////////////////
              }/////////////////////////////////////////////////////////////////////////////////////////////////////  
                
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
                ", lastReadTime=" + lastReadTime +
                ", lastTempRead=" + lastTempRead +
                '}';
    }

}

package org.KiplingHeat;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import org.KiplingHeat.ControllerService.SetpointsControllerService;
import org.KiplingHeat.ControllerService.TempsControllerService;
import org.KiplingHeat.ControllerService.ZoneDataControllerService;
import org.KiplingHeat.ControllerService.OutputsControllerService;
import org.KiplingHeat.Zones.Request;
import org.KiplingHeat.Zones.Zone;
import org.KiplingHeat.hardware.CallsToSystem;
import org.KiplingHeat.hardware.GPIOControl;

import java.util.List;
import java.util.Map;

public class MainController {

    private static final Pin ZONE_1_DAMPER_PIN = RaspiPin.GPIO_12;
    private static final Pin ZONE_2_DAMPER_PIN = RaspiPin.GPIO_13;
    private static final Pin ZONE_3_DAMPER_PIN = RaspiPin.GPIO_14;
    private static final Pin W1_RELAY_PIN = RaspiPin.GPIO_21;
    private static final Pin W2_RELAY_PIN = RaspiPin.GPIO_22;
    private static final Pin Y1_RELAY_PIN = RaspiPin.GPIO_23;
    private static final Pin Y2_RELAY_PIN = RaspiPin.GPIO_24;
    private static final Pin FAN_RELAY_PIN = RaspiPin.GPIO_25;
    private CallsToSystem callsToSystem;
    private GPIOControl gpio;
    private List<Zone> zones;

    public void mainController() {
        System.out.println("This is the main controller");
        //initialize stuff
        try {
            gpio = new GPIOControl();
            callsToSystem = new CallsToSystem(W1_RELAY_PIN, W2_RELAY_PIN,Y1_RELAY_PIN, Y2_RELAY_PIN, FAN_RELAY_PIN, gpio);
            zones = MainControllerUtils.initializeZones(ZONE_1_DAMPER_PIN, ZONE_2_DAMPER_PIN, ZONE_3_DAMPER_PIN, gpio);
            if (!MainControllerUtils.validateZones(zones)){
                throw new Exception("Unable to initialize zones");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        for (int i =0; i<zones.size(); i++) {
            System.out.println(zones.get(i).toString());
        }

            //update aliases and modes TO the front end. Initialize with this only once.
            ZoneDataControllerService.sendZoneDatatoFrontEnd(zones);
        
        //decide what the system should be doing and enable only the dampers that should be
        while(true){
            System.out.println();

            //send relay state data to front end for display
            OutputsControllerService.setOutputs(CallsToSystem.getRelayStates(), zones);

            //update setpoints with latest FROM front end
            MainControllerUtils.updateFromFrontEnd(SetpointsControllerService.getSetpoints(),
                                                    ZoneDataControllerService.getModes(),
                                                    ZoneDataControllerService.getFanRequests(),
                                                    zones);

            //totalize requests from all zones int one request object (this also gets new temps from sensors)
            List<Request> allRequests = MainControllerUtils.getAllRequests(zones);
            Request totalRequests = new Request(MainControllerUtils.totalRequests(allRequests));

            //sends new temps from sensors to front end
            TempsControllerService.setTemps(zones);

            //get current states in a map for usage in logic below
            Map<String, Boolean> states = CallsToSystem.getRelayStatesMap();

            //begin controller logic
            if(totalRequests.getHeatingStages() > 0){ //////////START do heating stuff

                if (states.get("Y1") || states.get("Y2") ){
                    CallsToSystem.turnCoolingOff(zones);
                }
                if (states.get("G")){
                    CallsToSystem.turnFanOff(zones);
                }

                CallsToSystem.startStage1Heat(zones);

                if(totalRequests.getHeatingStages()==2){
                    CallsToSystem.startStage2Heat();
                    continue;
                }
                else {
                    CallsToSystem.turnOffStage2Heat();
                    continue;
                }
            }///////////////////////////////////////////////////END do heating stuff
            else if(states.get("W1") || states.get("W2")){
                CallsToSystem.turnHeatingOff(zones);
            }
            else if (totalRequests.getCoolingStages() > 0){ //START do cooling stuff

                CallsToSystem.startStage1Cool(zones);

                if (totalRequests.getCoolingStages()==2){
                    CallsToSystem.startStage2Cool();
                    continue;
                }
                else {
                    CallsToSystem.turnOffStage2Cool();
                    continue;
                }
            }///////////////////////////////////////////////////END do cooling stuff
            else if(states.get("Y1") || states.get("Y2")){
                CallsToSystem.turnCoolingOff(zones);
            }
            else if(totalRequests.isFanRequested()){        //START do fan only stuff
                CallsToSystem.startFanOnly(zones);
                continue;
            }
            else if(states.get("G")){
                CallsToSystem.turnFanOff(zones);
            }
            ///////////////////////////////////////////




        }


/*
           try (Writer outFile = new FileWriter("src/main/resources/zones.json")) {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                gson.toJson(zones, outFile);
                outFile.close();
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
*/

    }


}

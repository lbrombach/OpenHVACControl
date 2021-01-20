package org.OpenHVACControl;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import org.OpenHVACControl.ControllerService.OutputsControllerService;
import org.OpenHVACControl.ControllerService.TempsControllerService;
import org.OpenHVACControl.ControllerService.ZoneDataControllerService;
import org.OpenHVACControl.Zones.Request;
import org.OpenHVACControl.Zones.Zone;
import org.OpenHVACControl.hardware.CallsToSystem;
import org.OpenHVACControl.hardware.GPIOControl;

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
    private GPIOControl gpio;
    private List<Zone> zones;

    /**
     * This is the main controller.
     * Initializes system and runs system control loop
     */
    public void mainController() {
        //initialize stuff
        try {
            gpio = new GPIOControl();
            CallsToSystem.setPins(W1_RELAY_PIN, W2_RELAY_PIN, Y1_RELAY_PIN, Y2_RELAY_PIN, FAN_RELAY_PIN, gpio);
            zones = MainControllerUtils.initializeZones(ZONE_1_DAMPER_PIN, ZONE_2_DAMPER_PIN, ZONE_3_DAMPER_PIN, gpio);
            if (!MainControllerUtils.validateZones(zones)) {
                throw new Exception("Unable to initialize zones");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < zones.size(); i++) {
            System.out.println(zones.get(i).toString());
        }

        //update aliases and modes TO the front end. Initialize with this only once.
        ZoneDataControllerService.setZoneSettings(zones);

        //main control loop. Decides what the system should be doing and enable only the dampers that should be
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //send relay state data to front end for display
            OutputsControllerService.setOutputs(CallsToSystem.getRelayStates(), zones);



            ZoneDataControllerService.updateZoneSettings(zones);
            if(ZoneDataControllerService.getNeedsSave()){
                MainControllerUtils.saveZoneJson(zones);
                ZoneDataControllerService.resetNeedsSave();
            }
            


            //totalize requests from all zones int one request object (this also gets new temps from sensors)
            List<Request> allRequests = MainControllerUtils.getAllRequests(zones);
            Request totalRequests = new Request(MainControllerUtils.totalRequests(allRequests));

            //sends new temps from sensors to front end
            TempsControllerService.setTemps(zones);

            //get current states in a map for usage in logic below
            Map<String, Boolean> states = CallsToSystem.getRelayStatesMap();

            //begin controller logic
            if (totalRequests.getHeatingStages() > 0) { //////////START do heating stuff

                if (states.get("Y1") || states.get("Y2")) {
                    CallsToSystem.turnCoolingOff(zones);
                }
                if (states.get("G")) {
                    CallsToSystem.turnFanOff(zones);
                }

                CallsToSystem.startStage1Heat(zones);

                if (totalRequests.getHeatingStages() == 2) {
                    CallsToSystem.startStage2Heat();
                } else {
                    CallsToSystem.turnOffStage2Heat();
                }
            } else if (states.get("W1") || states.get("W2")) {//there is no call but heating is on - turn it off
                CallsToSystem.turnHeatingOff(zones);
            }///////////////////////////////////////////////////END do heating stuff
            else if (totalRequests.getCoolingStages() > 0) { //START do cooling stuff

                CallsToSystem.startStage1Cool(zones);

                if (totalRequests.getCoolingStages() == 2) {
                    CallsToSystem.startStage2Cool();
                } else {
                    CallsToSystem.turnOffStage2Cool();
                }
            } else if (states.get("Y1") || states.get("Y2")) { //there is no call but cooling is on - turn it off
                CallsToSystem.turnCoolingOff(zones);
            }///////////////////////////////////////////////////END do cooling stuff
            else if (totalRequests.isFanRequested()) {        //START do fan only stuff
                CallsToSystem.startFanOnly(zones);
            } else if (states.get("G")) {  //no calls for fan only but fan is on - turn it off
                CallsToSystem.turnFanOff(zones);
            }///////////////////////////////////////////END do fan only stuff

            /////////////////////////END main loop//////////////////////////


        }




    }


}

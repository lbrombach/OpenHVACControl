package org.OpenHVACControl.ControllerService;

import com.google.common.util.concurrent.Monitor;
import org.OpenHVACControl.Zones.Zone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class OutputsControllerService {

    private static Monitor mutex = new Monitor();

    private static boolean W1 = false;
    private static boolean W2 = false;
    private static boolean Y1 = false;
    private static boolean Y2 = false;
    private static boolean G = false;
    private static boolean damperZ1 = false;
    private static boolean damperZ2 = false;
    private static boolean damperZ3 = false;


    /**
     * the front end calls this to get values so it can display the system outputs
     *
     * @return : list of system outputs
     */
    public static List<Boolean> getOutputs() {
        mutex.enter();
        try {
            return new ArrayList<Boolean>(Arrays.asList(W1, W2, Y1, Y2, G, damperZ1, damperZ2, damperZ3));
        }
        finally {
            mutex.leave();
        }
    }

    /**
     * the back end (in MainController() ) uses this to keep the values here updated
     *
     * @param systemRelaysStates : list of states of all the hvac system relays
     * @param zones              : list of zones
     */
    public static void setOutputs(List<Boolean> systemRelaysStates, List<Zone> zones) {
        mutex.enter();
        try {
        W1 = systemRelaysStates.get(0);
        W2 = systemRelaysStates.get(1);
        Y1 = systemRelaysStates.get(2);
        Y2 = systemRelaysStates.get(3);
        G = systemRelaysStates.get(4);
        damperZ1 = zones.get(0).isDamperIsOpen();
        damperZ2 = zones.get(1).isDamperIsOpen();
        damperZ3 = zones.get(2).isDamperIsOpen();
        }
        finally {
            mutex.leave();
        }
    }


}

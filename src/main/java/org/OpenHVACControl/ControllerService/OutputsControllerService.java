package org.OpenHVACControl.ControllerService;

import org.OpenHVACControl.Zones.Zone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class OutputsControllerService {

    private static boolean W1 = false;
    private static boolean W2 = false;
    private static boolean Y1 = false;
    private static boolean Y2 = false;
    private static boolean G = false;
    private static boolean damperZ1 = false;
    private static boolean damperZ2 = false;
    private static boolean damperZ3 = false;


    public static List<Boolean> getOutputs() {
        return new ArrayList<Boolean>(Arrays.asList(W1, W2, Y1, Y2, G, damperZ1, damperZ2, damperZ3) );
    }

    public static void setOutputs(List<Boolean> systemRelaysStates, List<Zone> zones){        
                W1=systemRelaysStates.get(0);
                W2=systemRelaysStates.get(1);
                Y1=systemRelaysStates.get(2);
                Y2=systemRelaysStates.get(3);
                G =systemRelaysStates.get(4);
                damperZ1=zones.get(0).isDamperIsOpen();
                damperZ2=zones.get(1).isDamperIsOpen();
                damperZ3=zones.get(2).isDamperIsOpen();
    }


}

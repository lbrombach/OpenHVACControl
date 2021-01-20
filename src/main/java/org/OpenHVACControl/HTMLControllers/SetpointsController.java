package org.OpenHVACControl.HTMLControllers;

//import org.OpenHVACControl.ControllerService.SetpointsControllerService;
import org.OpenHVACControl.ControllerService.ZoneDataControllerService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SetpointsController {

    @RequestMapping("/setpoints")
    public List<Integer> getSetpoints(){
        return ZoneDataControllerService.getSetpoints();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/setpoints/increase/{zoneNum}")
    public int increaseSP(@PathVariable String zoneNum){
        return ZoneDataControllerService.increaseSP(Integer.parseInt(zoneNum));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/setpoints/decrease/{zoneNum}")
    public int decreaseSP(@PathVariable String zoneNum){
        return ZoneDataControllerService.decreaseSP(Integer.parseInt(zoneNum));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/setpoints/set/{zoneNum}/{newSP}")
    public void setSP(@PathVariable String zoneNum, @PathVariable String newSP){
        ZoneDataControllerService.setSP(Integer.parseInt(zoneNum), Integer.parseInt(newSP));
        System.out.println("SETTING" + newSP);
    }

}

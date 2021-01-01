package org.OpenHVACControl.HTMLControllers;

import org.OpenHVACControl.ControllerService.SetpointsControllerService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SetpointsController {

    @RequestMapping("/setpoints")
    public List<Integer> getSetpoints(){
        return SetpointsControllerService.getSetpoints();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/setpoints/increase/{zoneNum}")
    public int increaseSP(@PathVariable String zoneNum){
        return SetpointsControllerService.increaseSP(Integer.valueOf(zoneNum));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/setpoints/decrease/{zoneNum}")
    public int decreaseSP(@PathVariable String zoneNum){
        return SetpointsControllerService.decreaseSP(Integer.valueOf(zoneNum));
    }

}

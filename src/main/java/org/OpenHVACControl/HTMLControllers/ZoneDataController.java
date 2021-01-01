package org.OpenHVACControl.HTMLControllers;

import org.OpenHVACControl.ControllerService.ZoneDataControllerService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ZoneDataController {

    @RequestMapping("/zonedata/aliases")
    public List<String> getAliases(){
        return ZoneDataControllerService.getAliases();
    }
    
    @RequestMapping("/zonedata/modes")
    public List<String> getModes(){
        return ZoneDataControllerService.getModes();
    }

    @RequestMapping("/zonedata/fans")
    public List<Boolean> getFansRequested(){
        return ZoneDataControllerService.getFanRequests();
    }
                                                           
    @RequestMapping(method = RequestMethod.PUT, value = "/zonedata/modes/{zoneNum}/{newMode}")
    public void setMode(@PathVariable String zoneNum, @PathVariable String newMode){
        ZoneDataControllerService.setMode(Integer.valueOf(zoneNum), newMode);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/zonedata/fans/{zoneNum}")
    public void setFanRequested(@PathVariable String zoneNum){
        ZoneDataControllerService.setFanRequests(Integer.valueOf(zoneNum));
    }

}

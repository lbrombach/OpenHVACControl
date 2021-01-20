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
        ZoneDataControllerService.setMode(Integer.parseInt(zoneNum), newMode);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/zonedata/fans/{zoneNum}")
    public void setFanRequested(@PathVariable String zoneNum){
        ZoneDataControllerService.setFanRequests(Integer.parseInt(zoneNum));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/zonedata/occupancy/{zoneNum}/{isOccupied}")
    public void setOccupancy(@PathVariable String zoneNum, @PathVariable String isOccupied){
        ZoneDataControllerService.setOccupancy(Integer.parseInt(zoneNum), isOccupied);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/zonedata/stagediffs/{zoneNum}/{stageNum}")
    public int getStageDiffs(@PathVariable String zoneNum, @PathVariable String stageNum){
        int stage = Integer.parseInt(stageNum);
        int zone = Integer.parseInt(zoneNum);
        return ZoneDataControllerService.getStageDiffs(stage).get(zone - 1);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/zonedata/zonesettings")
    public List<String> getZoneSettings(){
        return ZoneDataControllerService.getZoneSettings();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/zonedata/zonesettings/{zoneNum}")
    public String getSingleZoneSettings(@PathVariable String zoneNum){
        return ZoneDataControllerService.getZoneSettings(Integer.parseInt(zoneNum));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/zonedata/applysettings/{zoneData}")
    public void setZoneSettings(@PathVariable String zoneData){
        System.out.println("in controller  "+ zoneData);
        ZoneDataControllerService.applySettings(zoneData);
    }

}

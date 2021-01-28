package org.OpenHVACControl.HTMLControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import java.io.File;
import java.util.Scanner;



@Controller
public class WebPageController {
    @RequestMapping("/")
    public String getIndex(){
        return  "index.html";
    }

    @RequestMapping("/zone1")
    public String getZone1() { return "zone1.html";}
    
    @RequestMapping("/zone2")
    public String getZone2() { return "zone2.html";}

    @RequestMapping("/zone3")
    public String getZone3() { return "zone3.html";}

    @RequestMapping("/settings")
    public String getSettings(){
        return  "settings.html";
    }

    @RequestMapping("/zonesettings")
    public String getZoneSettings(){
        return  "zonesettings.html";
    }

    @RequestMapping("/sensorSetup")
    public String getSensorsSetup(){
        return  "sensorSetup.html";
    }

    @RequestMapping("/commonZoneDataFun.js")
    public String getCommonZoneDataFun(){
        return  "commonZoneDataFun.js";
    }
    
    
    @RequestMapping("/zone/{zoneNum}")
    @ResponseBody
    public String getZonePage(@PathVariable String zoneNum) {
        String template = "If you can read this something went wrong. \ndid zone.html get erased?";
        try{
            template = String.valueOf(new Scanner(new File("src/main/resources/templates/zone.html")).useDelimiter("\\Z").next());        
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return template; //.replace("XXX", zoneNum);
    }  
    

}


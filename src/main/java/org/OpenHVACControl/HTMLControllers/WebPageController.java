package org.OpenHVACControl.HTMLControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



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

    @RequestMapping("/commonFun.js")
    public String getCommonFun(){
        return  "commonFun.js";
    }

    @RequestMapping("/commonZoneFun.js")
    public String getcommonZoneFun(){
        return  "commonZoneFun.js";
    }

}


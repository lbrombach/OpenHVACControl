package org.OpenHVACControl.HTMLControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class WebPageController {
    @RequestMapping("/")
    public String getIndex(){
        return  "index.html";
    }

    @RequestMapping("/settings")
    public String getSettings(){
        return  "settings.html";
    }

    @RequestMapping("/sensorSetup")
    public String getSensorsSetup(){
        return  "sensorSetup.html";
    }
    
    @RequestMapping("/commonFun.js")
    public String getCommonFun(){
        return  "commonFun.js";
    }

}


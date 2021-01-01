package org.OpenHVACControl.HTMLControllers;

import org.OpenHVACControl.ControllerService.TempsControllerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TempsController {

    @RequestMapping("/temps")
    public List<Integer> getTemps(){
        return TempsControllerService.getTemps();
    }

}

package org.OpenHVACControl.HTMLControllers;

import org.OpenHVACControl.ControllerService.OutputsControllerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OutputsController {

    @RequestMapping("/outputs")
    public List<Boolean> getOutputs(){
        return OutputsControllerService.getOutputs();
    }
}

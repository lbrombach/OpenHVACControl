package org.KiplingHeat.HTMLControllers;

import org.KiplingHeat.ControllerService.ZoneDataControllerService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@RestController
public class SettingsController {



    @RequestMapping("/settings/ip")
    public String getIP(){

            String ip="";
    try {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // filters out 127.0.0.1 and inactive interfaces
            if (iface.isLoopback() || !iface.isUp())
                continue;

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while(addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                ip = addr.getHostAddress();
                System.out.println(iface.getDisplayName() + " " + ip);
            }
        }
    } catch (SocketException e) {
        throw new RuntimeException(e);
    }
    return ip;
    }
    

}

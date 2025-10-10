package com.example.fastfoodsimulator.controllers;

import com.example.fastfoodsimulator.services.CustomerGenerator;
import com.example.fastfoodsimulator.services.KitchenService;
import com.example.fastfoodsimulator.services.Server;
import com.example.fastfoodsimulator.services.WaiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulation")
public class SimulationController {
    @Autowired
    private CustomerGenerator customerGenerator;
    @Autowired
    private WaiterService waiterService;
    @Autowired
    private KitchenService kitchenService;
    @Autowired
    private Server server;
    private boolean isRunning = false;
    @GetMapping("/start")
    public void startSimulation(@RequestParam int kitchenCompletionTime, @RequestParam int waiterServingTime, @RequestParam int customerArrivalTime, @RequestParam int waiterCount, @RequestParam int cookCount){
        System.out.println(customerArrivalTime+" "+waiterServingTime);
        if(isRunning){
            return;
        }
        customerGenerator.setInterval(customerArrivalTime);
        waiterService.setInterval(waiterServingTime);
        kitchenService.setInterval(kitchenCompletionTime);
        waiterService.setWaiterCount(waiterCount);
        kitchenService.setCookCount(cookCount);
        customerGenerator.start();
        waiterService.start();
        kitchenService.start();
        server.start();
        isRunning = true;
    }
    @GetMapping("/end")
    public void endSimulation(){
        if (!isRunning){
            return;
        }
        customerGenerator.stop();
        waiterService.stop();
        kitchenService.stop();
        server.stop();
        isRunning = false;
    }
    @GetMapping("/status")
    public String getSimulationStatus(){
        return Boolean.toString(isRunning);
    }
}

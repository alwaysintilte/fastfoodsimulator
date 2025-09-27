package com.example.fastfoodsimulator.controllers;

import com.example.fastfoodsimulator.services.CustomerGenerator;
import com.example.fastfoodsimulator.services.Kitchen;
import com.example.fastfoodsimulator.services.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/simulation")
public class SimulationController {
    private ExecutorService executorService;
    @Autowired
    private CustomerGenerator generator;
    @Autowired
    private Kitchen kitchen;
    @Autowired
    private Server server;
    private boolean isRunning = false;
    @GetMapping("/start")
    public void startSimulation(@RequestParam int kitchenCompletionTime, @RequestParam int customerArrivalTime){
        if(isRunning){
            return;
        }
        executorService = Executors.newFixedThreadPool(3);
        generator.setInterval(customerArrivalTime);
        kitchen.setInterval(kitchenCompletionTime);
        generator.start();
        kitchen.start();
        server.start();
        executorService.submit(generator);
        executorService.submit(kitchen);
        executorService.submit(server);
        isRunning = true;
    }
    @GetMapping("/end")
    public void endSimulation(){
        if (!isRunning){
            return;
        }
        generator.stop();
        kitchen.stop();
        server.stop();
        executorService.shutdownNow();
        isRunning = false;
    }
    @GetMapping("/status")
    public String getSimulationStatus(){
        return Boolean.toString(isRunning);
    }
}

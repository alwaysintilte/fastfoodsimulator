package com.example.fastfoodsimulator.controllers;

import com.example.fastfoodsimulator.services.CustomerGenerator;
import com.example.fastfoodsimulator.services.KitchenService;
import com.example.fastfoodsimulator.services.Server;
import com.example.fastfoodsimulator.services.WaiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/simulation")
public class SimulationController {
    private ExecutorService executorService;
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
    public void startSimulation(@RequestParam int kitchenCompletionTime, @RequestParam int waiterServingTime, @RequestParam int customerArrivalTime){
        System.out.println(customerArrivalTime+" "+waiterServingTime);
        if(isRunning){
            return;
        }
        executorService = Executors.newFixedThreadPool(4);
        customerGenerator.setInterval(customerArrivalTime);
        waiterService.setInterval(waiterServingTime);
        kitchenService.setInterval(kitchenCompletionTime);
        customerGenerator.start();
        waiterService.start();
        kitchenService.start();
        server.start();
        executorService.submit(customerGenerator);
        executorService.submit(waiterService);
        executorService.submit(kitchenService);
        executorService.submit(server);
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
        executorService.shutdownNow();
        isRunning = false;
    }
    @GetMapping("/status")
    public String getSimulationStatus(){
        return Boolean.toString(isRunning);
    }
}

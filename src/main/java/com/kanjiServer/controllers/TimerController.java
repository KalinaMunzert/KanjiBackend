package com.kanjiServer.controllers;

import com.kanjiServer.services.TimerService;
import com.kanjiServer.timer.TimerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clock")
@CrossOrigin(origins = "http://localhost:5173")
public class TimerController implements TimerListener {

    private final TimerService timerService;

    @Autowired
    public TimerController(TimerService timerService) {
        this.timerService = timerService;
    }

    @PostMapping("/start/{duration}")
    public void startTimer(@PathVariable int duration) {
        System.out.println("Timer.start");
        timerService.startTimer(duration, this);
    }

    @GetMapping("/timer")
    public int onTimeUpdate() {
        return timerService.getTimeLeft();
    }
}

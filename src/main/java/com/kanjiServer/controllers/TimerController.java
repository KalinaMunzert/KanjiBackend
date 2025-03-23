package com.kanjiServer.controllers;

import com.kanjiServer.kanji.Word;
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

    @PostMapping("/start")
    public void startTimer(@RequestParam int duration) {
        timerService.startTimer(duration, this);
    }

    @Override
    @GetMapping("/game-over")
    public boolean onTimeUp() {
        return true;
    }

    @Override
    @GetMapping("/timer")
    public int onTimeUpdate() {
        return timerService.getTimeLeft();
    }

    @PostMapping("/word-created")
    public String onWordCreated(String word) {
        timerService.addTime(10000); // 10 sec
        return word;
    }
}

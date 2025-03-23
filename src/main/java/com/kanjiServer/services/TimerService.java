package com.kanjiServer.services;

import com.kanjiServer.kanji.Word;
import com.kanjiServer.kanji.WordRepository;
import com.kanjiServer.timer.GameTimer;
import com.kanjiServer.timer.TimerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class TimerService {
    private GameTimer timer;

    public void startTimer(int duration, TimerListener listener) {
        if (timer != null) {
            timer.stop();
        }
        timer = new GameTimer(duration, listener);
        timer.start();
    }

    public int getTimeLeft() {
        if (timer != null) {
            return timer.getTimeLeft();
        }
        return -1;
    }

    public void addTime(int addedTime) {
        if (timer != null) {
            timer.addTime(addedTime);
        }
    }
}

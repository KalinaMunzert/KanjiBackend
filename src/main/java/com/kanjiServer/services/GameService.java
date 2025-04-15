package com.kanjiServer.services;

import com.kanjiServer.board.GameBoard;
import com.kanjiServer.board.Tile;
import com.kanjiServer.documents.Word;
import com.kanjiServer.timer.TimerListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GameService {

    private GameBoard board;
    private final WordService wordService;
    private final TimerService timerService;

    public GameService(WordService wordService, TimerService timerService) {
        System.out.println("Service.constructor");
        this.timerService = timerService;
        this.wordService = wordService;
    }

    public ArrayList<Word> handleMove(String direction) {
        System.out.println("Service.handleMove");
        return board.moveTiles(direction);
    }

    public Tile[][] getCurrentBoard() {
        System.out.println("Service.getCurrentBoard");
        return board.getBoard();
    }

    public void createNewGame() {
        System.out.println("Service.createNewGame");
        board = new GameBoard(wordService, timerService);
    }

    public int getScore() {
        return board.getScore();
    }
}

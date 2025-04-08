package com.kanjiServer.services;

import com.kanjiServer.board.GameBoard;
import com.kanjiServer.board.Tile;
import com.kanjiServer.documents.Word;
import com.kanjiServer.kanji.WordChecker;
import com.kanjiServer.timer.TimerListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GameService {

    private GameBoard board;
    private final WordChecker wordChecker;
    private final WordService wordService;
    private final TimerService timerService;
    private final TimerListener listener;

    public GameService(WordChecker wordChecker, WordService wordService, TimerService timerService, TimerListener listener) {
        System.out.println("Service.constructor");
        this.timerService = timerService;
        this.wordChecker = wordChecker;
        this.wordService = wordService;
        this.listener = listener;
    }

    public ArrayList<Word> handleMove(String direction) {
        System.out.println("Service.handleMove");
        ArrayList<Word> words = board.moveTiles(direction);
        board.addRandomTile();
        return words;
    }

    public Tile[][] getCurrentBoard() {
        System.out.println("Service.getCurrentBoard");
        return board.getBoard();
    }

    public void createNewGame() {
        System.out.println("Service.createNewGame");
        board = new GameBoard(wordChecker, wordService, timerService, listener);
        board.initializeBoard();
    }

    public int getScore() {
        return board.getScore();
    }
}

package com.kanjiServer.services;

import com.kanjiServer.board.GameBoard;
import com.kanjiServer.board.Tile;
import com.kanjiServer.kanji.WordChecker;
import com.kanjiServer.timer.TimerListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GameService {

//    private final ArrayList<String> possibleDirections;
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
//        this.board = createNewGame();
//        System.out.println("Board created");
//        possibleDirections = new ArrayList<>(List.of("up", "down", "left", "right"));
    }

    public ArrayList<String> handleMove(String direction) {
        System.out.println("Service.handleMove");
//        System.out.println("DEBUG DIRECTION: " + direction);
//        String[] parsed = direction.split(":");
//        System.out.println("PARSED: " + parsed[1]);
        ArrayList<String> words = board.moveTiles(direction);
//        WordChecker.checkForWords(currentGameState);
//        return words.get(0);
        board.addRandomTile();
        board.printBoard();
        board.printEmpties();
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
}

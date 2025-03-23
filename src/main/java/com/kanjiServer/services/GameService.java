package com.kanjiServer.services;

import com.google.gson.JsonParser;
import com.kanjiServer.board.GameBoard;
import com.kanjiServer.board.Tile;
import com.kanjiServer.kanji.WordChecker;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Parser;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

    private final ArrayList<String> possibleDirections;
    private GameBoard board;
    private final WordChecker wordChecker;

    public GameService(WordChecker wordChecker) {
        System.out.println("Service.constructor");
        this.wordChecker = wordChecker;
        this.board = createNewGame();
//        System.out.println("Board created");
        possibleDirections = new ArrayList<>(List.of("up", "down", "left", "right"));
    }

    public void handleMove(String direction) {
        System.out.println("Service.handleMove");
//        System.out.println("DEBUG DIRECTION: " + direction);
//        String[] parsed = direction.split(":");
//        System.out.println("PARSED: " + parsed[1]);
        board.moveTiles(direction);
//        WordChecker.checkForWords(currentGameState);
//        return words.get(0);
    }

    public Tile[][] getCurrentBoard() {
        System.out.println("Service.getCurrentBoard");
        return board.getBoard();
    }

    public GameBoard createNewGame() {
        System.out.println("Service.createNewGame");
        return new GameBoard(wordChecker);
    }
}

package com.kanjiServer.services;

import com.kanjiServer.board.GameBoard;
import com.kanjiServer.kanji.WordChecker;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("singleton")
public class GameService {

    private final ArrayList<String> possibleDirections;
    private GameBoard currentGameState;

    public GameService(GameBoard currentGameState) {
        this.currentGameState = new GameBoard();
        possibleDirections = new ArrayList<>(List.of("up", "down", "left", "right"));
    }

    public GameBoard handleMove(String direction) {
        if (!possibleDirections.contains(direction)){
            System.out.println("Invalid direction: " + direction);
            return null;
        }
        currentGameState.moveTiles(direction);
//        WordChecker.checkForWords(currentGameState);
        return currentGameState;
    }

    public GameBoard getCurrentBoard() {
        return currentGameState;
    }
}

package com.kanjiServer.controllers;

import com.kanjiServer.board.Tile;
import com.kanjiServer.documents.Word;
import com.kanjiServer.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        System.out.println("Controller.constructor");
        this.gameService = gameService;
    }

    @PostMapping("/move")
    public ArrayList<Word> move(@RequestBody String moveRequest) {
        return gameService.handleMove(moveRequest);
    }

    @GetMapping("/board")
    public Tile[][] getBoard() {
        System.out.println("Controller.getBoard");
        return gameService.getCurrentBoard();
    }

    @PostMapping("/start")
    public void startGame() {
        System.out.println("Controller.startGame");
        gameService.createNewGame();
    }

    @GetMapping("/score")
    public int getScore() {
        System.out.println("Controller.getScore");
        return gameService.getScore();
    }
}

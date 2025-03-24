package com.kanjiServer.controllers;

import com.kanjiServer.board.GameBoard;
import com.kanjiServer.board.Tile;
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
    public String move(@RequestBody String moveRequest) {
        System.out.println("Controller.move");
        ArrayList<String> a = gameService.handleMove(moveRequest);
        System.out.println("Backend: " + a.toString());
        return a.toString();
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

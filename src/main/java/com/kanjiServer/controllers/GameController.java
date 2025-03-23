package com.kanjiServer.controllers;

import com.kanjiServer.board.GameBoard;
import com.kanjiServer.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    @Autowired
        public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/move")
    public GameBoard move(@RequestBody String moveRequest) {
        return gameService.handleMove(moveRequest);
    }

    @GetMapping("/state")
    public GameBoard getBoard() {
        return gameService.getCurrentBoard();
    }
}

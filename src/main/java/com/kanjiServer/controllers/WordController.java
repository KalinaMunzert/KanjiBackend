package com.kanjiServer.controllers;

import com.kanjiServer.documents.Word;
import com.kanjiServer.services.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/word")
@CrossOrigin(origins = "http://localhost:5173")
public class WordController {
    private final WordService wordService;

    @Autowired
    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping("/data")
    public Word getWord(@RequestParam("word") String word){
        return wordService.getByWord(word);
    }
}

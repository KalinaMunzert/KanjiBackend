package com.kanjiServer.kanji;

import com.kanjiServer.services.WordService;
import org.springframework.stereotype.Component;

@Component
public class WordChecker {

    private final WordService wordService;

    public WordChecker(WordService wordService) {
        this.wordService = wordService;
    }

    public boolean isValidWord(String word) {
        return wordService.getByWord(word) != null;
    }
}

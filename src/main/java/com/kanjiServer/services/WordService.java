package com.kanjiServer.services;

import com.kanjiServer.documents.Word;
import com.kanjiServer.kanji.WordRepository;
import org.springframework.stereotype.Service;

@Service
public class WordService {

    private WordRepository wordRepository;

    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public void save(String word, String pronunciation, String definition, String[] sentences) {
        Word newWord = new Word(word, pronunciation, definition, sentences);
        wordRepository.save(newWord);
    }

    public Word getByWord(String word) { // MONGO
        return wordRepository.findByWord(word);
    }
}

package com.kanjiServer.services;

import com.kanjiServer.kanji.Word;
import com.kanjiServer.kanji.WordRepository;
import org.springframework.stereotype.Service;

@Service
public class WordService {

    private WordRepository wordRepository;

    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public void save(String word) {
        Word newWord = new Word(word);
        wordRepository.save(newWord);
    }

    public Word getByWord(String word) {
        return wordRepository.findByWord(word);
    }

//    public Word getByFirstKanji(String firstKanji) {
//        return wordRepository.findByFirstKanji(firstKanji);
//    }
//
//    public Word getBySecondKanji(String secondKanji) {
//        return wordRepository.findBySecondKanji(secondKanji);
//    }

    public Word getByPronunciation(String pronunciation) {
        return wordRepository.findByPronunciation(pronunciation);
    }

    public Word getByDefinition(String definition) {
        return wordRepository.findByDefinition(definition);
    }

    public Word getByExampleSentence(String exampleSentence) {
        return wordRepository.findByExampleSentence(exampleSentence);
    }
}

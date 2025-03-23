package com.kanjiServer.services;

import com.kanjiServer.kanji.Word;
import com.kanjiServer.kanji.WordRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class WordService {

    private WordRepository wordRepository;

    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public void save(String word, String pronunciation, String definition, ArrayList<String> sentences) {
        Word newWord = new Word(word, pronunciation, definition, sentences);
        wordRepository.save(newWord);
    }

    public Word getByWord(String word) { // MONGO
        return wordRepository.findByWord(word);
    }

//    public Word getByWord(String word) { // for Testing
//        Word found = new Word(word);
//        return found;
//    }

//    public Word getByFirstKanji(String firstKanji) {
//        return wordRepository.findByFirstKanji(firstKanji);
//    }
//
//    public Word getBySecondKanji(String secondKanji) {
//        return wordRepository.findBySecondKanji(secondKanji);
//    }

//    public Word getByPronunciation(String pronunciation) {
//        return wordRepository.findByPronunciation(pronunciation);
//    }
//
//    public Word getByDefinition(String definition) {
//        return wordRepository.findByDefinition(definition);
//    }
//
//    public Word getByExampleSentence(String exampleSentence) {
//        return wordRepository.findByExampleSentence(exampleSentence);
//    }
}

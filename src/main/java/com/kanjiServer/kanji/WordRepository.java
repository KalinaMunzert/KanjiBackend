package com.kanjiServer.kanji;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface WordRepository extends MongoRepository<Word, String> {
    Word findByDefinition(String definition);
    Word findByWord(String word);
//    Word findByFirstKanji(String firstKanji);
//    Word findBySecondKanji(String secondKanji);
    Word findByPronunciation(String pronunciation);
    Word findByExampleSentence(String exampleSentence);
}

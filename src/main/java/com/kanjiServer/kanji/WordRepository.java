package com.kanjiServer.kanji;

import com.kanjiServer.documents.Word;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WordRepository extends MongoRepository<Word, String> {
//    Word findByDefinition(String definition);
    Word findByWord(String word);
//    Word findByFirstKanji(String firstKanji);
//    Word findBySecondKanji(String secondKanji);
//    Word findByPronunciation(String pronunciation);
//    Word findBySentences(String exampleSentence);
}

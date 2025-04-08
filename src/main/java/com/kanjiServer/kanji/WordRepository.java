package com.kanjiServer.kanji;

import com.kanjiServer.documents.Word;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WordRepository extends MongoRepository<Word, String> {
    Word findByWord(String word);
}

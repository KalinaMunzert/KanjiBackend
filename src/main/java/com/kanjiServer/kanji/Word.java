package com.kanjiServer.kanji;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "words")
public class Word {

    @Id
    private String id;
    private String word;
//    private String firstKanji;
//    private String secondKanji;
    private String pronunciation;
    private String definition;
    private ArrayList<String> exampleSentence;

    public Word(String word) {
        this.word = word;
        String[] split = word.split("");
//        firstKanji = split[0];
//        secondKanji = split[1];
        ArrayList<String> detail = ApiFetcher.getDetails(word);
        pronunciation = detail.get(0);
        definition = detail.get(1);
        exampleSentence = ApiFetcher.getExampleSentence(word);
    }

    //    public Word(String firstKanji, String secondKanji, String pronunciation, String definition, String exampleSentence) {
//        this.firstKanji = firstKanji;
//        this.secondKanji = secondKanji;
//        this.pronunciation = pronunciation;
//        this.definition = definition;
//        this.exampleSentence = exampleSentence;
//    }

    public Word() {} // default constructor

    public String getWord() {
        return word;
    }

//    public String getFirstKanji() {
//        return firstKanji;
//    }
//
//    public String getSecondKanji() {
//        return secondKanji;
//    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getDefinition() {
        return definition;
    }

    public ArrayList<String> getExampleSentence() {
        return exampleSentence;
    }
}

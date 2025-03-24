package com.kanjiServer.documents;

import com.kanjiServer.kanji.ApiFetcher;
import org.springframework.data.annotation.Id;
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
    private ArrayList<String> sentences;

    public Word(String word) {
        this.word = word;
//        String[] split = word.split("");
//        firstKanji = split[0];
//        secondKanji = split[1];
        ArrayList<String> detail = ApiFetcher.getDetails(word);
        pronunciation = detail.get(0);
        definition = detail.get(1);
        sentences = ApiFetcher.getSentences(word);
    }

    public Word(String word, String pronunciation, String definition, ArrayList<String> sentences) {
//        this.firstKanji = firstKanji;
//        this.secondKanji = secondKanji;
        this.word = word;
        this.pronunciation = pronunciation;
        this.definition = definition;
        this.sentences = sentences;
    }

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

    public ArrayList<String> getSentences() {
        return sentences;
    }
}

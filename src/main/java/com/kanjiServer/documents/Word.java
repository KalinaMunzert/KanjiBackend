package com.kanjiServer.documents;

import com.kanjiServer.kanji.ApiFetcher;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "words")
public class Word {

    @Id
    private String id;
    private String word;
    private String pronunciation;
    private String definition;
    private String[] sentences;

    public Word(String word, String pronunciation, String definition, String[] sentences) {
        this.word = word;
        this.pronunciation = pronunciation;
        this.definition = definition;
        this.sentences = sentences;
    }

    // These are not used, but Mongo expects them to be there

    public Word() {} // default constructor

    public String getWord() {
        return word;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getDefinition() {
        return definition;
    }

    public String[] getSentences() {
        return sentences;
    }
}

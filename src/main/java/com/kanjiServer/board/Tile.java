package com.kanjiServer.board;

import com.kanjiServer.kanji.Word;

public class Tile {

    private String kanji;
    private int x;
    private int y;

    public Tile(String kanji, int x, int y) {
        this.kanji = kanji;
        this.x = x;
        this.y = y;
    }

    public String getKanji() {
        if (kanji == null) {
            return " ";
        }
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}

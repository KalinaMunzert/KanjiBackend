package com.kanjiServer.board;

public class Tile {
    private String kanji;
    private int x;
    private int y;
    private String color;

    public Tile(String kanji, int x, int y) {
        this.kanji = kanji;
        this.x = x;
        this.y = y;
        setColor("white");
    }

    public int[] wordMade(int score, int time) {
        time /= 1000; // convert to seconds
        int[] updates = new int[2];
        updates[0] = score + time;
        updates[1] = time;
        return updates;
    }

    public String getKanji() {
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

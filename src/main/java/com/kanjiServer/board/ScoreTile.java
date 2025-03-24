package com.kanjiServer.board;

public class ScoreTile extends Tile {
    private final int SCORE_MULTIPLIER;

    public ScoreTile(String kanji, int x, int y) {
        super(kanji, x, y);
        SCORE_MULTIPLIER = 2;
        setColor("green");
    }

    @Override
    public int[] wordMade(int score, int time) {
        time /= 1000; // convert to seconds
        int[] updates = new int[2];
        updates[0] = score + (time * SCORE_MULTIPLIER);
        updates[1] = time;
        return updates;
    }
}

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
        System.out.printf("ScoreTile: Initial = S:%d, T:%d\n", score, time);
        int[] updates = new int[2];
        updates[0] = score + (time * SCORE_MULTIPLIER);
        updates[1] = time + 10;
        System.out.printf("ScoreTile: Final = S:%d, T:%d\n", updates[0], updates[1]);
        return updates;
    }
}

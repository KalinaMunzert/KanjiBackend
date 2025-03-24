package com.kanjiServer.board;

public class TimeTile extends Tile {
    private final int TIME_DEDUCTION;

    public TimeTile(String kanji, int x, int y) {
        super(kanji, x, y);
        TIME_DEDUCTION = 30;
    }

    @Override
    public int[] wordMade(int score, int time) {
        time /= 1000; // convert to seconds
        int[] updates = new int[2];
        updates[0] = score + time;
        updates[1] = time - TIME_DEDUCTION;
        return updates;
    }
}

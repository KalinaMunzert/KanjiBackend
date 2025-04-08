package com.kanjiServer.board;

public class TimeTile extends Tile {
    private final int TIME_DEDUCTION;

    public TimeTile(String kanji, int x, int y) {
        super(kanji, x, y);
        TIME_DEDUCTION = 30;
        setColor("red");
    }

    @Override
    public int[] wordMade(int score, int time) {
        System.out.printf("TimeTile: Initial = S:%d, T:%d\n", score, time);
        int[] updates = new int[2];
        updates[0] = score + time;
        updates[1] = time - TIME_DEDUCTION;
        System.out.printf("TimeTile: Final = S:%d, T:%d\n", updates[0], updates[1]);
        return updates;
    }
}

package com.kanjiServer.board;

import com.kanjiServer.kanji.ApiFetcher;
import com.kanjiServer.kanji.KanjiListTypes;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Scope("singleton")
public class GameBoard {

    // everything that has implementation works, except the little bug in the board initializer

    private Tile[][] board;
    private int score;
    private List<String> recentWords;
    private Random random = new Random();
    private List<int[]> emptyCells;

    public GameBoard() {
        board = new Tile[4][4];
        score = 0;
        recentWords = new ArrayList<>();
        emptyCells = new ArrayList<>();
        initializeEmptyCells();
        initializeBoard();
    }

    public void moveTiles(String direction) {
        //TODO
    }

    public void addWord(String word){
        recentWords.add(word);
    }

    private void initializeBoard() { // this runs twice for some strange reason
        System.out.println("Initializing Board...");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = null;
            }
        }
        Tile firstTile = addRandomTile(KanjiListTypes.GRADE_2);
        String firstKanji = firstTile.getKanji();
        System.out.println("First Kanji: " + firstKanji);
        System.out.printf("First Cords: (%d, %d)\n", firstTile.getX(), firstTile.getY());
        ArrayList<String> firstKanjiWords = ApiFetcher.searchWordsByKanji(firstKanji);
        if (!firstKanjiWords.isEmpty()) {
            String secondKanji;
            String word = firstKanjiWords.get(random.nextInt(firstKanjiWords.size()));
            if (String.valueOf(word.charAt(1)).equals(firstKanji)) {
                secondKanji = String.valueOf(word.charAt(0));
            } else {
                secondKanji = String.valueOf(word.charAt(1));
            }
            Tile secondTile = addRandomTile(secondKanji);
            System.out.println("Second Kanji: " + secondTile.getKanji());
            System.out.printf("Second Cords: (%d, %d)\n", secondTile.getX(), secondTile.getY());
        } else {
            System.out.println("so this is awkward");
        }
    }

    private Tile addRandomTile(KanjiListTypes listType) {
        Tile tile = null;
        if (!emptyCells.isEmpty()) {
            int[] pos = emptyCells.get(random.nextInt(emptyCells.size()));
            tile = new Tile(ApiFetcher.getRandomKanji(listType), pos[0], pos[1]);
            emptyCells.remove(pos);
            board[pos[0]][pos[1]] = tile;
        }
        return tile;
    }

    private Tile addRandomTile(String kanji) {
        Tile tile = null;
        if (!emptyCells.isEmpty()) {
            int[] pos = emptyCells.get(random.nextInt(emptyCells.size()));
            tile = new Tile(kanji, pos[0], pos[1]);
            emptyCells.remove(pos);
            board[pos[0]][pos[1]] = tile;
        }
        return tile;
    }

    private void initializeEmptyCells() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                emptyCells.add(new int[]{i, j});
            }
        }
    }
}

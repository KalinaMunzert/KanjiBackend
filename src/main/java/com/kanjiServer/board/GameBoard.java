package com.kanjiServer.board;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.kanjiServer.kanji.ApiFetcher;
import com.kanjiServer.kanji.ListTypes;
import com.kanjiServer.kanji.WordChecker;
import com.kanjiServer.kanji.WordRepository;
import com.kanjiServer.services.TimerService;
import com.kanjiServer.services.WordService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@JsonIgnoreProperties({"emptyCells", "nextTiles", "wordChecker", "random"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class GameBoard {

    // everything that has implementation works, except the little bug in the board initializer

    private Tile[][] board;
    private List<String> recentWords;
    private List<int[]> emptyCells;
    private ArrayList<String> nextTiles;
    private final WordChecker wordChecker;
    private Random random = new Random();

    public GameBoard(WordChecker wordChecker) {
        System.out.println("Board.constructor");
        this.wordChecker = wordChecker;
        board = new Tile[4][4];
        recentWords = new ArrayList<>();
        emptyCells = new ArrayList<>();
        nextTiles = new ArrayList<>();
        initializeEmptyCells();
        initializeBoard();
    }

    public void moveTiles(String direction) {
        System.out.println("Board.moveTiles");
        ArrayList<String> words = new ArrayList<>();
        switch (direction) {
            case "up" -> {
                for (int col = 0; col < 4; col++) {
                    shiftColumnUp(col);
                }
            }
            case "down" -> {
                for (int col = 0; col < 4; col++) {
                    shiftColumnDown(col);
                }
            }
            case "left" -> {
                for (int row = 0; row < 4; row++) {
                    shiftRowLeft(row);
                }
            }
            case "right" -> {
                for (int row = 0; row < 4; row++) {
                    shiftRowRight(row);
                }
            }
        }
        addRandomTile();
        System.out.println(printBoard());
    }

    private void shiftColumnUp(int col) {
        int moveTo = 0;
        ArrayList<String> words = new ArrayList<>();
        for (int row = 0; row < 4; row++) {
            if (board[row][col] == null) {
                continue;
            }
            String first = board[row][col].getKanji();
            String second = board[row + 1][col].getKanji();
            String word1 = first + second;
            String word2 = second + first;
            if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                wordCreated(validWord);
                words.add(validWord);
                System.out.println("Valid Word: " + validWord);
                board[row][col] = null;
                emptyCells.add(new int[]{row, col});
                board[row + 1][col] = null;
                emptyCells.add(new int[]{row, col});
                row++; // skip next tile because it already merged
            } else {
                if (row != moveTo) {
                    board[row][col] = null;
                    emptyCells.add(new int[]{row, col});
                    board[moveTo][col] = new Tile(first, moveTo, col);
                    emptyCells.remove(new int[]{moveTo, col});
                }
                moveTo++;
            }
        }
    }

    private void shiftColumnDown(int col) {
        int moveTo = 3;
        ArrayList<String> words = new ArrayList<>();
        for (int row = 3; row > -1; row--) {
            if (board[row][col] == null) {
                continue;
            }
            String first = board[row][col].getKanji();
            String second = board[row - 1][col].getKanji();
            String word1 = first + second;
            String word2 = second + first;
            if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                wordCreated(validWord);
                words.add(validWord);
                board[row][col] = null;
                board[row - 1][col] = null;
                row--; // skip next tile because it already merged
            } else {
                if (row != moveTo) {
                    board[row][col] = null;
                    board[moveTo][col] = new Tile(first, moveTo, col);
                }
                moveTo--;
            }
        }
    }

    private void shiftRowLeft(int row) {
        int moveTo = 0;
        ArrayList<String> words = new ArrayList<>();
        for (int col = 0; col < 4; col++) {
            if (board[row][col] == null) {
                continue;
            }
            String first = board[row][col].getKanji();
            String second = board[row][col + 1].getKanji();
            String word1 = first + second;
            String word2 = second + first;
            if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                wordCreated(validWord);
                words.add(validWord);
                board[row][col] = null;
                board[row][col + 1] = null;
                col++; // skip next tile because it already merged
            } else {
                if (row != moveTo) {
                    board[row][col] = null;
                    board[row][moveTo] = new Tile(first, row, moveTo);
                }
                moveTo++;
            }
        }
    }

    private void shiftRowRight(int row) {
        int moveTo = 3;
        ArrayList<String> words = new ArrayList<>();
        for (int col = 3; col > -1; col--) {
            if (board[row][col] == null) {
                continue;
            }
            String first = board[row][col].getKanji();
            String second = board[row][col - 1].getKanji();
            String word1 = first + second;
            String word2 = second + first;
            if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                wordCreated(validWord);
                words.add(validWord);
                board[row][col] = null;
                board[row][col - 1] = null;
                col--; // skip next tile because it already merged
            } else {
                if (row != moveTo) {
                    board[row][col] = null;
                    board[row][moveTo] = new Tile(first, row, moveTo);
                }
                moveTo--;
            }
        }
    }

    private String printBoard() {
        System.out.println("Board.print");
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < 4; i++){
            string.append(String.format("%s | %s | %s | %s", getKanji(board[i][0]), getKanji(board[i][1]), getKanji(board[i][2]), getKanji(board[i][3])));
            string.append("\n-----------------\n");
        }
        return string.toString();
    }

    private String getKanji(Tile tile) {
        if (tile == null || tile.getKanji() == null) {
            return " ";
        }
        return tile.getKanji();
    }

    public void wordCreated(String word){
        System.out.println("Board.wordCreated");
        recentWords.add(word);
        ArrayList<String> pair = getKanjiPair();
        nextTiles.add(pair.get(0));
        nextTiles.add(pair.get(1));
    }

    public Tile[][] getBoard() {
        System.out.println("Board.getBoard");
        return board;
    }

    private void initializeBoard() { // this runs twice for some strange reason
        System.out.println("Board.initialize");
        System.out.println("Initializing Board...");
        // initializes board with all null Tiles
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = null;
            }
        }

        ArrayList<String> pair = getKanjiPair();

        System.out.println("-----------");
        Tile firstTile = addRandomTile(pair.get(0));
        System.out.println("First Kanji: " + pair.get(0));
        System.out.printf("First Cords: (%d, %d)\n", firstTile.getX(), firstTile.getY());
        Tile secondTile = addRandomTile(pair.get(1));
        System.out.println("Second Kanji: " + pair.get(1));
        System.out.printf("Second Cords: (%d, %d)\n", secondTile.getX(), secondTile.getY());
        System.out.println("-----------");

        pair = getKanjiPair();
        nextTiles.add(pair.get(0));
        nextTiles.add(pair.get(1));
        pair = getKanjiPair();
        nextTiles.add(pair.get(0));
        nextTiles.add(pair.get(1));
    }

    private ArrayList<String> getKanjiPair() {
        System.out.println("Board.getPair");
        ArrayList<String> pair = new ArrayList<>();

        String firstKanji = ApiFetcher.getRandomKanji(ListTypes.GRADE_2);
        ArrayList<String> wordList = ApiFetcher.searchWordsByKanji(firstKanji);
        // Rerolls firstKanji if no words start with that character
        while (wordList.contains("Reroll")) {
            System.out.println("FirstKanji: " + firstKanji + "Rerolling List");
            firstKanji = ApiFetcher.getRandomKanji(ListTypes.GRADE_2);
            wordList = ApiFetcher.searchWordsByKanji(firstKanji);
            System.out.println("Rerolled list");
        }

        String word = wordList.get(random.nextInt(wordList.size()));
        if (!word.substring(0, 1).equals(firstKanji)) {
            firstKanji = word.substring(0, 1);
        }
        String secondKanji = word.substring(1, 2);

        pair.add(firstKanji);
        pair.add(secondKanji);
        return pair;
    }

    private Tile addRandomTile(String kanji) {
        System.out.println("Board.addTile (kanji)");
        Tile tile = null;
        if (!emptyCells.isEmpty()) {
            int[] pos = emptyCells.get(random.nextInt(emptyCells.size()));
            tile = new Tile(kanji, pos[0], pos[1]);
            emptyCells.remove(pos);
            board[pos[0]][pos[1]] = tile;
        }
        return tile;
    }

    private Tile addRandomTile() {
        System.out.println("Board.addTile (no kanji)");
        Tile tile = null;
        int index;
        if (nextTiles.size() < 3) {
            index = nextTiles.size() - 1;
            ArrayList<String> pair = getKanjiPair();
            nextTiles.add(pair.get(0));
            nextTiles.add(pair.get(1));
        } else {
            index = 2;
        }
        if (!emptyCells.isEmpty()) {
            int[] pos = emptyCells.get(random.nextInt(emptyCells.size()));
            tile = new Tile(nextTiles.get(index), pos[0], pos[1]);
            nextTiles.remove(index);
            emptyCells.remove(pos);
            board[pos[0]][pos[1]] = tile;
        }
        return tile;
    }

    private void initializeEmptyCells() {
        System.out.println("Board.initializeEmpties");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                emptyCells.add(new int[]{i, j});
            }
        }
    }
}

package com.kanjiServer.board;

import com.google.gson.JsonParser;
import com.kanjiServer.documents.Word;
import com.kanjiServer.kanji.ApiFetcher;
import com.kanjiServer.kanji.ListTypes;
import com.kanjiServer.services.TimerService;
import com.kanjiServer.services.WordService;

import java.util.ArrayList;
import java.util.Random;

public class GameBoard {

    private Tile[][] board;
    private int score;
    private ArrayList<String> nextTiles;
    private int emptyCellCount;
    private final WordService wordService;
    private final TimerService timerService;
    private final Random random = new Random();
    private final ArrayList<String> grade2Kanji;

    public GameBoard(WordService wordService, TimerService timerService) {
        this.timerService = timerService;
        this.wordService = wordService;
        score = 0;
        grade2Kanji = ApiFetcher.getKanjiList(ListTypes.GRADE_2);
        initializeBoard();
    }

    public ArrayList<Word> moveTiles(String directionJSON) {
        String direction = JsonParser.parseString(directionJSON).getAsJsonObject().get("direction").getAsString();
        ArrayList<Word> words = new ArrayList<>();
        switch (direction) {
            case "up" -> {
                for (int col = 0; col < 4; col++) {
                    words.addAll(shiftColumn(col, 0));
                }
            }
            case "down" -> {
                for (int col = 0; col < 4; col++) {
                    words.addAll(shiftColumn(col, 3));
                }
            }
            case "left" -> {
                for (int row = 0; row < 4; row++) {
                    words.addAll(shiftRow(row, 0));
                }
            }
            case "right" -> {
                for (int row = 0; row < 4; row++) {
                    words.addAll(shiftRow(row, 3));
                }
            }
        }
        addRandomTile();
        return words;
    }

    public Tile[][] getBoard() {
        printBoard();
        return board;
    }

    public int getScore() {
        return score;
    }

    private void initializeBoard() {
        board = new Tile[4][4];
        nextTiles = new ArrayList<>();
        emptyCellCount = 16;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = new Tile("", i, j);
            }
        }

        int i = random.nextInt(4);
        int j = random.nextInt(4);
        board[i][j] = new ScoreTile("", i, j);
        i = random.nextInt(4);
        j = random.nextInt(4);
        board[i][j] = new TimeTile("", i, j);

        String[] pair = getKanjiPair();

        System.out.println("-----------");
        addRandomTile(pair[0]);
        System.out.println("First Kanji: " + pair[0]);
        addRandomTile(pair[1]);
        System.out.println("Second Kanji: " + pair[1]);
        System.out.println("-----------");

        pair = getKanjiPair();
        nextTiles.add(pair[0]);
        nextTiles.add(pair[1]);

        pair = getKanjiPair();
        nextTiles.add(pair[0]);
        nextTiles.add(pair[1]);
    }

    private ArrayList<Word> shiftColumn(int col, int rowStart) {
        int moveTo = rowStart;
        ArrayList<Word> words = new ArrayList<>();

        int rowEnd = rowStart == 0 ? 4 : -1;
        int rowStep = rowStart == 0 ? 1 : -1;

        for (int row = rowStart; row != rowEnd; row += rowStep) {
            Tile firstTile = board[row][col];

            // Skip empty tiles
            if (firstTile.getKanji().isEmpty()) {
                continue;
            }
            String firstKanji = firstTile.getKanji();

            // Merge if possible
            int nextRow = row + rowStep;
            if (nextRow >= 0 && nextRow < 4) {
                Tile secondTile = board[nextRow][col];
                String secondKanji = secondTile.getKanji();

                Word word1 = wordService.getByWord(firstKanji + secondKanji);
                Word word2 = wordService.getByWord(secondKanji + firstKanji);

                if (word1 != null || word2 != null) {
                    Word validWord = word1 != null ? word1 : word2;
                    addNextKanji();
                    firstTile.setKanji("");
                    secondTile.setKanji("");
                    emptyCellCount += 2;

                    words.add(validWord);
                    int time = timerService.getTimeLeft();
                    int[] info = firstTile.wordMade(score, time);
                    score = info[0];
                    timerService.addTime(info[1] - time);
                    row += rowStep; // Skip next tile since it has merged
                    continue;
                }
            }

            // Move tile if necessary
            if (row != moveTo) {
                board[row][col].setKanji("");
                board[moveTo][col].setKanji(firstKanji);
            }
            moveTo += rowStep;
        }
        return words;
    }

    private ArrayList<Word> shiftRow(int row, int colStart) {
        int moveTo = colStart;
        ArrayList<Word> words = new ArrayList<>();

        int colEnd = colStart == 0 ? 4 : -1;
        int colStep = colStart == 0 ? 1 : -1;

        for (int col = colStart; col != colEnd; col += colStep) {
            Tile tile = board[row][col];

            // Skip empty tiles
            if (tile.getKanji().isEmpty()) {
                continue;
            }
            String firstKanji = tile.getKanji();

            // Merge if possible
            int nextCol = col + colStep;
            if (nextCol >= 0 && nextCol < 4) {
                Tile secondTile = board[row][nextCol];
                String secondKanji = secondTile.getKanji();

                Word word1 = wordService.getByWord(firstKanji + secondKanji);
                Word word2 = wordService.getByWord(secondKanji + firstKanji);

                if (word1 != null || word2 != null) {
                    Word validWord = word1 != null ? word1 : word2;
                    addNextKanji();
                    tile.setKanji("");
                    secondTile.setKanji("");
                    emptyCellCount += 2;

                    words.add(validWord);
                    int time = timerService.getTimeLeft();
                    int[] info = tile.wordMade(score, time);
                    score = info[0];
                    timerService.addTime(info[1] - time);
                    col += colStep; // Skip next tile since it has merged
                    continue;
                }
            }

            // Move tile if necessary
            if (col != moveTo) {
                board[row][col].setKanji("");
                board[row][moveTo].setKanji(firstKanji);
            }
            moveTo += colStep;
        }
        return words;
    }

    private void printBoard() {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < 4; i++){
            string.append(String.format("%s | %s | %s | %s", board[i][0].getKanji(), board[i][1].getKanji(), board[i][2].getKanji(), board[i][3].getKanji()));
            string.append("\n-----------------\n");
        }
        System.out.println(string);
    }

    private void addNextKanji() {
        String[] pair = getKanjiPair();
        nextTiles.add(pair[0]);
        nextTiles.add(pair[1]);
    }

    private String[] getKanjiPair() {
        String[] pair = new String[2];

        String firstKanji = grade2Kanji.get(random.nextInt(grade2Kanji.size()));
        ArrayList<String> wordList = ApiFetcher.searchWordsByKanji(firstKanji);
        if (wordList.isEmpty()) {
            System.out.println("Original Kanji: " + firstKanji + " Re rolling List");
            return getKanjiPair();
        }
        String word = wordList.get(random.nextInt(wordList.size()));

        String[] sentences = ApiFetcher.getSentences(word);
        if (sentences[0] == null || sentences[1] == null) {
            System.out.println("No sentences: " + word);
            return getKanjiPair();
        }
        String[] details = ApiFetcher.getDetails(word);
        String pronunciation = details[0];
        String definition = details[1];

        wordService.save(word, pronunciation, definition, sentences);
        if (!word.substring(0, 1).equals(firstKanji)) {
            firstKanji = word.substring(0, 1);
        }
        String secondKanji = word.substring(1, 2);

        pair[0] = firstKanji;
        pair[1] = secondKanji;
        return pair;
    }

    private void addRandomTile() {
        int index;
        if (nextTiles.size() < 3) {
            index = nextTiles.size() - 1;
            addNextKanji();
        } else {
            index = 2;
        }

        if (emptyCellCount != 0) {
            int i = random.nextInt(4);
            int j = random.nextInt(4);
            while (!board[i][j].getKanji().isEmpty()) {
                i = random.nextInt(4);
                j = random.nextInt(4);
            }
            String next = nextTiles.get(index);
            board[i][j].setKanji(next);
            nextTiles.remove(index);
            emptyCellCount--;
        }
    }

    private void addRandomTile(String kanji) {
        int i = random.nextInt(4);
        int j = random.nextInt(4);
        Tile tile = board[i][j];
        tile.setKanji(kanji);
        emptyCellCount--;
    }
}
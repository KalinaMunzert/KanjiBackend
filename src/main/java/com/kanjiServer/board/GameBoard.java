package com.kanjiServer.board;

import com.google.gson.JsonParser;
import com.kanjiServer.documents.Word;
import com.kanjiServer.kanji.ApiFetcher;
import com.kanjiServer.kanji.ListTypes;
import com.kanjiServer.kanji.WordChecker;
import com.kanjiServer.services.TimerService;
import com.kanjiServer.services.WordService;
import com.kanjiServer.timer.TimerListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameBoard {

    private Tile[][] board;
    private int score;
    private List<String> recentWords;
    private boolean[][] emptyCells;
    private ArrayList<String> nextTiles;
    private final WordChecker wordChecker;
    private final WordService wordService;
    private final TimerService timerService;
    private final TimerListener listener;
    private final Random random = new Random();
    private int[] scoreTileCoords;
    private  int[] timeTileCoords;

    public GameBoard(WordChecker wordChecker, WordService wordService, TimerService timerService, TimerListener listener) {
        this.listener = listener;
        this.timerService = timerService;
        this.wordChecker = wordChecker;
        this.wordService = wordService;
        score = 0;
    }

    public ArrayList<Word> moveTiles(String directionJSON) {
        String direction = JsonParser.parseString(directionJSON).getAsJsonObject().get("direction").getAsString();
        ArrayList<Word> words = new ArrayList<>();
        switch (direction) {
            case "up" -> {
                for (int col = 0; col < 4; col++) {
                    words.addAll(shiftColumnUp(col));
                }
            }
            case "down" -> {
                for (int col = 0; col < 4; col++) {
                    words.addAll(shiftColumnDown(col));
                }
            }
            case "left" -> {
                for (int row = 0; row < 4; row++) {
                    words.addAll(shiftRowLeft(row));
                }
            }
            case "right" -> {
                for (int row = 0; row < 4; row++) {
                    words.addAll(shiftRowRight(row));
                }
            }
        }
        return words;
    }

    private ArrayList<Word> shiftColumnUp(int col) {
        boolean merged;
        int moveTo = 0;
        ArrayList<Word> words = new ArrayList<>();
        for (int row = 0; row < 4; row++) {
            merged = false;
            Tile tile = board[row][col];
            if (tile.getKanji().isEmpty()) {
                continue;
            }
            String firstKanji = tile.getKanji();
            if (row < 3) {
                Tile second = board[row + 1][col];
                String secondKanji = second.getKanji();
                String word1 = firstKanji + secondKanji;
                String word2 = secondKanji + firstKanji;
                if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                    String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                    wordCreated(validWord);
                    tile.setKanji("");
                    second.setKanji("");

                    updateEmptyCells();
                    words.add(wordService.getByWord(validWord));
                    int time = timerService.getTimeLeft();
                    int[] info = tile.wordMade(score, time);
                    score = info[0];
                    int addedTime = info[1] - time;
                    timerService.addTime(addedTime);
                    row++; // skip next tile because it already merged
                    merged = true;
                }
            }
            if (row != moveTo && !merged) { // if tile can move and row has not changed
                translateTile(row, col, moveTo, firstKanji, true);
            }
            moveTo++;
        }
        return words;
    }

    private ArrayList<Word> shiftColumnDown(int col) {
        boolean merged;
        int moveTo = 3;
        ArrayList<Word> words = new ArrayList<>();
        for (int row = 3; row > -1; row--) {
            merged = false;
            Tile tile = board[row][col];
            if (tile.getKanji().isEmpty()) {
                continue;
            }
            String firstKanji = tile.getKanji();
            if (row > 1) {
                Tile secondTile = board[row - 1][col];
                String secondKanji = secondTile.getKanji();
                String word1 = firstKanji + secondKanji;
                String word2 = secondKanji + firstKanji;
                if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                    String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                    wordCreated(validWord);
                    tile.setKanji("");
                    secondTile.setKanji("");

                    words.add(wordService.getByWord(validWord));
                    int time = timerService.getTimeLeft();
                    int[] info = tile.wordMade(score, time);
                    score = info[0];
                    int addedTime = info[1] - time;
                    timerService.addTime(addedTime);
                    updateEmptyCells();
                    row--; // skip next tile because it already merged
                    merged = true;
                }
            }
            if (row != moveTo && !merged) { // if tile can move and row has not changed
                translateTile(row, col, moveTo, firstKanji, true);
            }
            moveTo--;
        }
        return words;
    }

    private ArrayList<Word> shiftRowLeft(int row) {
        boolean merged;
        int moveTo = 0;
        ArrayList<Word> words = new ArrayList<>();
        for (int col = 0; col < 4; col++) {
            merged = false;
            Tile tile = board[row][col];
            if (tile.getKanji().isEmpty()) {
                continue;
            }
            String firstKanji = tile.getKanji();
            if (col < 3) {
                Tile secondTile = board[row][col + 1];
                String Kanji = secondTile.getKanji();
                String word1 = firstKanji + Kanji;
                String word2 = Kanji + firstKanji;
                if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                    String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                    wordCreated(validWord);
                    tile.setKanji("");
                    secondTile.setKanji("");

                    words.add(wordService.getByWord(validWord));
                    int time = timerService.getTimeLeft();
                    int[] info = tile.wordMade(score, time);
                    score = info[0];
                    int addedTime = info[1] - time;
                    timerService.addTime(addedTime);

                    updateEmptyCells();
                    col++;
                    merged = true;
                }
            }
            if (col != moveTo && !merged) {
                translateTile(row, col, moveTo, firstKanji, false);
            }
            moveTo++;
        }
        return words;
    }

    private ArrayList<Word> shiftRowRight(int row) {
        boolean merged;
        int moveTo = 3;
        ArrayList<Word> words = new ArrayList<>();
        for (int col = 3; col > -1; col--) {
            merged = false;
            Tile tile = board[row][col];
            if (tile.getKanji().isEmpty()) {
                continue;
            }
            String firstKanji = tile.getKanji();
            if (col > 1) {
                String second = board[row][col - 1].getKanji();
                String word1 = firstKanji + second;
                String word2 = second + firstKanji;
                if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                    String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                    wordCreated(validWord);
                    tile.setKanji("");
                    board[row][col - 1].setKanji("");

                    words.add(wordService.getByWord(validWord));
                    int time = timerService.getTimeLeft();
                    int[] info = tile.wordMade(score, time);
                    score = info[0];
                    int addedTime = info[1] - time;
                    timerService.addTime(addedTime);
                    updateEmptyCells();
                    col--;
                    merged = true;
                }
            }
            if (col != moveTo && !merged) {
                translateTile(row, col, moveTo, firstKanji, false);
            }
            moveTo--;
        }
        return words;
    }

    public void printBoard() {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < 4; i++){
            string.append(String.format("%s | %s | %s | %s", board[i][0].getKanji(), board[i][1].getKanji(), board[i][2].getKanji(), board[i][3].getKanji()));
            string.append("\n-----------------\n");
        }
        System.out.println(string);
    }

    private void translateTile(int row, int col, int moveTo, String first, boolean isVert) {
        board[row][col].setKanji("");
        if (isVert) {
            int[] newCoords = new int[] {moveTo, col};
            if (Arrays.equals(newCoords, scoreTileCoords)) {
                board[moveTo][col] = new ScoreTile(first, moveTo, col);
            } else if (Arrays.equals(newCoords, timeTileCoords)) {
                board[moveTo][col] = new TimeTile(first, moveTo, col);
            } else {
                board[moveTo][col] = new Tile(first, moveTo, col);
            }
        } else {
            int [] newCoords = new int[] {row, moveTo};
            if (Arrays.equals(newCoords, scoreTileCoords)) {
                board[row][moveTo] = new ScoreTile(first, row, moveTo);
            } else if (Arrays.equals(newCoords, timeTileCoords)) {
                board[row][moveTo] = new TimeTile(first, row, moveTo);
            } else {
                board[row][moveTo] = new Tile(first, row, moveTo);
            }
        }
        updateEmptyCells();
    }

    public void wordCreated(String word){
        recentWords.add(word);
        addNextKanji();
        listener.onWordCreated(word);
    }

    private void addNextKanji() {
        ArrayList<String> pair = getKanjiPair();
        nextTiles.add(pair.get(0));
        nextTiles.add(pair.get(1));
    }

    public Tile[][] getBoard() {
        printBoard();
        return board;
    }

    public void initializeBoard() {
        board = new Tile[4][4];
        recentWords = new ArrayList<>();
        emptyCells = new boolean[4][4];
        nextTiles = new ArrayList<>();
        initializeEmptyCells();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = new Tile("", i, j);
            }
        }

        int i = random.nextInt(4);
        int j = random.nextInt(4);
        board[i][j] = new ScoreTile("", i, j);
        scoreTileCoords = new int[] {i, j};
        i = random.nextInt(4);
        j = random.nextInt(4);
        board[i][j] = new TimeTile("", i, j);
        timeTileCoords = new int[] {i, j};

        ArrayList<String> pair = getKanjiPair();

        System.out.println("-----------");
        addRandomTile(pair.get(0));
        System.out.println("First Kanji: " + pair.get(0));
        addRandomTile(pair.get(1));
        System.out.println("Second Kanji: " + pair.get(1));
        System.out.println("-----------");

        pair = getKanjiPair();
        nextTiles.add(pair.get(0));
        nextTiles.add(pair.get(1));

        pair = getKanjiPair();
        nextTiles.add(pair.get(0));
        nextTiles.add(pair.get(1));
    }

    private ArrayList<String> getKanjiPair() {
        ArrayList<String> pair = new ArrayList<>();

        String firstKanji = ApiFetcher.getRandomKanji(ListTypes.GRADE_2);
        ArrayList<String> wordList = ApiFetcher.searchWordsByKanji(firstKanji);
        if (wordList.isEmpty()) {
            System.out.println("Original Kanji: " + firstKanji + " Re rolling List");
            return getKanjiPair();
        }
        String word = wordList.get(random.nextInt(wordList.size()));

        ArrayList<String> sentences = ApiFetcher.getSentences(word);
        if (sentences.isEmpty()) {
            System.out.println("No sentences: " + word);
            return getKanjiPair();
        }
        ArrayList<String> details = ApiFetcher.getDetails(word);
        String pronunciation = details.get(0);
        String definition = details.get(1);

        wordService.save(word, pronunciation, definition, sentences);
        if (!word.substring(0, 1).equals(firstKanji)) {
            firstKanji = word.substring(0, 1);
        }
        String secondKanji = word.substring(1, 2);

        pair.add(firstKanji);
        pair.add(secondKanji);
        return pair;
    }

    private void addRandomTile(String kanji) {
        int i = random.nextInt(4);
        int j = random.nextInt(4);
        Tile tile = board[i][j];
        tile.setKanji(kanji);
        updateEmptyCells();
    }

    public void addRandomTile() {
        int index;
        if (nextTiles.size() < 3) {
            index = nextTiles.size() - 1;
            addNextKanji();
        } else {
            index = 2;
        }

        if (hasEmptyCells()) {
            int i = random.nextInt(4);
            int j = random.nextInt(4);
            while (!emptyCells[i][j]) {
                i = random.nextInt(4);
                j = random.nextInt(4);
            }
            String next = nextTiles.get(index);
            Tile tile = board[i][j];
            tile.setKanji(next);
            nextTiles.remove(index);
            board[i][j] = tile;
            updateEmptyCells();
        }
    }

    private void initializeEmptyCells() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                emptyCells[i][j] = true;
            }
        }
    }

    private void updateEmptyCells() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                emptyCells[i][j] = board[i][j].getKanji().isEmpty();
            }
        }
    }

    private boolean hasEmptyCells() {
        for (boolean[] row : emptyCells) {
            for (boolean empty : row) {
                if (empty) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getScore() {
        return score;
    }
}
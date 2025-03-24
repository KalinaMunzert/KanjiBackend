package com.kanjiServer.board;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.gson.JsonParser;
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

@JsonIgnoreProperties({"emptyCells", "nextTiles", "wordChecker", "random"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class GameBoard {

    // everything that has implementation works, except the little bug in the board initializer

    private Tile[][] board;
    private int score;
    private List<String> recentWords;
    private boolean[][] emptyCells;
    private ArrayList<String> nextTiles;
    private final WordChecker wordChecker;
    private final WordService wordService;
    private final TimerService timerService;
    private final TimerListener listener;
    private Random random = new Random();

    public GameBoard(WordChecker wordChecker, WordService wordService, TimerService timerService, TimerListener listener) {
        System.out.println("Board.constructor");
        this.listener = listener;
        this.timerService = timerService;
        this.wordChecker = wordChecker;
        this.wordService = wordService;
        score = 0;
    }

    public ArrayList<String> moveTiles(String directionJSON) {
        System.out.println("Board.moveTiles");
        System.out.println("DirectionJSON: " + directionJSON);
        String direction = JsonParser.parseString(directionJSON).getAsJsonObject().get("direction").getAsString();
        System.out.println("DirectionPARSED: " + direction);
        ArrayList<String> words = new ArrayList<>();
        switch (direction) {
            case "up" -> {
                for (int col = 0; col < 4; col++) {
                    words = shiftColumnUp(col);
                }
            }
            case "down" -> {
                for (int col = 0; col < 4; col++) {
                    words = shiftColumnDown(col);
                }
            }
            case "left" -> {
                for (int row = 0; row < 4; row++) {
                    words = shiftRowLeft(row);
                }
            }
            case "right" -> {
                for (int row = 0; row < 4; row++) {
                    words = shiftRowRight(row);
                }
            }
        }
        System.out.println("Length (main): " + words.size());
        System.out.println("Score: " + score);
        return words;
    }

    public void printEmpties() {
        System.out.println(Arrays.deepToString(emptyCells));
    }

    private ArrayList<String> shiftColumnUp(int col) {
        boolean merged;
        int moveTo = 0;
        ArrayList<String> words = new ArrayList<>();
        System.out.printf("----------SHIFT COLUMN UP: COL=%d----------\n", col);
        for (int row = 0; row < 4; row++) {
            merged = false;
            Tile tile = board[row][col];
            if (tile.getKanji().isEmpty()) {
                System.out.printf("(%d, %d) is null\n", row, col);
                continue;
            }
            String firstKanji = tile.getKanji();
            System.out.printf("(%d, %d) has %s (first)\n", row, col, firstKanji);
            if (row < 3) {
                Tile second = board[row + 1][col];
                String secondKanji = second.getKanji();
                System.out.printf("(%d, %d) has %s (second)\n", row, col, secondKanji);
                String word1 = firstKanji + secondKanji;
                System.out.println("Word1: " + word1);
                String word2 = secondKanji + firstKanji;
                System.out.println("Word2: " + word2);
                if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                    String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                    wordCreated(validWord);
                    System.out.println("Valid Word: " + validWord);
                    tile.setKanji("");;
                    second.setKanji("");

                    updateEmptyCells();
                    words.add(validWord);
                    int time = timerService.getTimeLeft();
                    int[] info = tile.wordMade(score, time);
                    score = info[0];
                    timerService.addTime(info[1] - time);

                    System.out.println("Length: " + words.size());
                    row++; // skip next tile because it already merged
                    merged = true;
                } else {
                    System.out.println("Not valid word");
                }
            }
            if (row != moveTo && !merged) { // if tile can move and row has not changed
                System.out.println("Translating Tile");
                translateTile(row, col, moveTo, firstKanji, true);
            } else {
                System.out.println("Tile can't move");
            }
            moveTo++;
        }
        return words;
    }

    private ArrayList<String> shiftColumnDown(int col) {
        boolean merged;
        int moveTo = 3;
        ArrayList<String> words = new ArrayList<>();
        System.out.printf("----------SHIFT COLUMN DOWN: COL=%d----------\n", col);
        for (int row = 3; row > -1; row--) {
            merged = false;
            Tile tile = board[row][col];
            if (tile.getKanji().isEmpty()) {
                System.out.printf("(%d, %d) is null\n", row, col);
                continue;
            }
            String firstKanji = tile.getKanji();
            System.out.printf("(%d, %d) has %s (first)\n", row, col, firstKanji);
            if (row > 1) {
                Tile secondTile = board[row - 1][col];
                String secondKanji = secondTile.getKanji();
                System.out.printf("(%d, %d) has %s (second)\n", row, col, secondKanji);
                String word1 = firstKanji + secondKanji;
                System.out.println("Word1: " + word1);
                String word2 = secondKanji + firstKanji;
                System.out.println("Word2: " + word2);
                if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                    String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                    wordCreated(validWord);
                    System.out.println("Valid Word: " + validWord);
                    tile.setKanji("");
                    secondTile.setKanji("");

                    words.add(validWord);
                    int time = timerService.getTimeLeft();
                    int[] info = tile.wordMade(score, time);
                    score = info[0];
                    updateEmptyCells();
                    row--; // skip next tile because it already merged
                    merged = true;
                } else {
                    System.out.println("Not valid word");
                }
            }
            if (row != moveTo && !merged) { // if tile can move and row has not changed
                System.out.println("Translating tile");
                translateTile(row, col, moveTo, firstKanji, true);
            } else {
                System.out.println("Tile can't move");
            }
            moveTo--;
        }
        return words;
    }

    private ArrayList<String> shiftRowLeft(int row) {
        boolean merged;
        int moveTo = 0;
        ArrayList<String> words = new ArrayList<>();
        System.out.printf("----------SHIFT COLUMN LEFT: ROW=%d----------\n", row);
        for (int col = 0; col < 4; col++) {
            merged = false;
            Tile tile = board[row][col];
            if (tile.getKanji().isEmpty()) {
                System.out.printf("(%d, %d) is null\n", row, col);
                continue;
            }
            String firstKanji = tile.getKanji();
            System.out.printf("(%d, %d) has %s (first)\n", row, col, firstKanji);
            if (col < 3) {
                Tile secondTile = board[row - 1][col];
                String Kanji = secondTile.getKanji();
                System.out.printf("(%d, %d) has %s (second)\n", row, col, Kanji);
                String word1 = firstKanji + Kanji;
                System.out.println("Word1: " + word1);
                String word2 = Kanji + firstKanji;
                System.out.println("Word2: " + word2);
                if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                    String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                    wordCreated(validWord);
                    System.out.println("Valid Word: " + validWord);
                    tile.setKanji("");
                    secondTile.setKanji("");

                    words.add(validWord);
                    int time = timerService.getTimeLeft();
                    int[] info = tile.wordMade(score, time);
                    score = info[0];                    updateEmptyCells();
                    col++; // skip next tile because it already merged
                    merged = true;
                } else {
                    System.out.println("Not valid word");
                }
            }
            if (col != moveTo && !merged) { // if tile can move and row has not changed
                System.out.println("Translating Tile");
                translateTile(row, col, moveTo, firstKanji, false);
            } else {
                System.out.println("Tile can't move");
            }
            moveTo++;
        }
        return words;
    }

    private ArrayList<String> shiftRowRight(int row) {
        boolean merged;
        int moveTo = 3;
        ArrayList<String> words = new ArrayList<>();
        System.out.printf("----------SHIFT COLUMN RIGHT: ROW=%d----------\n", row);
        for (int col = 3; col > -1; col--) {
            merged = false;
            Tile tile = board[row][col];
            if (tile.getKanji().isEmpty()) {
                System.out.printf("(%d, %d) is null\n", row, col);
                continue;
            }
            String firstKanji = tile.getKanji();
            System.out.printf("(%d, %d) has %s (first)\n", row, col, firstKanji);
            if (col > 1) {
                String second = board[row][col - 1].getKanji();
                System.out.printf("(%d, %d) has %s (second)\n", row, col, second);
                String word1 = firstKanji + second;
                System.out.println("Word1: " + word1);
                String word2 = second + firstKanji;
                System.out.println("Word2: " + word2);
                if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                    String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                    wordCreated(validWord);
                    System.out.println("Valid Word: " + validWord);
                    tile.setKanji("");
                    board[row][col - 1].setKanji("");

                    words.add(validWord);
                    int time = timerService.getTimeLeft();
                    int[] info = tile.wordMade(score, time);
                    score = info[0];                    updateEmptyCells();
                    col--; // skip next tile because it already merged
                    merged = true;
                } else {
                    System.out.println("Not valid word");
                }
            }
            if (col != moveTo && !merged) { // if tile can move and row has not changed
                System.out.println("Translating tile");
                translateTile(row, col, moveTo, firstKanji, false);
            } else {
                System.out.println("Tile can't move");
            }
            moveTo--;
        }
        return words;
    }

    public void printBoard() {
        System.out.println("Board.print");
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < 4; i++){
            string.append(String.format("%s | %s | %s | %s", board[i][0].getKanji(), board[i][1].getKanji(), board[i][2].getKanji(), board[i][3].getKanji()));
            string.append("\n-----------------\n");
        }
        System.out.println(string);
    }

    private void translateTile(int row, int col, int moveTo, String first, boolean isVert) {
        System.out.printf("(%d, %d) can move to %d\n", row, col, moveTo);
        board[row][col].setKanji("");
        System.out.printf("(%d, %d) made null\n", row, col);
        if (isVert) {
            board[moveTo][col] = new Tile(first, moveTo, col);
            System.out.printf("New tile with %s at (%d, %d)\n", first, moveTo, col);
        } else {
            board[row][moveTo] = new Tile(first, row, moveTo);
            System.out.printf("New tile with %s at (%d, %d)\n", first, row, moveTo);
        }
        updateEmptyCells();
    }

    public void wordCreated(String word){
        System.out.println("Board.wordCreated");
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
        System.out.println("Board.getBoard");
        return board;
    }

    public void initializeBoard() {
        board = new Tile[4][4];
        recentWords = new ArrayList<>();
        emptyCells = new boolean[4][4];
        nextTiles = new ArrayList<>();
        initializeEmptyCells();
//        initializeBoard();
        System.out.println("Board.initialize");
//        System.out.println("Initializing Board...");
//        initializeEmptyCells();

        // initializes board with all null Tiles
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

        ArrayList<String> pair = getKanjiPair();

        System.out.println("-----------");
        addRandomTile(pair.get(0));
        System.out.println("First Kanji: " + pair.get(0));
//        System.out.printf("First Cords: (%d, %d)\n", firstTile.getX(), firstTile.getY());
        addRandomTile(pair.get(1));
        System.out.println("Second Kanji: " + pair.get(1));
//        System.out.printf("Second Cords: (%d, %d)\n", secondTile.getX(), secondTile.getY());
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
        while (wordList.isEmpty()) {
            System.out.println("Original Kanji: " + firstKanji + " Rerolling List");
            return getKanjiPair();
//            System.out.println("Rerolled list");
        }
        String word = wordList.get(random.nextInt(wordList.size()));

        ArrayList<String> sentences = ApiFetcher.getSentences(word);
        if (sentences.isEmpty()) {
            System.out.println("No sentences: " + word);
            return getKanjiPair(); // gets new pair
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
        System.out.println("Board.addTile (kanji)");
        int i = random.nextInt(4);
        int j = random.nextInt(4);
        Tile tile = board[i][j];
        tile.setKanji(kanji);
        updateEmptyCells();
    }

    public void addRandomTile() {
        System.out.println("Board.addTile (no kanji)");
        int index;
        System.out.println("Next Tiles: " + nextTiles.toString());
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
            System.out.println("Next tile: " + next);
            Tile tile = board[i][j];
            tile.setKanji(next);
            nextTiles.remove(index);
            board[i][j] = tile;
            updateEmptyCells();
        }
    }

    private void initializeEmptyCells() {
        System.out.println("Board.initializeEmpties");
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
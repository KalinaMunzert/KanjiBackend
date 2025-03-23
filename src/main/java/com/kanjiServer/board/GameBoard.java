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
import org.bson.internal.BsonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@JsonIgnoreProperties({"emptyCells", "nextTiles", "wordChecker", "random"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class GameBoard {

    // everything that has implementation works, except the little bug in the board initializer

    private Tile[][] board;
    private List<String> recentWords;
//    private List<int[]> emptyCells;
    private boolean[][] emptyCells;
    private ArrayList<String> nextTiles;
    private final WordChecker wordChecker;
    private final WordService wordService;
//    private final TimerService timerService;
    private final TimerListener listener;
    private Random random = new Random();

    public GameBoard(WordChecker wordChecker, WordService wordService, TimerService timerService, TimerListener listener) {
        this.listener = listener;
        System.out.println("Board.constructor");
//        this.timerService = timerService;
        this.wordChecker = wordChecker;
        this.wordService = wordService;
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
//                return words;
            }
            case "down" -> {
                for (int col = 0; col < 4; col++) {
                    words = shiftColumnDown(col);
                }
//                return words;
            }
            case "left" -> {
                for (int row = 0; row < 4; row++) {
                    words = shiftRowLeft(row);
                }
//                return words;
            }
            case "right" -> {
                for (int row = 0; row < 4; row++) {
                    words = shiftRowRight(row);
                }
//                return words;
            }
        }
        System.out.println("Length (main): " + words.size());
        return words;
//        addRandomTile();
//        System.out.println(printBoard());
//        System.out.println(printEmpties());
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
            if (board[row][col] == null) {
                System.out.printf("(%d, %d) is null\n", row, col);
                continue;
            }
            String first = board[row][col].getKanji();
            System.out.printf("(%d, %d) has %s (first)\n", row, col, first);
            if (row < 3) {
                String second = getKanji(board[row + 1][col]);
                System.out.printf("(%d, %d) has %s (second)\n", row, col, second);
                String word1 = first + second;
                System.out.println("Word1: " + word1);
                String word2 = second + first;
                System.out.println("Word2: " + word2);
                if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                    String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                    wordCreated(validWord);
                    System.out.println("Valid Word: " + validWord);
                    board[row][col] = null;
                    board[row + 1][col] = null;

                    updateEmptyCells();
                    words.add(validWord);
                    System.out.println("Length: " + words.size());
                    row++; // skip next tile because it already merged
                    merged = true;
                } else {
                    System.out.println("Not valid word");
                }
            }
            if (row != moveTo && !merged) { // if tile can move and row has not changed
                System.out.println("Translating Tile");
                translateTile(row, col, moveTo, first, true);
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
            if (board[row][col] == null) {
                System.out.printf("(%d, %d) is null\n", row, col);
                continue;
            }
            String first = board[row][col].getKanji();
            System.out.printf("(%d, %d) has %s (first)\n", row, col, first);
            if (row > 1) {
                String second = getKanji(board[row - 1][col]);
                System.out.printf("(%d, %d) has %s (second)\n", row, col, second);
                String word1 = first + second;
                System.out.println("Word1: " + word1);
                String word2 = second + first;
                System.out.println("Word2: " + word2);
                if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                    String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                    wordCreated(validWord);
                    System.out.println("Valid Word: " + validWord);
                    board[row][col] = null;
                    board[row - 1][col] = null;

                    words.add(validWord);
                    updateEmptyCells();
                    row--; // skip next tile because it already merged
                    merged = true;
                } else {
                    System.out.println("Not valid word");
                }
            }
            if (row != moveTo && !merged) { // if tile can move and row has not changed
                System.out.println("Translating tile");
                translateTile(row, col, moveTo, first, true);
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
            if (board[row][col] == null) {
                System.out.printf("(%d, %d) is null\n", row, col);
                continue;
            }
            String first = board[row][col].getKanji();
            System.out.printf("(%d, %d) has %s (first)\n", row, col, first);
            if (col < 3) {
                String second = getKanji(board[row][col + 1]);
                System.out.printf("(%d, %d) has %s (second)\n", row, col, second);
                String word1 = first + second;
                System.out.println("Word1: " + word1);
                String word2 = second + first;
                System.out.println("Word2: " + word2);
                if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                    String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                    wordCreated(validWord);
                    System.out.println("Valid Word: " + validWord);
                    board[row][col] = null;
                    board[row][col + 1] = null;

                    words.add(validWord);
                    updateEmptyCells();
                    col++; // skip next tile because it already merged
                    merged = true;
                } else {
                    System.out.println("Not valid word");
                }
            }
            if (col != moveTo && !merged) { // if tile can move and row has not changed
                System.out.println("Translating Tile");
                translateTile(row, col, moveTo, first, false);
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
            if (board[row][col] == null) {
                System.out.printf("(%d, %d) is null\n", row, col);
                continue;
            }
            String first = board[row][col].getKanji();
            System.out.printf("(%d, %d) has %s (first)\n", row, col, first);
            if (col > 1) {
                String second = getKanji(board[row][col - 1]);
                System.out.printf("(%d, %d) has %s (second)\n", row, col, second);
                String word1 = first + second;
                System.out.println("Word1: " + word1);
                String word2 = second + first;
                System.out.println("Word2: " + word2);
                if (wordChecker.isValidWord(word1) || wordChecker.isValidWord(word2)) {
                    String validWord = wordChecker.isValidWord(word1) ? word1 : word2;
                    wordCreated(validWord);
                    System.out.println("Valid Word: " + validWord);
                    board[row][col] = null;
                    board[row][col - 1] = null;

                    words.add(validWord);
                    updateEmptyCells();
                    col--; // skip next tile because it already merged
                    merged = true;
                } else {
                    System.out.println("Not valid word");
                }
            }
            if (col != moveTo && !merged) { // if tile can move and row has not changed
                System.out.println("Translating tile");
                translateTile(row, col, moveTo, first, false);
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
            string.append(String.format("%s | %s | %s | %s", getKanji(board[i][0]), getKanji(board[i][1]), getKanji(board[i][2]), getKanji(board[i][3])));
            string.append("\n-----------------\n");
        }
        System.out.println(string);
    }

    private void translateTile(int row, int col, int moveTo, String first, boolean isVert) {
        System.out.printf("(%d, %d) can move to %d\n", row, col, moveTo);
        board[row][col] = null;
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

    private String getKanji(Tile tile) {
        if (tile == null || tile.getKanji() == null) {
            return "";
        }
        return tile.getKanji();
    }

    public void wordCreated(String word){
        System.out.println("Board.wordCreated");
        recentWords.add(word);
        addNextTiles();
        listener.onWordCreated(word);
    }

    private void addNextTiles() {
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

    private Tile addRandomTile(String kanji) {
        System.out.println("Board.addTile (kanji)");
        Tile tile = null;
        if (hasEmptyCells()) {
            int i = random.nextInt(4);
            int j = random.nextInt(4);
            while (!emptyCells[i][j]) {
                i = random.nextInt(4);
                j = random.nextInt(4);
            }
            tile = new Tile(kanji, i, j);
            board[i][j] = tile;
            updateEmptyCells();
        }
        return tile;
    }

    public void addRandomTile() {
        System.out.println("Board.addTile (no kanji)");
        Tile tile = null;
        int index;
        System.out.println("Next Tiles: " + nextTiles.toString());
        if (nextTiles.size() < 3) {
            index = nextTiles.size() - 1;
            addNextTiles();
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
            tile = new Tile(next, i, j);
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
                emptyCells[i][j] = board[i][j] == null;
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
}
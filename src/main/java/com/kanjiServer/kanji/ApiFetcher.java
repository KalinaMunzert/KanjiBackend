package com.kanjiServer.kanji;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ApiFetcher {

    //everything api related works now

    private static String apiUrl;
    private static URL url;
    private static HttpURLConnection connection;
    private static BufferedReader reader;

    // TODO These need to go in database?
    private final static HashSet<String> kanjiGrade1 = new HashSet<>(getKanjiList(KanjiListTypes.GRADE_1));
    private final static HashSet<String> kanjiGrade2 = new HashSet<>(getKanjiList(KanjiListTypes.GRADE_2));
    private final static HashSet<String> kanjiGrade3 = new HashSet<>(getKanjiList(KanjiListTypes.GRADE_3));
    private final static HashSet<String> kanjiGrades1to3 = new HashSet<>();

    static {
        kanjiGrades1to3.addAll(kanjiGrade1);
        kanjiGrades1to3.addAll(kanjiGrade2);
        kanjiGrades1to3.addAll(kanjiGrade3);
    }

    public static ArrayList<String> getKanjiList(KanjiListTypes listType) { //TODO convert to hashset?
        ArrayList<String> kanjiList = new ArrayList<>();
        try {
            apiUrl = "https://kanjiapi.dev/v1/kanji/" + listType.getValue();
            url = new URL(apiUrl);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            kanjiList = new ArrayList<>();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            reader.close();
            for (int i = 0; i < jsonArray.size(); i++) {
                kanjiList.add(jsonArray.get(i).getAsString());
            }
            return kanjiList;
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return kanjiList;
    }

    public static ArrayList<String> searchWordsByKanji(String kanji) {
        ArrayList<String> words = new ArrayList<>();
        try {
            apiUrl = "https://kanjiapi.dev/v1/words/" + URLEncoder.encode(kanji, StandardCharsets.UTF_8);
            url = new URL(apiUrl);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 400) {
                System.out.println(connection.getResponseMessage());
                return words;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject wordObject = jsonArray.get(i).getAsJsonObject();
//                System.out.println(wordObject);
                if (wordObject.has("variants")) {
                    JsonArray variants = wordObject.getAsJsonArray("variants");
                    String written = variants.get(0).getAsJsonObject().get("written").getAsString();
                    String char0 = String.valueOf(written.charAt(0));
                    String char1 = String.valueOf(written.charAt(1));
                    if (written.length() == 2 && kanjiGrades1to3.contains(char1) && kanjiGrades1to3.contains(char0)) {
                        words.add(written);
                    }
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return words;
    }

    public static String getRandomKanji(KanjiListTypes listType) {
        ArrayList<String> kanjiList = getKanjiList(listType);
        return kanjiList.get(new Random().nextInt(kanjiList.size()));
    }

    public static String getRandomKanji(ArrayList<String> kanjiList) {
        return kanjiList.get(new Random().nextInt(kanjiList.size()));
    }
}

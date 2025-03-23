package com.kanjiServer.kanji;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ApiFetcher {

    private static Random random = new Random();
    private static String apiUrl;
    //    private final static HashSet<String> kanjiGrade1 = new HashSet<>(getKanjiList(ListTypes.GRADE_1));
//    private static ArrayList<String> kanjiGrade2;
//    private final static HashSet<String> kanjiGrade3 = new HashSet<>(getKanjiList(ListTypes.GRADE_3));
    private final static HashSet<String> allKanji = new HashSet<>();
//
    static {
        allKanji.addAll(getKanjiList(ListTypes.JOYO));
//        System.out.println("Grade 2 initialized");
    }

    public static ArrayList<String> getKanjiList(ListTypes listType) {
        ArrayList<String> kanjiList = new ArrayList<>();
        try {
            apiUrl = "https://kanjiapi.dev/v1/kanji/" + listType.getValue();

            String data = getConnectionData();
            // removes surrounding braces
            data = data.substring(1, data.length() - 1);
            String[] kanji = data.split(",");
            for (String character : kanji) {
                // adds only the kanji and not surrounding quotes.
                kanjiList.add(character.substring(1, 2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kanjiList;
    }

    public static ArrayList<String> searchWordsByKanji(String kanji) {
        ArrayList<String> words = new ArrayList<>();
        try {
            apiUrl = "https://jisho.org/api/v1/search/words?keyword=" +
                    URLEncoder.encode(kanji, StandardCharsets.UTF_8) + "%3F";

            JsonArray json = getJsonData();

            for (int i = 0; i < json.size(); i++) {
                JsonObject word = json.get(i).getAsJsonObject();
                String written = word.get("slug").getAsString();
                boolean isCommon = word.has("is_common")
                        && word.get("is_common").getAsBoolean();
                boolean isKanji = allKanji.contains(written.substring(1));
                if (isCommon && isKanji) {
                    words.add(written);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return words;
    }

    public static String getRandomKanji(ListTypes listType) {
        ArrayList<String> kanjiList = getKanjiList(ListTypes.GRADE_2);
        return kanjiList.get(random.nextInt(kanjiList.size()));
    }

    public static ArrayList<String> getDetails(String word) {
        ArrayList<String> details = new ArrayList<>();
        try {
            apiUrl = "https://jisho.org/api/v1/search/words?keyword=" + URLEncoder.encode(word, StandardCharsets.UTF_8);
            JsonObject json = getJsonData().get(0).getAsJsonObject();
            String kanaPronunciation = json.get("japanese").getAsJsonArray().get(0).getAsJsonObject().get("reading").getAsString();

            apiUrl = "https://api.romaji2kana.com/v1/to/romaji?q=" + URLEncoder.encode(kanaPronunciation, StandardCharsets.UTF_8);
            String response = getConnectionData();
            String romaji = JsonParser.parseString(response).getAsJsonObject().get("a").getAsString();
            details.add(romaji);

            String definition = json.get("senses").getAsJsonArray().get(0).getAsJsonObject().get("english_definitions").getAsJsonArray().get(0).getAsString();
            details.add(definition);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    public static ArrayList<String> getSentences(String word) {
        ArrayList<String> sentences = new ArrayList<>();
        try {
            apiUrl = "https://tatoeba.org/en/api_v0/search?query=" + word + "&from=jpn&to=eng";

//            System.out.println("Calling sentences");
            String response = getConnectionData();

            try {
                JsonArray json = JsonParser.parseString(response).getAsJsonObject().getAsJsonArray("results");
                JsonObject data = json.get(0).getAsJsonObject();
                String jpSentence = data.get("text").getAsString();
                String enSentence = data.getAsJsonArray("translations").get(0).getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
                sentences.add(jpSentence);
                sentences.add(enSentence);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return sentences;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sentences;
    }

    private static JsonArray getJsonData() throws IOException {
        String response = getConnectionData();

        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        return json.getAsJsonArray("data");
    }

    private static String getConnectionData() throws IOException {
        URL url = new URL(apiUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        StringBuffer response = new StringBuffer();
        InputStream s = connection.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(s));
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        connection.disconnect();
        return response.toString();
    }
}

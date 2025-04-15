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

    private static String apiUrl;
    private final static HashSet<String> allKanji = new HashSet<>();

    static {
        // joyo kanji are the 2000 kanji most used in daily life
        allKanji.addAll(getKanjiList(ListTypes.JOYO));
    }

    public static ArrayList<String> searchWordsByKanji(String kanji) {
        ArrayList<String> words = new ArrayList<>();
        try {
            // "%3F" is question mark
            // Gets all 2 character entries that begin with the chosen kanji
            apiUrl = "https://jisho.org/api/v1/search/words?keyword=" +
                    URLEncoder.encode(kanji, StandardCharsets.UTF_8) + "%3F";

            JsonArray json = getJishoData();

            for (int i = 0; i < json.size(); i++) {
                JsonObject word = json.get(i).getAsJsonObject();
                // "slug" = the word written in kanji
                String slug = word.get("slug").getAsString();
                // some entires don't have an is_common property
                boolean isCommon = word.has("is_common")
                        && word.get("is_common").getAsBoolean();
                //check that second character is an appropriate kanji
                boolean isKanji = allKanji.contains(slug.substring(1));
                if (isCommon && isKanji) {
                    words.add(slug);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return words;
    }

    public static String[] getDetails(String word) {
        String[] details = new String[2];
        try {
            apiUrl = "https://jisho.org/api/v1/search/words?keyword=" + URLEncoder.encode(word, StandardCharsets.UTF_8);
            JsonObject json = getJishoData().get(0).getAsJsonObject();
            String kanaPronunciation = json.get("japanese").getAsJsonArray().get(0).getAsJsonObject().get("reading").getAsString();

            apiUrl = "https://api.romaji2kana.com/v1/to/romaji?q=" + URLEncoder.encode(kanaPronunciation, StandardCharsets.UTF_8);
            String response = getConnectionData();
            String romaji = JsonParser.parseString(response).getAsJsonObject().get("a").getAsString();
            details[0] = romaji;

            String definition = json.get("senses").getAsJsonArray().get(0).getAsJsonObject().get("english_definitions").getAsJsonArray().get(0).getAsString();
            details[1] = definition;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    public static String[] getSentences(String word) {
        String[] sentences = new String[2];
        try {
            apiUrl = "https://tatoeba.org/en/api_v0/search?query=" + word + "&from=jpn&to=eng";

            String response = getConnectionData();

            try { // separate try-catch b/c this API has variations in the brackets/braces
                JsonArray json = JsonParser.parseString(response).getAsJsonObject().getAsJsonArray("results");
                JsonObject data = json.get(0).getAsJsonObject();
                String jpSentence = data.get("text").getAsString();
                String enSentence = data.getAsJsonArray("translations").get(0).getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
                sentences[0] = jpSentence;
                sentences[1] = enSentence;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return sentences;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sentences;
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

    private static JsonArray getJishoData() throws IOException {
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

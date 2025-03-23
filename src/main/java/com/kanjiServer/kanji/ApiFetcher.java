package com.kanjiServer.kanji;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.catalina.util.ToStringUtil;

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

            System.out.println("Calling .dev...");
            String data = getConnectionData();
            data = data.substring(1, data.length() - 1);
            String[] kanji = data.split(",");
            for (String character : kanji) {
                kanjiList.add(String.valueOf(character.charAt(1))); // adds only the kanji and not surrounding quotes.
            }
            if (kanjiList.isEmpty()) {
                System.out.println("List empty");
                kanjiList.add("Reroll");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kanjiList;
    }

    public static ArrayList<String> searchWordsByKanji(String kanji) {
        ArrayList<String> words = new ArrayList<>();
        try {
            apiUrl = "https://jisho.org/api/v1/search/words?keyword=" + URLEncoder.encode(kanji, StandardCharsets.UTF_8) + "%3F";

            System.out.println("Calling jisho (list)");
            JsonArray json = getJsonData();

            for (int i = 0; i < json.size(); i++) {
                JsonObject word = json.get(i).getAsJsonObject();
//                System.out.println(word);
//                System.out.println(json.get(0).getAsJsonObject().get("is_common").toString());
//                System.out.println("Word: " + word);

                // removes surrounding quotation marks
                String written = word.get("slug").getAsString();
//                String firstKanji = written.substring(0,1);
//                System.out.println("First Kanji: " + firstKanji);
//                System.out.println("Written: " + written);

//                String secondKanji = written.substring(1);
//                System.out.println("Second Kanji: " + secondKanji);

                boolean isCommon = word.has("is_common") && word.get("is_common").getAsBoolean();
//                System.out.println("Is Common: " + isCommon);

                boolean isKanji = allKanji.contains(written.substring(1));
//                System.out.println("Is Kanji: " + isKanji);
                if (isCommon && isKanji) {
                    words.add(written);
//                    System.out.println(written + " Added");
                }
            }
//            System.out.println("List: " + words);

            if (words.isEmpty()) {
                words.add("Reroll");
            }
/*
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input =
            }
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JsonArray json =
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            String json1 = json.toString();
            JsonArray j = JsonParser.parseString(json1).getAsJsonArray();
            System.out.println(j);
            System.out.println(JsonParser.parseReader(reader).isJsonObject());
            jsonArray.remove(0);
            jsonArray.remove(0); //removes "meta"
            for (int i = 0; i < 2; i++) {
                String thing = jsonArray.get(i).getAsString();
                System.out.println(thing);
            }
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.get(1).getAsString()) {

                }
            }
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return words;
//         try {
//             apiUrl = "https://kanjiapi.dev/v1/words/" + URLEncoder.encode(kanji, StandardCharsets.UTF_8);
//             url = new URL(apiUrl);
//
//             connection = (HttpURLConnection) url.openConnection();
//             connection.setRequestMethod("GET");
//
//             if (connection.getResponseCode() == 400) {
//                 System.out.println(connection.getResponseMessage());
//                 return words;
//             }
// //
// //            addToArrayList(words);
// //            if (words.isEmpty()) {
// //                return null;
// //            }
// //            return words;
//
//             reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//             JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
//             String otherKanji = "";
//             for (int i = 0; i < jsonArray.size(); i++) {
//                 JsonObject wordObject = jsonArray.get(i).getAsJsonObject();
//                 System.out.println(wordObject);
//                 if (wordObject.has("variants")) {
//                     JsonArray variants = wordObject.getAsJsonArray("variants");
//                     String written = variants.get(0).getAsJsonObject().get("written").getAsString();
//                     if (written.length() == 2) {
//                         if (String.valueOf(written.charAt(0)).equals(kanji)) {
//                             otherKanji = String.valueOf(written.charAt(1));
//                         } else if (String.valueOf(written.charAt(1)).equals(kanji)) {
//                             otherKanji = String.valueOf(written.charAt(0));
//                         }
//                         if (kanjiGrades1to3.contains(otherKanji)) {
//                             words.add(written);
//                         }
//                     }
//                 }
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         return words;
    }

    public static String getRandomKanji(ListTypes listType) {
        ArrayList<String> kanjiList = getKanjiList(ListTypes.GRADE_2);
//        if (kanjiList == null || kanjiList.isEmpty()) {
//            System.out.println("List null");
//            return null;
//        }
        return kanjiList.get(random.nextInt(kanjiList.size()));
    }

    public static ArrayList<String> getDetails(String word) {
        ArrayList<String> details = new ArrayList<>();
        try {
            apiUrl = "https://jisho.org/api/v1/search/words?keyword=" + URLEncoder.encode(word, StandardCharsets.UTF_8);
            System.out.println("Calling jisho (details)");
            JsonObject json = getJsonData().get(0).getAsJsonObject();
            String kanaPronunciation = json.get("japanese").getAsJsonArray().get(0).getAsJsonObject().get("reading").getAsString();
            System.out.println(kanaPronunciation);

            apiUrl = "https://api.romaji2kana.com/v1/to/romaji?q=" + URLEncoder.encode(kanaPronunciation, StandardCharsets.UTF_8);
            System.out.println("Calling romaji");
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

    public static ArrayList<String> getExampleSentence(String word) {
        ArrayList<String> sentence = new ArrayList<>();
        try {
            apiUrl = "https://tatoeba.org/en/api_v0/search?query=" + word + "&from=jpn&to=eng";

            System.out.println("Calling sentences");
            String response = getConnectionData();

            JsonArray json = JsonParser.parseString(response).getAsJsonObject().getAsJsonArray("results");
            JsonObject data = json.get(0).getAsJsonObject();
            String jpSentence = data.get("text").toString();
            String enSentence = data.getAsJsonArray("translations").get(0).getAsJsonArray().get(0).getAsJsonObject().get("text").toString();

            sentence.add(jpSentence);
            sentence.add(enSentence);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sentence;
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

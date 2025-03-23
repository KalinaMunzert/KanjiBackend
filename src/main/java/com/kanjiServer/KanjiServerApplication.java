package com.kanjiServer;

import com.kanjiServer.kanji.ApiFetcher;
import com.kanjiServer.kanji.ListTypes;
import com.kanjiServer.kanji.Word;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;

@SpringBootApplication
public class KanjiServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KanjiServerApplication.class, args);

//		System.out.println("\n\n\n");
//
//		Word word = new Word("今月");
//		System.out.println("Word: " + word.getWord());
//		System.out.println("Pronunciation: " + word.getPronunciation());
//		System.out.println("Definition: " + word.getDefinition());
//		System.out.println("Sentence (Japanese): " + word.getExampleSentence().get(0));
//		System.out.println("Sentence (English): " + word.getExampleSentence().get(1));
//
//		System.out.println("\n\n\n");
//		WordService.save("同調");
//		System.out.println(ApiFetcher.getDefinition("同調"));
//		ArrayList<String> sentences = ApiFetcher.getExampleSentence("同調");
//		System.out.println("JP: " + sentences.get(0));
//		System.out.println("EN: " + sentences.get(1));

//		System.out.println("Main method");
//
////		ArrayList<String> kanjiList = ApiFetcher.getKanjiList(ListTypes.GRADE_2);
////		String kanji = kanjiList.get(new Random().nextInt(kanjiList.size()));
//////		String kanji = ApiFetcher.getRandomKanji(kanjiList);
////		ArrayList<String> wordsList = ApiFetcher.searchWordsByKanji(kanji);
//
//		ArrayList<String> kanjiList = ApiFetcher.getKanjiList(ListTypes.GRADE_2);
//
////		for (String kanji : kanjiList) {
////			ArrayList<String> wordsList = ApiFetcher.searchWordsByKanji(kanji);
////			if (wordsList.contains("Reroll")) {
////				System.out.println("Kanji: " + kanji + " Words: " + wordsList);
////			}
////		}
		//TODO
//		String kanji = ApiFetcher.getRandomKanji(ListTypes.GRADE_2);
//		ArrayList<String> wordsList = ApiFetcher.searchWordsByKanji(kanji);
		//TODO
////		System.out.println(kanji);
////		String kanji = "地";
//
////		System.out.println("Kanji List: " + kanjiList);
		//TODO
//		System.out.println("Kanji: " + kanji);
//		System.out.println("Words List: " + wordsList);
		//TODO

//		System.out.println(new Word("がいや"));
	}

}

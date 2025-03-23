package com.kanjiServer;

import com.kanjiServer.board.GameBoard;
import com.kanjiServer.controllers.GameController;
import com.kanjiServer.kanji.ApiFetcher;
import com.kanjiServer.kanji.KanjiListTypes;
import com.kanjiServer.kanji.WordChecker;
import com.kanjiServer.services.GameService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@SpringBootApplication
public class KanjiServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KanjiServerApplication.class, args);

		ArrayList<String> kanjiList = ApiFetcher.getKanjiList(KanjiListTypes.GRADE_2);
		String kanji = ApiFetcher.getRandomKanji(kanjiList);
		ArrayList<String> wordsList = ApiFetcher.searchWordsByKanji(kanji);

		System.out.println("Kanji List: " + kanjiList);
		System.out.println("Kanji: " + kanji);
		System.out.println("Words List: " + wordsList);
	}

}

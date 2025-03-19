package com.kanjiServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KanjiServerApplication {

	// AS OF 3/18 01:30, I got the frontend and backend connected. The timer is null though.
	// comment
	public static void main(String[] args) {
		SpringApplication.run(KanjiServerApplication.class, args);
	}

}

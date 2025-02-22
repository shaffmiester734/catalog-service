package com.shaffersoft.git.retriever;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class GitDataRetrieverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitDataRetrieverApplication.class, args);
	}

}

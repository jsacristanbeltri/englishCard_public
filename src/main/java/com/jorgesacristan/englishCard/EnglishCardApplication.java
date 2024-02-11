package com.jorgesacristan.englishCard;

import com.jorgesacristan.englishCard.rabbit.MessageSender;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan
public class EnglishCardApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnglishCardApplication.class, args);


	}

}

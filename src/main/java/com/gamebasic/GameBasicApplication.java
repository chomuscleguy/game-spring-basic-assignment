package com.gamebasic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class GameBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameBasicApplication.class, args);
    }

}

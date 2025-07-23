package com.example.stagemate;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

//스케줄링 기능을 활성화하기 위한 어노테이션
@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.stagemate.repository")
public class StagemateApplication {

	public static void main(String[] args) {
		SpringApplication.run(StagemateApplication.class, args);
	}


}

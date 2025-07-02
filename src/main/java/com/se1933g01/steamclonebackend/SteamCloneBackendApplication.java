package com.se1933g01.steamclonebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class SteamCloneBackendApplication {
	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing() // Không lỗi nếu thiếu .env
				.load();

		// Inject env vào hệ thống Java
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		SpringApplication.run(SteamCloneBackendApplication.class, args);
	}

}

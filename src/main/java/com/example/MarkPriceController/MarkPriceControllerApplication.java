package com.example.MarkPriceController;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarkPriceControllerApplication {

	public static void main(String[] args) {
		// Запуск додатка Spring Boot
		SpringApplication.run(MarkPriceControllerApplication.class, args);

		// Встановлення шляху до драйвера Chrome
		System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

		// Налаштування параметрів браузера Chrome для відкриття у фоновому режимі
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless"); // Використовувати режим без головного вікна

		// Створення екземпляру драйвера Chrome з налаштуваннями
		WebDriver driver = new ChromeDriver(options);

		// Відкриття URL
		driver.get("http://localhost:8080/currentPrice/btc-usdt");

		// Закриття драйвера після завершення роботи
		driver.quit();
	}

}

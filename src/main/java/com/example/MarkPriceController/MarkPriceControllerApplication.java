package com.example.MarkPriceController;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MarkPriceControllerApplication {

	public static void main(String[] args) {
		// Running a Spring Boot application
		SpringApplication app = new SpringApplication(MarkPriceControllerApplication.class);
		ConfigurableApplicationContext context = app.run(args);
		BinancePriceService binancePriceService = context.getBean(BinancePriceService.class);

		boolean continueChecking = true;

		while (continueChecking) {
			// Get input from the user
			String cryptocurrencyPairs = ConsoleInputHandler.promptUserForCryptocurrencyPairs();

			// Split input into individual cryptocurrency pairs
			String[] pairs = cryptocurrencyPairs.split("\\s+");

			// Get the current rate for each cryptocurrency pair
			for (String pair : pairs) {
				String currentPrice = binancePriceService.getCurrentPrice(pair);
				System.out.println("Current price of " + pair + " is: " + currentPrice);
			}

			// User request to continue
			continueChecking = ConsoleInputHandler.promptUserForContinuation();
		}
	}
}

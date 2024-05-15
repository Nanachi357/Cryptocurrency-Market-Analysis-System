package com.example.MarkPriceController;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
@EnableScheduling
public class MarkPriceControllerApplication {

	public static void main(String[] args) {
		// Running a Spring Boot application
		SpringApplication app = new SpringApplication(MarkPriceControllerApplication.class);
		ConfigurableApplicationContext context = app.run(args);
		BinancePriceService binancePriceService = context.getBean(BinancePriceService.class);
		BinanceHistoricalDataService historicalDataService = context.getBean(BinanceHistoricalDataService.class);
		Scanner scanner = new Scanner(System.in);


		boolean continueChecking = true;

		while (continueChecking) {
			// Prompt the user for choice
			System.out.println("Choose an option:");
			System.out.println("1. Check current cryptocurrency prices");
			System.out.println("2. Get historical candlestick data");
			System.out.print("Enter your choice (1 or 2): ");
			int choice = scanner.nextInt();
			scanner.nextLine(); // Consume newline character

			switch (choice) {
				case 1 -> {
					// Get input from the user
					String cryptocurrencyPairs = ConsoleInputHandler.promptUserForCryptocurrencyPairs();
					// Split input into individual cryptocurrency pairs
					String[] pairs = cryptocurrencyPairs.split("\\s+");
					// Get the current rate for each cryptocurrency pair
					for (String pair : pairs) {
						String currentPrice = binancePriceService.getCurrentPrice(pair);
						System.out.println("Current price of " + pair + " is: " + currentPrice);
					}
				}
				case 2 -> {
					// Get input for historical candlestick data
					System.out.print("Enter symbol: ");
					String symbol = scanner.nextLine();
					System.out.print("""
							                     
							Available intervals:\s
							ONE_MINUTE
							THREE_MINUTES
							FIVE_MINUTES
							FIFTEEN_MINUTES
							HALF_HOURLY
							HOURLY
							TWO_HOURLY
							FOUR_HOURLY
							SIX_HOURLY
							EIGHT_HOURLY
							TWELVE_HOURLY
							DAILY
							THREE_DAILY
							WEEKLY
							MONTHLY
							Enter interval:""");
					String intervalString = scanner.nextLine();
					CandlestickInterval interval = CandlestickInterval.valueOf(intervalString);
					LocalDateTime startDateTime = promptForDateTime("Enter start");
					Long startTime = convertToEpochMillis(startDateTime);
					LocalDateTime endDateTime = promptForDateTime("Enter end");
					Long endTime = convertToEpochMillis(endDateTime);
					System.out.print("Enter limit (optional, default 500, max 1500): ");
					Integer limit = scanner.nextInt();

					// Retrieve and display historical candlestick data
					System.out.println("Retrieving historical candlestick data...");
					List<Candlestick> candlestickData = historicalDataService.getHistoricalCandlestickData(symbol, interval, startTime, endTime, limit);
					for (Candlestick candlestick : candlestickData) {
						System.out.println(candlestick);
					}
				}
				default -> System.out.println("Invalid choice! Please enter 1 or 2.");
			}

			// User request to continue
			System.out.println("Do you want to continue? (Y/N)");
			String continueInput = scanner.next().toUpperCase();
			continueChecking = continueInput.equals("Y");
			scanner.nextLine(); // Consume newline character
		}
		scanner.close();
	}
	private static LocalDateTime promptForDateTime(String prompt) {
		Scanner scanner = new Scanner(System.in);
		System.out.println(prompt + " time (optional):");
		System.out.print("Enter year: ");
		int year = scanner.nextInt();
		System.out.print("Enter month: ");
		int month = scanner.nextInt();
		System.out.print("Enter day: ");
		int day = scanner.nextInt();
		System.out.print("Enter hour: ");
		int hour = scanner.nextInt();
		System.out.print("Enter minute: ");
		int minute = scanner.nextInt();
		System.out.print("Enter second: ");
		int second = scanner.nextInt();
		return LocalDateTime.of(year, month, day, hour, minute, second);
	}
	private static long convertToEpochMillis(LocalDateTime localDateTime) {
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
}

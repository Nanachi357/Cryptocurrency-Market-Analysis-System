package com.example.MarkPriceController;

import com.binance.api.client.domain.market.CandlestickInterval;

import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleInputHandler {

    // Regular expression pattern to validate cryptocurrency pair input
    private static final Pattern CRYPTOCURRENCY_PAIR_PATTERN = Pattern.compile("^(BTC|ETH)USDT$");

    // Scanner object to read user input from the console
    private static final Scanner scanner = new Scanner(System.in);

    // Prompt the user to enter cryptocurrency pairs and validate the input
    public static String promptUserForCryptocurrencyPairs() {
        System.out.println("Type the exchange rates of cryptocurrency pairs you want to see:");
        System.out.println("(Available cryptocurrencies: BTCUSDT, ETHUSDT)");

        String input = scanner.nextLine().toUpperCase();
        if (input.isEmpty() || !isValidCryptocurrencyPairs(input)) {
            System.out.println("You entered incorrect data, follow the previous instruction and try again \n");
            return promptUserForCryptocurrencyPairs(); // A recursive call to get the correct input
        }


        return input;
    }

    // Validate cryptocurrency pairs input
    private static boolean isValidCryptocurrencyPairs(String input) {
        String[] pairs = input.split("\\s+");
        for (String pair : pairs) {
            if (!CRYPTOCURRENCY_PAIR_PATTERN.matcher(pair).matches()) {
                return false;
            }
        }
        return true;
    }

    // Prompt the user to decide whether to check another cryptocurrency pair
    public static boolean promptUserForContinuation() {
        System.out.println("\n Do you want to check another cryptocurrency pair? (Y/N)");
        String continueInput = scanner.nextLine().toUpperCase();
        return continueInput.equals("Y");
    }

    public static CandlestickInterval promptUserForCandlestickInterval() {
        System.out.println("Choose candlestick interval:");
        System.out.println("1 - 1 minute");
        System.out.println("3 - 3 minutes");
        System.out.println("5 - 5 minutes");
        // Додайте інші варіанти за потреби

        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                return CandlestickInterval.ONE_MINUTE;
            case 3:
                return CandlestickInterval.THREE_MINUTES;
            case 5:
                return CandlestickInterval.FIVE_MINUTES;
            default:
                throw new IllegalArgumentException("Invalid choice");
        }
    }

    public static Long promptUserForTime(String prompt) {
        System.out.println(prompt);
        System.out.println("Enter time in milliseconds:");
        return scanner.nextLong();
    }
}
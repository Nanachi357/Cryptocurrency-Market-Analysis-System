package com.example.MarkPriceController;

import com.example.MarkPriceController.service.BinanceHistoricalDataService;
import com.example.MarkPriceController.service.BinancePriceService;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@EnableScheduling
public class MarkPriceControllerApplication {
	private static final Logger logger = Logger.getLogger(MarkPriceControllerApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(MarkPriceControllerApplication.class, args);
		// Open browser window with index.html page
		openBrowser("http://localhost:8085/");
	}

	// Bean for interacting with Binance web service for historical data
	@Bean
	public BinanceHistoricalDataService binanceHistoricalDataService() {
		return new BinanceHistoricalDataService();
	}

	// Bean for interacting with Binance web service for current prices
	@Bean
	public BinancePriceService binancePriceService() {
		return new BinancePriceService();
	}

	// Adding a controller for the error page
	@Bean
	public WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addViewControllers(@NotNull ViewControllerRegistry registry) {
				registry.addViewController("/error").setViewName("error");
			}

		};
	}

	// Creating a RestTemplate bean
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	// Method to open the browser with the given URL
	private static void openBrowser(String url) {
		String os = System.getProperty("os.name").toLowerCase();
		Runtime rt = Runtime.getRuntime();

		try {
			if (os.contains("win")) {
				// Windows
				rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else if (os.contains("mac")) {
				// MacOS
				rt.exec("open " + url);
			} else if (os.contains("nix") || os.contains("nux")) {
				// Unix/Linux
				String[] browsers = {"xdg-open", "google-chrome", "firefox", "mozilla", "konqueror",
						"netscape", "opera", "links", "lynx"};
				for (String browser : browsers) {
					if (rt.exec(new String[] {"which", browser}).waitFor() == 0) {
						rt.exec(new String[] {browser, url});
						break;
					}
				}
			} else {
				logger.log(Level.WARNING, "Cannot open browser on this platform.");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An error occurred while opening the browser", e);
		}
	}

	// Configuration class for MVC
	@Configuration
	public static class MvcConfig implements WebMvcConfigurer {

		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry
					.addResourceHandler("/**")
					.addResourceLocations("classpath:/templates/");
		}
	}


}

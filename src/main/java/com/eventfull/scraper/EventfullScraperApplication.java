package com.eventfull.scraper;

import com.eventfull.scraper.service.ScrapingService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class EventfullScraperApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(EventfullScraperApplication.class, args);

        ScrapingService scrapingService = applicationContext.getBean(ScrapingService.class);

        try {
            scrapingService.scrapEventsMadeiraWebsite();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

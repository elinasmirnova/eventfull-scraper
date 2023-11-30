package com.eventfull.scraper.service;

import static com.eventfull.scraper.model.SourceNames.EVENTS_MADEIRA;
import static java.lang.System.nanoTime;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.jsoup.Jsoup.parse;

import com.eventfull.scraper.model.Category;
import com.eventfull.scraper.model.Event;
import com.eventfull.scraper.model.Source;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ScrapingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingService.class);

    private static final List<String> patterns = asList("dd/MM/yyyy HH:mm", "dd/MM/yyyy H:mm");

    private final EventService eventService;

    private final SourceService sourceService;

    private final CategoryService categoryService;

    public void scrapEventsMadeiraWebsite() throws IOException {
        Document doc;
        Source source = sourceService.findSourceByTitle(EVENTS_MADEIRA.getSourceName());

        String mainPageHtml = getHtmlFromWebsite("https://eventsmadeira.com/en/event-listing/");

        doc = parse(mainPageHtml);
        Elements elements = doc.getElementsByClass("gt-event-style-3");

        LOGGER.info("Got {} events from Events Madeira website, starting to process...", elements.size());
        long startTime = nanoTime();

        for (Element e : elements) {

            String sourceLink = e.select(".gt-title > a[href]").attr("href");
            String eventTitle = e.select(".gt-title > a[href]").text();

            if (sourceLink.isEmpty() || eventTitle.isEmpty()) {
                continue;
            }

            String eventDetailsHtml = getHtmlFromWebsite(sourceLink);
            if (eventDetailsHtml == null) {
                continue;
            }
            doc = parse(eventDetailsHtml);

            LOGGER.info("Processing {} event", eventTitle);

            Elements pageContent = doc.getElementsByClass("gt-page-content");
            Elements infoContainer = doc.getElementsByClass("gt-content-detail-box");

            StringBuilder stringBuilder = new StringBuilder();
            pageContent.select(".gt-content > p").textNodes().forEach(textNode -> {
                stringBuilder.append(textNode.text());
                stringBuilder.append("\n");
            });

            String description = stringBuilder.toString();
            String address = parseTextFromInput(infoContainer.select("li.gt-address div.gt-inner"));
            String location = getDataText(!infoContainer.select("li.gt-locations div.gt-inner").isEmpty() ?
                                          infoContainer.select("li.gt-locations div.gt-inner").get(0) : null);
            String startDate = parseTextFromInput(infoContainer.select("li.gt-start-date div.gt-inner"));
            String endDate = parseTextFromInput(infoContainer.select("li.gt-end-date div.gt-inner"));
            String imageLink = pageContent.select("div.gt-image img").attr("data-src");
            Set<String> categories = getDataSet(!infoContainer.select("li.gt-categories div.gt-inner").isEmpty() ?
                                                infoContainer.select("li.gt-categories div.gt-inner").get(0) : null);

            Set<Category> categorySetFromDb = null;

            if (categories != null) {

                Set<Category> fetchedCategorySet =
                        categories.stream().map(c -> Category.builder().title(c).build()).collect(toSet());

                categorySetFromDb = categories.stream()
                                              .map(categoryService::findCategoryByTitle)
                                              .filter(Objects::nonNull)
                                              .collect(toSet());

                SetView<Category> differentCategoriesSetView = Sets.difference(fetchedCategorySet, categorySetFromDb);
                Set<Category> differentCategories = differentCategoriesSetView.immutableCopy();
                if (!differentCategories.isEmpty()) {
                    differentCategories.forEach(c -> {
                        Category saved = categoryService.saveCategory(c);
                        c.setId(saved.getId());
                    });
                }

                categorySetFromDb.addAll(differentCategories);
            }

            Event fetchedEvent = Event.builder()
                                      .sourceLink(sourceLink)
                                      .title(eventTitle)
                                      .description(description)
                                      .address(address)
                                      .location(location)
                                      .startDate(!startDate.isEmpty() ? parseDateTime(startDate, patterns) : null)
                                      .endDate(!endDate.isEmpty() ? parseDateTime(endDate, patterns) : null)
                                      .imageLink(imageLink)
                                      .source(source)
                                      .categories(categorySetFromDb)
                                      .build();

            Event existingEvent = eventService.findEventByTitleAndSourceLink(eventTitle, sourceLink);

            if (existingEvent != null) {
                if (shouldUpdate(existingEvent, fetchedEvent)) {
                    fetchedEvent.setId(existingEvent.getId());
                    fetchedEvent.setLastUpdatedAt(now());
                    eventService.saveEvent(fetchedEvent);
                    LOGGER.info("{} event successfully updated", eventTitle);
                }
                LOGGER.info("{} event has not changed, continuing...", eventTitle);
            } else {
                eventService.saveEvent(fetchedEvent);
                LOGGER.info("{} event successfully saved", eventTitle);
            }
        }
        long elapsedTime = System.nanoTime() - startTime;
        LOGGER.info("Total execution time to parse and save {} events in millis: {}", elements.size(), elapsedTime);
    }

    private static String getDataText(Element result) {
        if (result != null) {
            Set<String> listItemsText = getDataSet(result);

            if (!listItemsText.isEmpty() && listItemsText.size() > 1) {
                // Remove duplicities
                return String.join(", ", new HashSet<>(listItemsText));
            }

            return result.text();
        }
        return null;
    }

    private static Set<String> getDataSet(Element result) {
        if (result != null) {
            Elements listItems = result.select("li");
            return listItems.stream().map(Element::text).collect(toSet());
        }
        return null;
    }

    private String getHtmlFromWebsite(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            System.out.println("Response code: " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            return null;
        }
    }

    private boolean shouldUpdate(Event existingEvent, Event fetchedEvent) {
        // Compare fields to decide whether an update is needed
        return !existingEvent.equals(fetchedEvent);
    }

    private static LocalDateTime parseDateTime(String input, List<String> patterns) {
        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDateTime.parse(input, formatter);
            } catch (Exception e) {
                // Try the next pattern if parsing fails
            }
        }
        // If none of the patterns match
        throw new IllegalArgumentException("Unable to parse LocalDateTime from input: " + input);
    }

    private static String parseTextFromInput(Elements input) {
        return input != null ? input.text() : null;
    }
}

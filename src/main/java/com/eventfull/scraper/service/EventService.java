package com.eventfull.scraper.service;

import com.eventfull.scraper.dao.EventRepository;
import com.eventfull.scraper.model.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public void saveEvent(Event event) {
        eventRepository.save(event);
    }

    public Event findEventByTitleAndSourceLink(String title, String sourceLink) {
        return eventRepository.findByTitleAndSourceLink(title, sourceLink);
    }
}

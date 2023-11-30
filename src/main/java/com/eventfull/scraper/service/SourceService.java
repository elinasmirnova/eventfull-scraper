package com.eventfull.scraper.service;

import com.eventfull.scraper.dao.SourceRepository;
import com.eventfull.scraper.model.Source;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class SourceService {

    private final SourceRepository sourceRepository;

    Source findSourceByTitle(String sourceTitle) {
        return sourceRepository.findSourceIdBySourceTitle(sourceTitle);
    }
}

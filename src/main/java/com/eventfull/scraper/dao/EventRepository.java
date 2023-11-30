package com.eventfull.scraper.dao;

import com.eventfull.scraper.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query("SELECT e FROM Event e JOIN FETCH e.categories JOIN FETCH e.source WHERE e.title = :title AND e.sourceLink = :sourceLink")
    Event findByTitleAndSourceLink(@Param("title") String title, @Param("sourceLink") String sourceLink);
}

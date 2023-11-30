package com.eventfull.scraper.dao;

import com.eventfull.scraper.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceRepository extends JpaRepository<Source, Integer> {

    @Query("SELECT s FROM Source s WHERE s.title = :sourceTitle")
    Source findSourceIdBySourceTitle(@Param("sourceTitle") String sourceTitle);
}

package com.eventfull.scraper.dao;

import com.eventfull.scraper.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE c.title = :categoryTitle")
    Category getCategoryByTitle(@Param("categoryTitle") String categoryTitle);
}

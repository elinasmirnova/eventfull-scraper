package com.eventfull.scraper.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "category", schema = "eventfull")
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @ManyToMany(mappedBy = "categories")
    private Set<Event> events;

    @Column(name = "last_inserted_at", columnDefinition = "TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime lastInsertedAt;

    @Column(name = "last_updated_at", columnDefinition = "TIMESTAMP", insertable = false)
    private LocalDateTime lastUpdatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Category category = (Category) o;
        return title.equals(category.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}

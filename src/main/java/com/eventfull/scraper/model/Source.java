package com.eventfull.scraper.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "source", schema = "eventfull")
public class Source implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "link")
    private String link;

    @Column(name = "type")
    private String type;

    @OneToMany(mappedBy = "source")
    private List<Event> events;

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
        Source source = (Source) o;
        return Objects.equals(id, source.id) && Objects.equals(title, source.title) && Objects.equals(link, source.link)
               && Objects.equals(type, source.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, link, type);
    }
}

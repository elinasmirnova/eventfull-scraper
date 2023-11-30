package com.eventfull.scraper.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "event", schema = "eventfull")
public class Event implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title; // +

    @Column(name = "source_link")
    private String sourceLink; //  +

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Source source;

    @Column(name = "description")
    private String description; // +

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "location")
    private String location; // +

    @Column(name = "address")
    private String address; // +

    @Column(name = "start_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime startDate; // +

    @Column(name = "end_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime endDate; // +

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "image_link")
    private String imageLink; // +

    @Column(name = "organizer")
    private String organizer;

    @ManyToMany
    @JoinTable(
            name = "event_to_category",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    @Column(name = "last_inserted_at", columnDefinition = "TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime lastInsertedAt;

    @Column(name = "last_updated_at", columnDefinition = "TIMESTAMP",  insertable = false)
    private LocalDateTime lastUpdatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return title.equals(event.title) && sourceLink.equals(event.sourceLink) && source.equals(event.source) && Objects.equals(description,
                                                                                                                                 event.description)
               && Objects.equals(shortDescription, event.shortDescription) && Objects.equals(location, event.location)
               && Objects.equals(address, event.address) && Objects.equals(startDate, event.startDate) && Objects.equals(
                endDate, event.endDate) && Objects.equals(price, event.price) && Objects.equals(imageLink, event.imageLink)
               && Objects.equals(organizer, event.organizer) && Objects.equals(categories, event.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, sourceLink, source, description, shortDescription, location, address, startDate, endDate, price, imageLink,
                            organizer,
                            categories);
    }
}

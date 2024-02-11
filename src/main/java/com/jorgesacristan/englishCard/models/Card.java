package com.jorgesacristan.englishCard.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jorgesacristan.englishCard.enums.PeriodDaysReminder;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name="cards")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Card implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id",updatable = false)
    private long id;

    @Column (name = "name1")
    private String name1;

    @Column (name = "name2")
    private String name2;

    @Column (name = "enable")
    private boolean enable;

    @Column (name = "lastTry")
    private Instant lastTry;

    @Column (name = "periodDaysReminder")
    private Integer periodDaysReminder;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "deck_id", nullable = false)
    @JsonIgnore
    private Deck deck;

    public Card(String name1, String name2, boolean enable, Instant lastTry, Integer periodDaysReminder, Deck deck) {
        this.name1 = name1;
        this.name2 = name2;
        this.enable = enable;
        this.lastTry = lastTry;
        this.periodDaysReminder = periodDaysReminder;
        this.deck = deck;
    }
}

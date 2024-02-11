package com.jorgesacristan.englishCard.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name="decks")
public class Deck implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id", updatable = false)
    private Long id;

    @Column (name = "name")
    private String name;

    @Column (name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "username_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    @OneToMany(mappedBy = "deck",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Card> cards;

    public Deck(String name, String description, User user, Language language, List<Card> cards) {
        this.name = name;
        this.description = description;
        this.user = user;
        this.language = language;
        this.cards = cards;
    }
}

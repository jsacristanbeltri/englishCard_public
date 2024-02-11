package com.jorgesacristan.englishCard.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
@Table(name="languages")
public class Language implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id", updatable = false)
    private long id;

    @Column(name = "language",nullable = false)
    private String language;

    @Column(name = "image")
    private String image;

    @Column(name = "userid")
    private Long userid;
}


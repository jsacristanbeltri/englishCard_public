package com.jorgesacristan.englishCard.dtos;

import com.jorgesacristan.englishCard.models.Card;
import com.jorgesacristan.englishCard.models.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckOutDto {
    private long id;
    private String name;
    private String description;
    private String language;
    private String username;
    private Integer numberOfCards;
    private List<Card> cards;
}

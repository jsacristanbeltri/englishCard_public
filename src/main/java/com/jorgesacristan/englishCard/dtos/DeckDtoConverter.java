package com.jorgesacristan.englishCard.dtos;

import com.jorgesacristan.englishCard.models.Deck;
import org.springframework.stereotype.Component;

@Component
public class DeckDtoConverter {



    public static DeckOutDto deckToDeckOutDto (Deck deck) {
        DeckOutDto deckOutDto = null;
        /*DeckOutDto deckOutDto = new DeckOutDto(
                deck.getId(),
                deck.getName(),
                deck.getDescription(),
                deck.getLanguage().getLanguage(),
                deck.getUser().getUsername(),
                deck.getNumberTotalOfCards(),
                deck.getCards());*/

        return deckOutDto;
    }
}

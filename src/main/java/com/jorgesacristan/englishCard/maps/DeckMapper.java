package com.jorgesacristan.englishCard.maps;

import com.jorgesacristan.englishCard.dtos.DeckOutDto;
import com.jorgesacristan.englishCard.dtos.DeckRequestDto;
import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.request.CreateDeckRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeckMapper {
    @Mapping(target = "language", source = "language.language")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "numberOfCards", expression = "java(deck.getCards().size())")
    DeckOutDto deckToDeckOutDto (Deck deck);
    DeckRequestDto deckToDeckRequestDto(CreateDeckRequest createDeckRequest);
}

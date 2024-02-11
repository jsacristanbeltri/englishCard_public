package com.jorgesacristan.englishCard.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DeckRequestDto {
    private String name;
    private String description;
    private String language;
    private String username;
}

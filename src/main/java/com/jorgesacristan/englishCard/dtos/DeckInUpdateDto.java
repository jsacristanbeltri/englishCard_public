package com.jorgesacristan.englishCard.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckInUpdateDto {
    private String name;
    private String description;
}

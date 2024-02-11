package com.jorgesacristan.englishCard.dtos;

import com.jorgesacristan.englishCard.models.Card;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecksResponseDto {
    private long id;
    private String name;
    private String description;
    private String language;
    private String username;
    private List<Card> cards;
}

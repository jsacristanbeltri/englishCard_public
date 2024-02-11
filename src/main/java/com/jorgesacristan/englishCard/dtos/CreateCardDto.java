package com.jorgesacristan.englishCard.dtos;

import com.jorgesacristan.englishCard.models.Deck;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCardDto implements Serializable {
    @NotBlank(message = "name1 is mandatory")
    String name1;
    @NotBlank(message = "name1 is mandatory")
    String name2;
    Boolean enable;

}

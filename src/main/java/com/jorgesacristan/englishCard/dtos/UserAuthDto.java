package com.jorgesacristan.englishCard.dtos;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthDto {
    private String username;
    private String password;
}

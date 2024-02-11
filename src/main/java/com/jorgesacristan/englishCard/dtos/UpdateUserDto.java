package com.jorgesacristan.englishCard.dtos;

import com.jorgesacristan.englishCard.enums.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class UpdateUserDto {
    private long id;
    private String username;
    private String password;
    private String email;
    private int level;
    private int experience;
    private int logStreak;
    private int gems;
    private String avatar;
}

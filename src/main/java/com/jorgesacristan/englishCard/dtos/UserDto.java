package com.jorgesacristan.englishCard.dtos;

import com.jorgesacristan.englishCard.enums.Provider;
import com.jorgesacristan.englishCard.enums.UserRole;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private long id;
    private String username;
    private String avatar;
    private Set<UserRole> roles;
    private String email;
    private int level;
    private int experience;
    private int logStreak;
    private int gems;
    private Boolean isEnabled;
    private Provider provider;

}

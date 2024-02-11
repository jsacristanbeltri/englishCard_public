package com.jorgesacristan.englishCard.models;

import com.jorgesacristan.englishCard.enums.Provider;
import com.jorgesacristan.englishCard.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="users")
public class User implements Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id",updatable = true)
    private long id;

    @Column (name = "username", unique = true, nullable = false)
    private String username;

    @Column (name = "password",  nullable = true)
    private String password;

    @Column (name = "email",unique = true, nullable = false)
    private String email;

    @Column (name = "level", nullable = true)
    private int level;

    @Column (name = "experience", nullable = true)
    private int experience;

    @Column (name = "log_streak", nullable = true)
    private int logStreak;

    @Column (name = "gems", nullable = true)
    private int gems;

    private String avatar;

    @Column (name = "isenabled")
    private boolean isEnabled;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(ur -> new SimpleGrantedAuthority(ur.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

}

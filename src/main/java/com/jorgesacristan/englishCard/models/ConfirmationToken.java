package com.jorgesacristan.englishCard.models;

import com.jorgesacristan.englishCard.models.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "confirmationtoken")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="token_id")
    private Long tokenId;

    @Column(name="confirmation_token")
    private String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date")
    private Date createdDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public ConfirmationToken(User user) {
        this.user = user;
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }

}

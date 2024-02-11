package com.jorgesacristan.englishCard.repositories;


import com.jorgesacristan.englishCard.models.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByConfirmationToken(String confirmationToken);
    @Query(value="select c from ConfirmationToken c where c.user.email = ?1")
    List<ConfirmationToken> findByUserEmail (String email);


}

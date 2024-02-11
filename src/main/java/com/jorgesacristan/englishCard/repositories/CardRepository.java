package com.jorgesacristan.englishCard.repositories;

import com.jorgesacristan.englishCard.models.Card;
import com.jorgesacristan.englishCard.models.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card,Long> {
}

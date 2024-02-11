package com.jorgesacristan.englishCard.repositories;

import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.models.Language;
import com.jorgesacristan.englishCard.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeckRepository extends JpaRepository<Deck,Long> {


    //@Query("SELECT d FROM Deck d WHERE d.language=?1 and d.user=?2")
    //@Query("SELECT d FROM Deck d")
    List<Deck> findDeckByLanguageAndUser (Language languageRequest, User userRequest);

    @Query("SELECT d FROM Deck d WHERE d.language=?1")
    //@Query("SELECT d FROM Deck d ")
    List<Deck> findDeckByLanguage(Language languageRequest);

    List<Deck> findByUser(User user);
}

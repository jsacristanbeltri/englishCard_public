package com.jorgesacristan.englishCard.repositories;

import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language,Long> {
    Optional<Language> findByLanguage(String language);
    @Query("select l from Language l where l.userid is null or l.userid = ?1")
    List<Language> findByUserId (Long idUser);
}

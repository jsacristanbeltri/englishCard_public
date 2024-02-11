package com.jorgesacristan.englishCard.services;

import java.util.List;

public interface EnglishCardService<E> {
    List<E> findAll();
    E find(Long id);
    E create(E element);
    E update(Long id, E element) throws Exception;
    void delete(Long id) throws Exception;
    //boolean checkUserPassword(String username, String password);
}

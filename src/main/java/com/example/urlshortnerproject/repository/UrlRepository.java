package com.example.urlshortnerproject.repository;

import com.example.urlshortnerproject.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Url findByShortLink(String shortLink);
}

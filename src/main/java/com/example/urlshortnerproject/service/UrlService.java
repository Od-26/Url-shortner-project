package com.example.urlshortnerproject.service;

import com.example.urlshortnerproject.model.Url;
import com.example.urlshortnerproject.model.UrlDto;
import org.springframework.stereotype.Service;

@Service
public interface UrlService {
    Url generateShortUrlContainer(UrlDto urlDto);
    Url saveShortUrl(Url url);
    Url checkIfShortLinkExists(String url);
    void deleteShortUrl(Url url);
}

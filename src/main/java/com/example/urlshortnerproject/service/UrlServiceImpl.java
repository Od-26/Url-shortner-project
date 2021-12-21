package com.example.urlshortnerproject.service;


import com.example.urlshortnerproject.model.Url;
import com.example.urlshortnerproject.model.UrlDto;
import com.example.urlshortnerproject.repository.UrlRepository;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
//The difference between @Configuration and @Component is that @Configuration tells Spring that there are more than one bean methods in class
//@Component tells Spring to scan a class for dependencies and inject to the class
public class UrlServiceImpl implements UrlService{

    private final UrlRepository urlRepository;
    @Autowired
    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public Url generateShortUrlContainer(UrlDto urlDto) {
        if(!(String.valueOf(urlDto.getOriginal_url()).isEmpty())){
            String encodedUrl= encodeUrl(urlDto.getOriginal_url());
            Url shortUrlContainer= new Url();
            shortUrlContainer.setShortLink(encodedUrl);
            shortUrlContainer.setOriginal_url(urlDto.getOriginal_url());
            shortUrlContainer.setExpirationDate(getExpirationDate((urlDto.getExpirationDate()),LocalDateTime.now()));



            Url urlToReturn =saveShortUrl(shortUrlContainer);
           if(urlToReturn!=null){
               return urlToReturn;
           }
        }
       return null;
    }

    private LocalDateTime getExpirationDate(String expirationDate, LocalDateTime createdAt) {
        if(expirationDate==null){
            return createdAt.plusMinutes(2);
        }
        return LocalDateTime.parse(expirationDate);
    }


    private String encodeUrl(String url) {
        String encodedUrl = "";
        {
            encodedUrl = Hashing.adler32().hashString(url,StandardCharsets.UTF_8).toString();
            return encodedUrl;



        }

    }

    @Override
    public Url saveShortUrl(Url shortUrl) {
      return urlRepository.save(shortUrl);

    }

    @Override
    public Url checkIfShortLinkExists(String url) {
        return urlRepository.findByShortLink(url);
    }

    @Override
    public void deleteShortUrl(Url url) {
        urlRepository.delete(url);
    }
}

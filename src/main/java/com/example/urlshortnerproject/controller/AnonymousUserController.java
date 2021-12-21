package com.example.urlshortnerproject.controller;

import com.example.urlshortnerproject.model.Url;
import com.example.urlshortnerproject.model.UrlDto;
import com.example.urlshortnerproject.model.UrlErrorResponseDto;
import com.example.urlshortnerproject.model.UrlResponseDto;
import com.example.urlshortnerproject.service.UrlService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Controller
public class AnonymousUserController {
    private UrlService urlService;

    @Autowired
    public AnonymousUserController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/")
    public String anonymousUserView(Model model){
        UrlDto urlDto= new UrlDto();
        model.addAttribute("urlDto",urlDto);
        return "anonymousUser";
    }

    @PostMapping("/anonymousUser/generate")
    public ResponseEntity<?> generateShortLink(@ModelAttribute("urlDto") UrlDto urlDto) {
        Url shortUrlContainer = urlService.generateShortUrlContainer(urlDto);


        if (shortUrlContainer != null) {
            UrlResponseDto urlResponseDto = new UrlResponseDto();
            urlResponseDto.setShortLink(shortUrlContainer.getShortLink());

            return new ResponseEntity<>(urlResponseDto, HttpStatus.OK);
        }
        UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
        urlErrorResponseDto.setError("Please enter a url");
        urlErrorResponseDto.setStatus("404");
        return new ResponseEntity<>(urlErrorResponseDto,HttpStatus.OK);}

    @GetMapping("/{shortLink}")
    public ResponseEntity<?> redirectUser(@PathVariable String shortLink, HttpServletResponse response) throws IOException {
        if (StringUtils.isEmpty(shortLink)) {
            UrlErrorResponseDto errorResponseDto = new UrlErrorResponseDto();
            errorResponseDto.setError("Url cannot be empty");
            errorResponseDto.setStatus("400");
            return new ResponseEntity<>(errorResponseDto, HttpStatus.OK);
        }
        Url url = urlService.checkIfShortLinkExists(shortLink);
        if (url == null) {
            UrlErrorResponseDto errorResponseDto = new UrlErrorResponseDto();
            errorResponseDto.setError("Url does not exist");
            errorResponseDto.setStatus("400");
            return new ResponseEntity<>(errorResponseDto, HttpStatus.OK);
        }
        if ((LocalDateTime.now().equals(url.getExpirationDate()))) {
            urlService.deleteShortUrl(url);
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Url Expired. Try generating a new one.");
            urlErrorResponseDto.setStatus("200");
            return new ResponseEntity<>(urlErrorResponseDto, HttpStatus.OK);
        }

        response.sendRedirect(url.getOriginal_url());
        return null;
    }
}

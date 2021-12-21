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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;


@Controller
public class LoggedInUserController {
    private final UrlService urlService;

    @Autowired
    public LoggedInUserController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/loggedIn")
    public String homepage(Model model){
        UrlDto urlDto= new UrlDto();  //So form displays and to get rid of the error in the login form th:object. Also used to store user input
        model.addAttribute("urlDto",urlDto);
        return "loggedIn";
    }

    @PostMapping("/createShortLink")
    public ResponseEntity<?> generateShortLink(@ModelAttribute("urlDto") UrlDto urlDto) {
        //The @ModelAttribute is used to fetch the model(input) from the form
        Url shortUrlContainer = urlService.generateShortUrlContainer(urlDto);


        if (shortUrlContainer != null) {
            UrlResponseDto urlResponseDto = new UrlResponseDto();
            urlResponseDto.setShortLink(shortUrlContainer.getShortLink());

            return new ResponseEntity<>(urlResponseDto, HttpStatus.OK);
        }
        UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
        urlErrorResponseDto.setError("An error occurred, please try again ");
        urlErrorResponseDto.setStatus("404");
        return new ResponseEntity<>(urlErrorResponseDto,HttpStatus.OK);
    }

    @GetMapping("/loggedIn/{createdShortLink}")
    public ResponseEntity<?> redirect(@PathVariable String createdShortLink, HttpServletResponse response) throws IOException {
        if (StringUtils.isEmpty(createdShortLink)) {
            UrlErrorResponseDto errorResponseDto = new UrlErrorResponseDto();
            errorResponseDto.setError("Url cannot be empty");
            errorResponseDto.setStatus("400");
            return new ResponseEntity<>(errorResponseDto, HttpStatus.OK);
        }
        Url url = urlService.checkIfShortLinkExists(createdShortLink);
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

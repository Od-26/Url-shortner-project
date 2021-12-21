package com.example.urlshortnerproject.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String original_url;
    private String shortLink;
    private LocalDateTime expirationDate;

    public Url() {
    }
}

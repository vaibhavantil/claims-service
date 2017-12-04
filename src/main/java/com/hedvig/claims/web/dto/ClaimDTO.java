package com.hedvig.claims.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import java.time.LocalDate;

public class ClaimDTO {

    public String id;
    public String name;

    @JsonDeserialize(using= LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate registrationDate;

    public ClaimDTO(){}

    public ClaimDTO(String id, String name, LocalDate registrationDate) {
        this.id = id;
        this.name = name;
        this.registrationDate = registrationDate;
    }
}

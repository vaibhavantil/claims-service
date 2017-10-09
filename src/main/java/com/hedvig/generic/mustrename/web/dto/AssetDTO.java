package com.hedvig.generic.mustrename.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import java.time.LocalDate;

public class AssetDTO {

    public String id;
    public String name;

    @JsonDeserialize(using= LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate registrationDate;

    public AssetDTO(){}

    public AssetDTO(String id, String name, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.registrationDate = birthDate;
    }
}

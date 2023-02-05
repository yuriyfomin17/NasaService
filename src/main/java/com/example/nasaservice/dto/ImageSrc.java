package com.example.nasaservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImageSrc(@JsonProperty("img_src") String imgSrc) {
}


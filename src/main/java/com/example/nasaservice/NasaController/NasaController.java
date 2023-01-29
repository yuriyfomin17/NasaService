package com.example.nasaservice.NasaController;

import com.example.nasaservice.NasaService.NasaService;
import lombok.SneakyThrows;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/mars")
public class NasaController {
    private final NasaService nasaService;

    public NasaController(NasaService nasaService) {
        this.nasaService = nasaService;
    }

    @GetMapping(value = "/pictures/largest/{sol}", produces = {"image/jpeg"})
    @SneakyThrows
    public ResponseEntity<?> getLargetPicture(@PathVariable Long sol){
        byte[] responseBody = nasaService.getLargestPictureURL(sol);
        return ResponseEntity.ok()
                .contentLength(responseBody.length)
                .contentType(MediaType.IMAGE_JPEG)
                .body(responseBody);
    }
}

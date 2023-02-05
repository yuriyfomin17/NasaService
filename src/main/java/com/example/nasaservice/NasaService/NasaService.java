package com.example.nasaservice.NasaService;

import com.example.nasaservice.NasaScheduler.NasaScheduler;
import com.example.nasaservice.dto.ImageSrc;
import com.example.nasaservice.dto.NasaImage;
import com.example.nasaservice.dto.Photos;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class NasaService {
    private final RestTemplate restTemplate;
    private final Executor executor;
    @Value("${nasa.api.url}")
    private String NASA_URL;
    @Value("${nasa.api.key}")
    private String NASA_API_KEY;


    public byte[] getLargestPictureURL(Long sol) {

        String formattedNasaURL = buildUrl(sol).toString();
        Photos photos = this.restTemplate.getForEntity(formattedNasaURL, Photos.class).getBody();
        assert photos != null;
        List<CompletableFuture<NasaImage>> completableFutures = photos.photos().stream()
                .map(ImageSrc::imgSrc)
                .map(imgSrc -> CompletableFuture.supplyAsync(() -> extractImage(imgSrc), executor))
                .toList();
        var largestNasaImage = completableFutures.stream().map(CompletableFuture::join).max(Comparator.comparing(NasaImage::size)).orElseThrow();
        return getLargetImageByte(largestNasaImage.url());

    }

    private URI buildUrl(Long sol) {
        return UriComponentsBuilder.fromHttpUrl(NASA_URL)
                .queryParam("api_key", NASA_API_KEY)
                .queryParam("sol", sol)
                .build().toUri();
    }

    public byte[] getLargetImageByte(String url) {
        return restTemplate.getForObject(url, byte[].class);
    }

    private NasaImage extractImage(String imageSrc) {
        var res = restTemplate.headForHeaders(imageSrc);
        if (res.getLocation() != null) {
            return extractImage(res.getLocation().toString());
        } else {
            return new NasaImage(imageSrc, res.getContentLength());
        }
    }


}

package com.example.nasaservice.NasaService;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Service
public class NasaService {
    private final RestTemplate restTemplate;
    private final Executor executor;
    private final Map<Long, NasaImage> maxSolNasaImageMap = new ConcurrentHashMap<>();

    public NasaService(RestTemplate restTemplate, Executor executor) {
        this.executor = executor;
        this.restTemplate = restTemplate;
    }


    public byte[] getLargestPictureURL(Long sol) {
        if (maxSolNasaImageMap.containsKey(sol)) {
            var largestNasaImage = maxSolNasaImageMap.get(sol);
            return getLargetImageByte(largestNasaImage.url());
        } else {
            String NASA_URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=%s&api_key=ljLkoIFi56lbogCGEjVz0sot75lkjAksN8mqIh7c";
            String formattedNasaURL = String.format(NASA_URL, sol);

            Photos photos = this.restTemplate.getForEntity(formattedNasaURL, Photos.class).getBody();
            assert photos != null;
            List<CompletableFuture<NasaImage>> completableFutures = photos.photos().stream()
                    .map(ImageSrc::imgSrc)
                    .map(imgSrc -> CompletableFuture.supplyAsync(() -> extractImage(imgSrc), executor))
                    .toList();
            var largestNasaImage = completableFutures.stream().map(CompletableFuture::join).max(Comparator.comparing(NasaImage::size)).orElseThrow();
            maxSolNasaImageMap.put(sol, largestNasaImage);
            return getLargetImageByte(largestNasaImage.url());
        }

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

record Photos(List<ImageSrc> photos) {

}

record ImageSrc(@JsonProperty("img_src") String imgSrc) {
}


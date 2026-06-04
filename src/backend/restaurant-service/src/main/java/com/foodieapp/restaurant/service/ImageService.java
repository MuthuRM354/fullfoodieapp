package com.foodieapp.restaurant.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ImageService {

    public String uploadImage(byte[] imageData, String filename) {
        // Stub: in production, upload to S3/GCS
        log.info("Image upload requested for: {}", filename);
        return "https://placeholder.images.foodieapp.com/" + filename;
    }

    public void deleteImage(String imageUrl) {
        log.info("Image delete requested for: {}", imageUrl);
    }
}

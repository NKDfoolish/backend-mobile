package com.myproject.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.myproject.dto.FileData;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.Plant;
import com.myproject.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "FILE_SERVICE")
public class FileService {

    @Value("${app.file.download-prefix}")
    private String urlPrefix;

    private final PlantRepository plantRepository;
    private final Cloudinary cloudinary;

    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(Long plantId, MultipartFile file) throws IOException {
        log.info("Uploading file: {}", file.getOriginalFilename());

        String publicId = UUID.randomUUID().toString();

        // Upload file to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("public_id", publicId, "resource_type", "image"));

        log.info("Upload result: {}", uploadResult);

        // Get the secure URL from the upload result
        String secureUrl = (String) uploadResult.get("secure_url");
        log.info("File uploaded successfully. URL: {}", secureUrl);

        // Save the file information to the database
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        // Delete old file if exists
        if (StringUtils.hasText(plant.getImage()) && plant.getImage().contains("/")) {
            // Extract public_id from the old URL
            String oldPublicId = plant.getImage().substring(plant.getImage().lastIndexOf("/") + 1);
            if (oldPublicId.contains(".")) {
                oldPublicId = oldPublicId.substring(0, oldPublicId.lastIndexOf("."));
            }
            cloudinary.uploader().destroy(oldPublicId, ObjectUtils.asMap("resource_type", "image"));
        }

        plant.setImage(secureUrl);
        plantRepository.save(plant);

        return urlPrefix + plantId;
    }

    public FileData download(Long plantId) throws IOException {
        log.info("Downloading file for plant: {}", plantId);

        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        if (!StringUtils.hasText(plant.getImage())) {
            throw new ResourceNotFoundException("File not found for plant id: " + plantId);
        }

        // The image field now contains the full Cloudinary URL
        String cloudinaryUrl = plant.getImage();

        // Determine content type based on file extension
        String extension = cloudinaryUrl.substring(cloudinaryUrl.lastIndexOf(".") + 1);
        String contentType = extension != null && !extension.isEmpty()
                ? "image/" + extension.toLowerCase()
                : "application/octet-stream";

        // Fetch file content with error handling
        try {
            byte[] data = new URL(cloudinaryUrl).openStream().readAllBytes();
            return new FileData(contentType, new ByteArrayResource(data));
        } catch (IOException e) {
            log.error("Failed to download file from Cloudinary: {}", cloudinaryUrl, e);
            throw new ResourceNotFoundException("Failed to download file for plant id: " + plantId);
        }
    }
}

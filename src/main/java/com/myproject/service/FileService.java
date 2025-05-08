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

        String fileExtension = StringUtils
                .getFilenameExtension(file.getOriginalFilename());

        String publicId = UUID.randomUUID().toString() + (fileExtension != null ? "." + fileExtension : "");

        // Upload file to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("public_id", publicId, "resource_type", "image"));

        log.info("Upload result: {}", uploadResult);
        log.info("File uploaded successfully: {}", publicId);

        // Save the file information to the database
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        // Delete old file if exists
        if (StringUtils.hasText(plant.getImage())) {
            String oldPublicId = plant.getImage();
            cloudinary.uploader().destroy(oldPublicId, ObjectUtils.asMap("resource_type", "image"));
        }

        plant.setImage(publicId);
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

        // Fetch the resource details from Cloudinary to get the secure_url
        String cloudinaryUrl;
        try {
            Map resource = cloudinary.api().resource(plant.getImage(), ObjectUtils.asMap("resource_type", "image"));
            cloudinaryUrl = (String) resource.get("secure_url");
            log.info("Fetched Cloudinary secure_url: {}", cloudinaryUrl);
        } catch (Exception e) {
            log.error("Failed to fetch resource from Cloudinary for public_id: {}", plant.getImage(), e);
            throw new ResourceNotFoundException("Failed to retrieve file metadata for plant id: " + plantId);
        }

        // Determine content type based on file extension
        String contentType = StringUtils.getFilenameExtension(plant.getImage()) != null
                ? "image/" + StringUtils.getFilenameExtension(plant.getImage()).toLowerCase()
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

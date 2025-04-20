package com.myproject.service;

import com.myproject.dto.FileData;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.Plant;
import com.myproject.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "FILE_SERVICE")
public class FileService {

    @Value("${app.file.storage-dir}")
    private String storageDir;

    @Value("${app.file.download-prefix}")
    private String urlPrefix;

    private final PlantRepository plantRepository;

    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(Long plantId, MultipartFile file) throws IOException {
        log.info("Uploading file: {}", file.getOriginalFilename());

        Path folder = Paths.get(storageDir);

        // create directory if not exists
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }

        String fileExtension = StringUtils
                .getFilenameExtension(file.getOriginalFilename());

        String fileName = Objects.isNull(fileExtension)
                ? UUID.randomUUID().toString()
                : UUID.randomUUID() + "." + fileExtension;

        Path filePath = folder.resolve(fileName).normalize().toAbsolutePath();

        Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        log.info("File uploaded successfully: {}", fileName);

        // Save the file information to the database
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        // Check if the plant already has an image then remove the old image
        if (StringUtils.hasText(plant.getImage())) {
            Path oldFilePath = Paths.get(storageDir).resolve(plant.getImage()).normalize().toAbsolutePath();
            Files.deleteIfExists(oldFilePath);
        }

        plant.setImage(storageDir + "/" + fileName);

        return urlPrefix + fileName;
    }

    public FileData download(Long plantId) throws IOException {
        log.info("Downloading file: {}", plantId);

        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        Path filePath = Paths.get(plant.getImage()).normalize().toAbsolutePath();

        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("File not found with id: " + plantId);
        }

        var data = Files.readAllBytes(filePath);

        // get content type based on file extension (example: image/jpeg, image/png)
        String contentType = Files.probeContentType(filePath);

        return new FileData(contentType, new ByteArrayResource(data));
    }
}

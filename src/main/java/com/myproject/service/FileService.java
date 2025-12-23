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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "FILE_SERVICE")
public class FileService {

//    private final PlantDetailRepository plantDetailRepository;
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

        String imageUrl = plant.getImage();
        log.info("Fetching image from URL: {}", imageUrl);

        HttpURLConnection connection = null;
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            // Thêm tiêu đề HTTP để giả lập trình duyệt
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // Timeout 5 giây
            connection.setReadTimeout(5000);
            connection.setInstanceFollowRedirects(true); // Tự động theo chuyển hướng

            // Kiểm tra mã trạng thái HTTP
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            // Kiểm tra Content-Type
            String contentType = connection.getContentType();
            List<String> validImageTypes = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");
            if (contentType == null || !validImageTypes.contains(contentType.toLowerCase())) {
                log.error("Invalid content type: {} for URL: {}", contentType, imageUrl);
                throw new ResourceNotFoundException("URL does not point to a valid image for plant id: " + plantId);
            }

            // Đọc dữ liệu hình ảnh
            byte[] data = connection.getInputStream().readAllBytes();
            if (data.length == 0) {
                throw new IOException("Empty response from URL: " + imageUrl);
            }

            // Sử dụng Content-Type từ phản hồi thay vì dựa vào phần mở rộng
            return new FileData(contentType, new ByteArrayResource(data));

        } catch (IOException e) {
            log.error("Failed to download file from URL: {}", imageUrl, e);
            throw new ResourceNotFoundException("Failed to download file for plant id: " + plantId);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long plantId) throws IOException {
        log.info("Attempting to delete file for plant: {}", plantId);

        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        if (!StringUtils.hasText(plant.getImage())) {
            log.info("No image found for plant id: {}", plantId);
            return;
        }

        String imageUrl = plant.getImage();
        log.info("Checking image URL: {}", imageUrl);

        // Kiểm tra xem URL có thuộc Cloudinary không
        String cloudinaryDomain = "res.cloudinary.com/dhk08b5s3";
        if (!imageUrl.contains(cloudinaryDomain)) {
            log.info("Image URL {} is not from Cloudinary, skipping deletion", imageUrl);
            return;
        }

        // Trích xuất public_id từ URL
        String publicId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        if (publicId.contains(".")) {
            publicId = publicId.substring(0, publicId.lastIndexOf("."));
        }

        try {
            // Xóa file trên Cloudinary
            Map deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
            log.info("Delete result from Cloudinary: {}", deleteResult);

            // Xóa URL khỏi cơ sở dữ liệu
            plant.setImage(null);
            plantRepository.save(plant);

            log.info("Successfully deleted image for plant id: {}", plantId);

        } catch (Exception e) {
            log.error("Failed to delete file from Cloudinary for plant id: {}, public_id: {}", plantId, publicId, e);
            throw new IOException("Failed to delete file for plant id: " + plantId, e);
        }
    }
}

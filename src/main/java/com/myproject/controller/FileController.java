package com.myproject.controller;

import com.myproject.dto.FileData;
import com.myproject.dto.response.ApiResponse;
import com.myproject.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Slf4j(topic = "FILE_CONTROLLER")
@Tag(name = "File Controller", description = "File Controller")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "Upload media file", description = "API to upload media file")
    @PostMapping("/upload/plant/{plantId}")
    public ApiResponse uploadMedia(@PathVariable Long plantId,
                                   @RequestParam("file") MultipartFile file) throws IOException {
        log.info("Upload media file");

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("File uploaded successfully")
                .data(fileService.uploadFile(plantId,file))
                .build();
    }

    @Operation(summary = "Download media file", description = "API to download media file")
    @GetMapping("/download/plant/{plantId}")
    public ResponseEntity<Resource> downloadMedia(@PathVariable Long plantId) throws IOException {
        log.info("Download media file");

        FileData fileData = fileService.download(plantId);

        log.info("Content type: {}", fileData.contentType());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, fileData.contentType())
                .body(fileData.resource());
    }

}

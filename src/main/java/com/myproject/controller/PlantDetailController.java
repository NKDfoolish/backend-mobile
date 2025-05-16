package com.myproject.controller;

import com.myproject.dto.request.PlantCreationRequest;
import com.myproject.dto.request.PlantDetailRequest;
import com.myproject.dto.response.ApiResponse;
import com.myproject.dto.response.PlantDetailResponse;
import com.myproject.dto.response.PlantResponse;
import com.myproject.service.PlantDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plant/detail")
@Tag(name = "Plant Detail Controller", description = "Plant Detail Controller")
@RequiredArgsConstructor
@Slf4j(topic = "PLANT_DETAIL_CONTROLLER")
@Validated
public class PlantDetailController {

    private final PlantDetailService plantDetailService;

    @Operation(summary = "Create plant with more information", description = "API create new plant with more information")
    @PostMapping("/add")
    public ApiResponse createPlant(@RequestBody @Valid PlantDetailRequest req) {
        log.info("Create plant with more information {}", req);

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("plant with more information created")
                .data(plantDetailService.save(req))
                .build();
    }

    @Operation(summary = "Get plant with more information", description = "API retrieve plant with more information by id")
    @GetMapping("/{plantId}")
    public ApiResponse getPlantDetail(@PathVariable @Min(value = 1, message = "PlantId must be equal or greater than 1") Long plantId) {
        log.info("Get plant with more information {}", plantId);

        PlantDetailResponse plantResponse = plantDetailService.findByPlantId(plantId);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("plant with more information")
                .data(plantResponse)
                .build();
    }
}

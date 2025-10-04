package com.myproject.controller;

import com.myproject.dto.request.PlantCreationRequest;
import com.myproject.dto.request.PlantDetailRequest;
import com.myproject.dto.request.PlantDetailUpdateRequest;
import com.myproject.dto.request.PlantUpdateRequest;
import com.myproject.dto.response.ApiResponse;
import com.myproject.dto.response.PlantResponse;
import com.myproject.service.PlantService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/plant")
@Tag(name = "Plant Controller", description = "Plant Controller")
@RequiredArgsConstructor
@Slf4j(topic = "PLANT_CONTROLLER")
@Validated
public class PlantController {

    private final PlantService plantService;

    @Operation(summary = "Get list plant", description = "API retrieve list of plant")
    @GetMapping("/list")
    public ApiResponse getList(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String sort,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {

        log.info("Get list plant");

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("plant list")
                .data(plantService.findAll(keyword, sort, page, size))
                .build();
    }

    @Operation(summary = "Get plant detail", description = "API retrieve plant detail by id")
    @GetMapping("/{plantId}")
    public ApiResponse getPlantDetail(@PathVariable @Min(value = 1, message = "PlantId must be equal or greater than 1") Long plantId) {
        log.info("Get plant detail {}", plantId);

        PlantResponse plantResponse = plantService.findById(plantId);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("plant")
                .data(plantResponse)
                .build();
    }


    @Operation(summary = "Create plant with details", description = "API create new plant with detailed information")
    @PostMapping("/detail/add")
    public ApiResponse createPlantWithDetail(@RequestBody @Valid PlantDetailRequest req) {
        log.info("Create plant with details {}", req);

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("plant with details created")
                .data(plantService.saveWithDetail(req))
                .build();
    }

    @Operation(summary = "Update plant with details", description = "API update plant with detailed information")
    @PutMapping("/detail/upd")
    public ApiResponse updatePlantWithDetail(@RequestBody @Valid PlantDetailUpdateRequest req) {
        log.info("Update plant with details {}", req);

        plantService.updateWithDetail(req);

        return ApiResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("plant with details updated successfully")
                .build();
    }

    @Operation(summary = "Delete plant", description = "API delete plant by id")
    @DeleteMapping("/del/{plantId}")
    public ApiResponse deletePlant(@PathVariable @Min(value = 1, message = "PlantId must be equal or greater than 1") Long plantId) throws IOException {
        log.info("Delete plant {}", plantId);

        plantService.delete(plantId);

        return ApiResponse.builder()
                .status(HttpStatus.RESET_CONTENT.value())
                .message("plant deleted successfully")
                .build();
    }

    @Operation(summary = "Get plant detail", description = "API retrieve plant with detailed information by id")
    @GetMapping("/detail/{plantId}")
    public ApiResponse getPlantDetailInfo(@PathVariable @Min(value = 1, message = "PlantId must be equal or greater than 1") Long plantId) {
        log.info("Get plant detail information {}", plantId);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("plant detail")
                .data(plantService.findDetailById(plantId))
                .build();
    }

    @Hidden
    @Operation(summary = "Create plant", description = "API create new plant")
    @PostMapping("/add")
    public ApiResponse createPlant(@RequestBody @Valid PlantCreationRequest req) {
        log.info("Create plant {}", req);

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("plant created")
                .data(plantService.save(req))
                .build();
    }

    @Hidden
    @Operation(summary = "Update plant", description = "API update plant")
    @PutMapping("/upd")
    public ApiResponse updatePlant(@RequestBody @Valid PlantUpdateRequest req) {
        log.info("Update plant {}", req);

        plantService.update(req);

        return ApiResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("plant updated successfully")
                .build();
    }

//    @Hidden
//    @Operation(summary = "Delete plant", description = "API delete plant by id")
//    @DeleteMapping("/del/{plantId}")
//    public ApiResponse deletePlant(@PathVariable @Min(value = 1, message = "PlantId must be equal or greater than 1") Long plantId) {
//        log.info("Delete plant {}", plantId);
//
//        plantService.delete(plantId);
//
//        return ApiResponse.builder()
//                .status(HttpStatus.RESET_CONTENT.value())
//                .message("plant deleted successfully")
//                .build();
//    }
}

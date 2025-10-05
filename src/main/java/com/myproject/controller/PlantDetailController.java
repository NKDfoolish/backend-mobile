//package com.myproject.controller;
//
//import com.myproject.dto.request.PlantDetailRequest;
//import com.myproject.dto.request.PlantDetailUpdateRequest;
//import com.myproject.dto.response.ApiResponse;
//import com.myproject.dto.response.PlantDetailResponse;
//import com.myproject.service.PlantDetailService;
//import io.swagger.v3.oas.annotations.Hidden;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.Min;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//
//@RestController
//@RequestMapping("/plant/detail-old")
//@Tag(name = "Plant Detail Controller", description = "Plant Detail Controller")
//@RequiredArgsConstructor
//@Slf4j(topic = "PLANT_DETAIL_CONTROLLER")
//@Validated
//@Hidden
//public class PlantDetailController {
//
//    private final PlantDetailService plantDetailService;
//
//    @Hidden
//    @Operation(summary = "Create plant with more information", description = "API create new plant with more information")
//    @PostMapping("/add")
//    public ApiResponse createPlant(@RequestBody @Valid PlantDetailRequest req) {
//        log.info("Create plant with more information {}", req);
//
//        return ApiResponse.builder()
//                .status(HttpStatus.CREATED.value())
//                .message("plant with more information created")
//                .data(plantDetailService.save(req))
//                .build();
//    }
//
//    @Operation(summary = "Get plant with more information", description = "API retrieve plant with more information by id")
//    @GetMapping("/{plantId}")
//    public ApiResponse getPlantDetail(@PathVariable @Min(value = 1, message = "PlantId must be equal or greater than 1") Long plantId) {
//        log.info("Get plant with more information {}", plantId);
//
//        PlantDetailResponse plantResponse = plantDetailService.findByPlantId(plantId);
//
//        return ApiResponse.builder()
//                .status(HttpStatus.OK.value())
//                .message("plant with more information")
//                .data(plantResponse)
//                .build();
//    }
//
//    @Hidden
//    @Operation(summary = "Update plant & information", description = "API update plant & information")
//    @PutMapping("/upd")
//    public ApiResponse updatePlant(@RequestBody @Valid PlantDetailUpdateRequest req) {
//        log.info("Update plant & information {}", req);
//
//        plantDetailService.update(req);
//
//        return ApiResponse.builder()
//                .status(HttpStatus.ACCEPTED.value())
//                .message("plant with more information updated successfully")
//                .build();
//    }
//
//    @Hidden
//    @Operation(summary = "Delete plant & information", description = "API delete plant & information by plant id")
//    @DeleteMapping("/del/{plantId}")
//    public ApiResponse deletePlant(@PathVariable @Min(value = 1, message = "PlantId must be equal or greater than 1") Long plantId) throws IOException {
//        log.info("Delete plant {}", plantId);
//
//        plantDetailService.delete(plantId);
//
//        return ApiResponse.builder()
//                .status(HttpStatus.RESET_CONTENT.value())
//                .message("plant with information deleted successfully")
//                .build();
//    }
//}

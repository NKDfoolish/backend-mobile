package com.myproject.controller;

import com.myproject.dto.request.AreaCreationRequest;
import com.myproject.dto.request.AreaUpdateRequest;
import com.myproject.dto.request.VaseCreationRequest;
import com.myproject.dto.request.VaseUpdateRequest;
import com.myproject.dto.response.ApiResponse;
import com.myproject.dto.response.AreaResponse;
import com.myproject.dto.response.VaseResponse;
import com.myproject.service.AreaService;
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
@RequestMapping("/area")
@Tag(name = "Area Controller", description = "Area Controller")
@RequiredArgsConstructor
@Slf4j(topic = "AREA_CONTROLLER")
@Validated
public class AreaController {

    private final AreaService areaService;

    @Operation(summary = "Get list area", description = "API retrieve list of area")
    @GetMapping("/list")
    public ApiResponse getList(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String sort,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {

        log.info("Get list area");

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("area list")
                .data(areaService.findAll(keyword, sort, page, size))
                .build();
    }

    @Operation(summary = "Get area detail", description = "API retrieve area detail by id")
    @GetMapping("/{areaId}")
    public ApiResponse getAreaDetail(@PathVariable @Min(value = 1, message = "AreaId must be equal or greater than 1") Integer areaId) {
        log.info("Get vase detail {}", areaId);

        AreaResponse areaResponse = areaService.findById(areaId);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("area")
                .data(areaResponse)
                .build();
    }

    @Operation(summary = "Create area", description = "API create new area")
    @PostMapping("/add")
    public ApiResponse createArea(@RequestBody @Valid AreaCreationRequest req) {
        log.info("Create area {}", req);

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("area created")
                .data(areaService.save(req))
                .build();
    }

    @Operation(summary = "Update area", description = "API update area")
    @PutMapping("/upd")
    public ApiResponse updateArea(@RequestBody @Valid AreaUpdateRequest req) {
        log.info("Update area {}", req);

        areaService.update(req);

        return ApiResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("area updated successfully")
                .build();
    }

    @Operation(summary = "Delete area", description = "API delete area by id")
    @DeleteMapping("/del/{areaId}")
    public ApiResponse deleteArea(@PathVariable @Min(value = 1, message = "VaseId must be equal or greater than 1") Integer areaId) {
        log.info("Delete area {}", areaId);

        areaService.delete(areaId);

        return ApiResponse.builder()
                .status(HttpStatus.RESET_CONTENT.value())
                .message("area deleted successfully")
                .build();
    }
}

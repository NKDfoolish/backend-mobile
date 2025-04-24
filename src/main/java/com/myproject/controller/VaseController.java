package com.myproject.controller;

import com.myproject.dto.request.VaseCreationRequest;
import com.myproject.dto.request.VaseUpdateRequest;
import com.myproject.dto.response.ApiResponse;
import com.myproject.dto.response.VaseResponse;
import com.myproject.service.VaseService;
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
@RequestMapping("/vase")
@Tag(name = "Vase Controller", description = "Vase Controller")
@RequiredArgsConstructor
@Slf4j(topic = "VASE_CONTROLLER")
@Validated
public class VaseController {

    private final VaseService vaseService;

    @Operation(summary = "Get list vase", description = "API retrieve list of vase")
    @GetMapping("/list")
    public ApiResponse getList(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String sort,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {

        log.info("Get list vase");

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("vase list")
                .data(vaseService.findAll(keyword, sort, page, size))
                .build();
    }

    @Operation(summary = "Get vase detail", description = "API retrieve vase detail by id")
    @GetMapping("/{vaseId}")
    public ApiResponse getVaseDetail(@PathVariable @Min(value = 1, message = "VaseId must be equal or greater than 1") Integer vaseId) {
        log.info("Get vase detail {}", vaseId);

        VaseResponse vaseResponse = vaseService.findById(vaseId);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("vase")
                .data(vaseResponse)
                .build();
    }

    @Operation(summary = "Create vase", description = "API create new vase")
    @PostMapping("/add")
    public ApiResponse createVase(@RequestBody @Valid VaseCreationRequest req) {
        log.info("Create vase {}", req);

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("vase created")
                .data(vaseService.save(req))
                .build();
    }

    @Operation(summary = "Update vase", description = "API update vase")
    @PutMapping("/upd")
    public ApiResponse updateVase(@RequestBody @Valid VaseUpdateRequest req) {
        log.info("Update vase {}", req);

        vaseService.update(req);

        return ApiResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("vase updated successfully")
                .build();
    }

    @Operation(summary = "Delete vase", description = "API delete vase by id")
    @DeleteMapping("/del/{vaseId}")
    public ApiResponse deleteVase(@PathVariable @Min(value = 1, message = "VaseId must be equal or greater than 1") Integer vaseId) {
        log.info("Delete vase {}", vaseId);

        vaseService.delete(vaseId);

        return ApiResponse.builder()
                .status(HttpStatus.RESET_CONTENT.value())
                .message("vase deleted successfully")
                .build();
    }
}

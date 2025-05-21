package com.myproject.controller;

import com.myproject.dto.request.GardenCreationRequest;
import com.myproject.dto.request.GardenUpdateRequest;
import com.myproject.dto.response.ApiResponse;
import com.myproject.dto.response.GardenResponse;
import com.myproject.model.UserEntity;
import com.myproject.service.GardenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/garden")
@Tag(name = "Garden Controller", description = "Garden Controller")
@RequiredArgsConstructor
@Slf4j(topic = "GARDEN_CONTROLLER")
@Validated
public class GardenController {

    private final GardenService gardenService;

    @Operation(summary = "Get list garden", description = "API retrieve list of garden")
    @GetMapping("/list")
    public ApiResponse getList(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String sort,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {

        log.info("Get list garden");

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("user info email: " + user.getEmail() + "; id: " + user.getId());

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("garden list")
                .data(gardenService.findAll(keyword, sort, page, size))
                .build();
    }

    @Operation(summary = "Get garden detail", description = "API retrieve garden detail by id")
    @GetMapping("/{gardenId}")
    public ApiResponse getGardenDetail(@PathVariable @Min(value = 1, message = "GardenId must be equal or greater than 1") Integer gardenId) {
        log.info("Get vase detail {}", gardenId);

        GardenResponse gardenResponse = gardenService.findById(gardenId);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("garden")
                .data(gardenResponse)
                .build();
    }

    @Operation(summary = "Create garden", description = "API create new garden")
    @PostMapping("/add")
    @PreAuthorize("#req.userId == authentication.principal.id or hasAnyAuthority('manager', 'admin', 'sysadmin')")
    public ApiResponse createGarden(@RequestBody @Valid GardenCreationRequest req) {
        log.info("Create garden {}", req);

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("garden created")
                .data(gardenService.save(req))
                .build();
    }

    @Operation(summary = "Update garden", description = "API update garden")
    @PutMapping("/upd")
    public ApiResponse updateGarden(@RequestBody @Valid GardenUpdateRequest req) {
        log.info("Update garden {}", req);

        gardenService.update(req);

        return ApiResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("Garden updated successfully")
                .build();
    }

    @Operation(summary = "Delete garden", description = "API delete garden by id")
    @DeleteMapping("/del/{gardenId}")
    public ApiResponse deleteGarden(@PathVariable @Min(value = 1, message = "VaseId must be equal or greater than 1") Integer gardenId) {
        log.info("Delete garden {}", gardenId);

        gardenService.delete(gardenId);

        return ApiResponse.builder()
                .status(HttpStatus.RESET_CONTENT.value())
                .message("Garden deleted successfully")
                .build();
    }
}

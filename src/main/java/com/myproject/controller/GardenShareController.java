package com.myproject.controller;

import com.myproject.dto.request.ShareGardenRequest;
import com.myproject.dto.response.ApiResponse;
import com.myproject.dto.response.SharedGardenResponse;
import com.myproject.model.UserEntity;
import com.myproject.service.GardenShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/garden-share")
@Tag(name = "Garden Share Controller", description = "Garden Share Controller")
@RequiredArgsConstructor
@Slf4j(topic = "GARDEN_SHARE_CONTROLLER")
@Validated
public class GardenShareController {

    private final GardenShareService gardenShareService;

    @Operation(
            summary = "Share garden to user",
            description = "Owner shares garden permission to another user"
    )
    @PostMapping("/add")
    public ApiResponse shareGarden(@RequestBody @Valid ShareGardenRequest req) {

        log.info("Share garden request {}", req);

        UserEntity user = (UserEntity) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        log.info("Owner info email: {}; id: {}", user.getEmail(), user.getId());

        gardenShareService.shareGarden(user.getId(), req);

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Garden shared successfully")
                .build();
    }

    @Operation(
            summary = "Get shared gardens",
            description = "Get list of gardens shared to current user"
    )
    @GetMapping("/list")
    public ApiResponse getSharedGardens() {

        log.info("Get shared garden list");

        UserEntity user = (UserEntity) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        log.info("Shared user info email: {}; id: {}", user.getEmail(), user.getId());

        List<SharedGardenResponse> data =
                gardenShareService.getSharedGardens(user.getId());

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("shared garden list")
                .data(data)
                .build();
    }

    @Operation(
            summary = "Revoke shared garden permission",
            description = "Owner revoke shared garden permission from user"
    )
    @DeleteMapping("/del/{gardenId}/{userId}")
    public ApiResponse revokeShare(
            @PathVariable @Min(1) Integer gardenId,
            @PathVariable @Min(1) Long userId) {

        log.info("Revoke garden share gardenId={}, userId={}", gardenId, userId);

        UserEntity owner = (UserEntity) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        gardenShareService.revokeShare(owner.getId(), gardenId, userId);

        return ApiResponse.builder()
                .status(HttpStatus.RESET_CONTENT.value())
                .message("Garden share revoked successfully")
                .build();
    }
}

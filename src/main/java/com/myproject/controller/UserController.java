package com.myproject.controller;

import com.myproject.dto.response.ApiResponse;
import com.myproject.dto.request.UserCreationRequest;
import com.myproject.dto.request.UserPasswordRequest;
import com.myproject.dto.request.UserUpdateRequest;
import com.myproject.dto.response.UserResponse;
import com.myproject.model.UserEntity;
import com.myproject.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller", description = "User Controller")
@RequiredArgsConstructor
@Slf4j(topic = "USER_CONTROLLER")
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get list user", description = "API retrieve list of user")
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('manager', 'admin', 'sysadmin')")
    public ApiResponse getList(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String sort,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {

        log.info("Get list user");

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user list")
                .data(userService.findAll(keyword, sort, page, size))
                .build();
    }

    @Operation(summary = "Get user detail", description = "API retrieve user detail by id")
    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.id or hasAnyAuthority('manager', 'admin', 'sysadmin')")
    public ApiResponse getUserDetail(@PathVariable @Min(value = 1, message = "UserId must be equal or greater than 1") Long userId) {
        log.info("Get user detail {}", userId);

        UserResponse userResponse = userService.findById(userId);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user")
                .data(userResponse)
                .build();
    }

    @Operation(summary = "Get myinfo", description = "API retrieve user info by id")
    @GetMapping("/myinfo")
    public ApiResponse getMyInfo() {
        log.info("Get user information");

        UserEntity userCheck = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserResponse userResponse = userService.findById(userCheck.getId());

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user")
                .data(userResponse)
                .build();
    }

    @Hidden
    @GetMapping("/confirm-email")
    public void confirmEmail(@RequestParam String secretCode, HttpServletResponse response) throws IOException {
        log.info("Confirm email {}", secretCode);

        try {
            // check or compare secretCode from db
        } catch (Exception e) {
            log.error("Error confirm email!, errorMessage={}", e.getMessage());
        } finally {
            response.sendRedirect("http://localhost:8088/login");
        }
    }

    @Operation(summary = "Create user", description = "API create new user")
    @PostMapping("/add")
    public ApiResponse createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Create user {}", request);

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("user created successfully")
                .data(userService.save(request))
                .build();
    }

    @Operation(summary = "Update user", description = "API update user by id")
    @PutMapping("/upd")
    @PreAuthorize("#request.id == authentication.principal.id or " +
            "hasAnyAuthority('admin', 'sysadmin', 'manager')")
    public ApiResponse updateUser(@RequestBody @Valid UserUpdateRequest request) {
        log.info("Update user {}", request);

        userService.update(request);

        return ApiResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("user updated successfully")
                .build();
    }

    @Operation(summary = "Change password", description = "API change password by id")
    @PatchMapping("/change-pwd")
    @PreAuthorize("#request.id == authentication.principal.id or " +
            "hasAnyAuthority('admin', 'sysadmin', 'manager')")
    public ApiResponse changePassword(@RequestBody @Valid UserPasswordRequest request) {
        log.info("Change password {}", request);

        userService.changePassword(request);

        return ApiResponse.builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("user updated successfully")
                .build();
    }

    @Operation(summary = "Delete user", description = "API delete user by id")
    @DeleteMapping("/del/{userId}")
    @PreAuthorize("hasAnyAuthority('manager', 'admin', 'sysadmin')")
    public ApiResponse deleteUser(@PathVariable @Min(value = 1, message = "UserId must be equal or greater than 1") Long userId) {
        log.info("Delete user {}", userId);

        userService.delete(userId);

        return ApiResponse.builder()
                .status(HttpStatus.RESET_CONTENT.value())
                .message("user deleted successfully")
                .build();
    }

    @Operation(summary = "Get user ID by username", description = "API retrieve user ID by username")
    @GetMapping("/contact-id")
    @PreAuthorize("hasAnyAuthority('manager', 'admin', 'sysadmin')")
    public ApiResponse getUserIdByUsername(@RequestParam String username) {
        log.info("Get user ID by username: {}", username);

        Long userId = userService.findUserIdByUsername(username);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user ID retrieved successfully")
                .data(userId)
                .build();
    }
}

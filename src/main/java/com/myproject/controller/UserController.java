package com.myproject.controller;

import com.myproject.controller.request.UserCreationRequest;
import com.myproject.controller.request.UserPasswordRequest;
import com.myproject.controller.request.UserUpdateRequest;
import com.myproject.controller.response.UserResponse;
import com.myproject.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

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
    public Map<String, Object> getList(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String sort,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {

        log.info("Get list user");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Success");
        result.put("data", userService.findAll(keyword, sort, page, size));

        return result;
    }

    @Operation(summary = "Get user detail", description = "API retrieve user detail by id")
    @GetMapping("/{userId}")
    public Map<String, Object> getUserDetail(@PathVariable @Min(value = 1, message = "UserId must be equal or greater than 1") Long userId) {
        log.info("Get user detail {}", userId);

        UserResponse userResponse = userService.findById(userId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Success");
        result.put("data", userResponse);

        return result;
    }

    @GetMapping("/confirm-email")
    public void confirmEmail(@RequestParam String secretCode, HttpServletResponse response) throws IOException {
        log.info("Confirm email {}", secretCode);

        try {
            // TODO check or compare secretCode from db
        } catch (Exception e) {
            log.error("Error confirm email!, errorMessage={}", e.getMessage());
        } finally {
            response.sendRedirect("http://localhost:8080/login");
        }
    }

    @Operation(summary = "Create user", description = "API create new user")
    @PostMapping("/add")
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Create user {}", request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Success");
        result.put("data", userService.save(request));

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "Update user", description = "API update user by id")
    @PutMapping("/upd")
    public Map<String, Object> updateUser(@RequestBody @Valid UserUpdateRequest request) {
        log.info("Update user {}", request);

        userService.update(request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "Success");
        result.put("data", "");

        return result;
    }

    @Operation(summary = "Change password", description = "API change password by id")
    @PatchMapping("/change-pwd")
    public Map<String, Object> changePassword(@RequestBody @Valid UserPasswordRequest request) {
        log.info("Change password {}", request);

        userService.changePassword(request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message", "Success");
        result.put("data", "");

        return result;
    }

    @Operation(summary = "Delete user", description = "API delete user by id")
    @DeleteMapping("/del/{userId}")
    public Map<String, Object> deleteUser(@PathVariable @Min(value = 1, message = "UserId must be equal or greater than 1") Long userId) {
        log.info("Delete user {}", userId);

        userService.delete(userId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message", "Success");
        result.put("data", "");

        return result;
    }
}

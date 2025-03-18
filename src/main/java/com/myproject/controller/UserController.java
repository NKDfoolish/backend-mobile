package com.myproject.controller;

import com.myproject.controller.request.UserCreationRequest;
import com.myproject.controller.request.UserPasswordRequest;
import com.myproject.controller.request.UserUpdateRequest;
import com.myproject.controller.response.UserResponse;
import com.myproject.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller", description = "User Controller")
@RequiredArgsConstructor
@Slf4j(topic = "USER_CONTROLLER")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get list user", description = "API retrieve list of user")
    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam(required = false) String keyword,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {

        UserResponse userResponse1 = new UserResponse();
        userResponse1.setId(1L);
        userResponse1.setFirstName("John");
        userResponse1.setLastName("Doe");
        userResponse1.setEmail("John@gmail.com");
        userResponse1.setPhone("123321123321");
        userResponse1.setBirthDate(new Date());
        userResponse1.setGender("M");
        userResponse1.setUsername("johndoe");

        UserResponse userResponse2 = new UserResponse();
        userResponse2.setId(2L);
        userResponse2.setFirstName("Jane");
        userResponse2.setLastName("Doe");
        userResponse2.setEmail("jane@gmail.com");
        userResponse2.setPhone("123321123321");
        userResponse2.setBirthDate(new Date());
        userResponse2.setGender("F");
        userResponse2.setUsername("janedoe");

        List<UserResponse> userResponses = List.of(userResponse1, userResponse2);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Success");
        result.put("data", userResponses);

        return result;
    }

    @Operation(summary = "Get user detail", description = "API retrieve user detail by id")
    @GetMapping("/{userId}")
    public Map<String, Object> getUserDetail(@PathVariable Long userId) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");
        userResponse.setEmail("John@gmail.com");
        userResponse.setPhone("123321123321");
        userResponse.setBirthDate(new Date());
        userResponse.setGender("M");
        userResponse.setUsername("johndoe");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Success");
        result.put("data", userResponse);

        return result;
    }

    @Operation(summary = "Create user", description = "API create new user")
    @PostMapping("/add")
    public ResponseEntity<Object> createUser(@RequestBody UserCreationRequest request) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Success");
        result.put("data", userService.save(request));

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "Update user", description = "API update user by id")
    @PutMapping("/upd")
    public Map<String, Object> updateUser(@RequestBody UserUpdateRequest request) {
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
    public Map<String, Object> changePassword(@RequestBody UserPasswordRequest request) {
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
    public Map<String, Object> deleteUser(@PathVariable Long userId) {
        log.info("Delete user {}", userId);

        userService.delete(userId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message", "Success");
        result.put("data", "");

        return result;
    }
}

package com.myproject.controller;

import com.myproject.common.Gender;
import com.myproject.dto.response.ApiResponse;
import com.myproject.dto.request.UserCreationRequest;
import com.myproject.dto.request.UserPasswordRequest;
import com.myproject.dto.request.UserUpdateRequest;
import com.myproject.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/mockup/user")
@Tag(name = "Mockup User Controller", description = "User Controller")
@Hidden
public class MockupUserController {

    @Operation(summary = "Get list user", description = "API retrieve list of user")
    @GetMapping("/list")
    public ApiResponse getList(@RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {

        UserResponse userResponse1 = new UserResponse();
        userResponse1.setId(1L);
        userResponse1.setFirstName("John");
        userResponse1.setLastName("Doe");
        userResponse1.setEmail("John@gmail.com");
        userResponse1.setPhone("123321121321");
        userResponse1.setBirthday(new Date());
        userResponse1.setGender(Gender.MALE);
        userResponse1.setUsername("johndoe");

        UserResponse userResponse2 = new UserResponse();
        userResponse2.setId(2L);
        userResponse2.setFirstName("Jane");
        userResponse2.setLastName("Doe");
        userResponse2.setEmail("jane@gmail.com");
        userResponse2.setPhone("123021123321");
        userResponse2.setBirthday(new Date());
        userResponse2.setGender(Gender.FEMALE);
        userResponse2.setUsername("janedoe");

        List<UserResponse> userResponses = List.of(userResponse1, userResponse2);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user list")
                .data(userResponses)
                .build();
    }

    @Operation(summary = "Get user detail", description = "API retrieve user detail by id")
    @GetMapping("/{userId}")
    public ApiResponse getUserDetail(@PathVariable Long userId) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");
        userResponse.setEmail("John@gmail.com");
        userResponse.setPhone("120021123321");
        userResponse.setBirthday(new Date());
        userResponse.setGender(Gender.MALE);
        userResponse.setUsername("johndoe");

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user")
                .data(userResponse)
                .build();
    }

    @Operation(summary = "Create user", description = "API create new user")
    @PostMapping("/add")
    public ApiResponse createUser(UserCreationRequest request) {

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("user created successfully")
                .data(3)
                .build();
    }

    @Operation(summary = "Update user", description = "API update user by id")
    @PutMapping("/upd")
    public ApiResponse updateUser(UserUpdateRequest request) {

        return ApiResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("user updated successfully")
                .build();
    }

    @Operation(summary = "Change password", description = "API change password by id")
    @PatchMapping("/change-pwd")
    public ApiResponse changePassword(UserPasswordRequest request) {

        return ApiResponse.builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("user password changed successfully")
                .build();
    }

    @Operation(summary = "Delete user", description = "API delete user by id")
    @DeleteMapping("/{userId}/del")
    public ApiResponse deleteUser(@PathVariable Long userId) {

        return ApiResponse.builder()
                .status(HttpStatus.RESET_CONTENT.value())
                .message("user deleted successfully")
                .build();
    }
}

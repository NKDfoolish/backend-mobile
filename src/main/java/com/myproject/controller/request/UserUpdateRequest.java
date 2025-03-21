package com.myproject.controller.request;

import com.myproject.common.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
public class UserUpdateRequest {

    @NotNull(message = "Id is required")
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Email should be valid")
    private String email;
    private String phone;
    private Gender gender;
    private Date birthday;
    private String username;
    private List<AddressRequest> addresses;
}

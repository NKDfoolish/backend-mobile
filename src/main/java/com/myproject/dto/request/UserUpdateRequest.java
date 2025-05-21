package com.myproject.dto.request;

import com.myproject.common.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UserUpdateRequest {

    @NotNull(message = "Id is required")
    private Long id;

    private String firstName;
    private String lastName;

    @Email(message = "Email should be valid")
    private String email;
    private String phone;
    private Gender gender;
    private Date birthday;
    private String username;
    private List<AddressRequest> addresses;
}

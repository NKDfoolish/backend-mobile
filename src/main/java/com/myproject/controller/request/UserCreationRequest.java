package com.myproject.controller.request;

import com.myproject.common.Gender;
import com.myproject.common.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class UserCreationRequest implements Serializable {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;
    private Gender gender;
    private Date birthday;
    private String username;

    @Email(message = "Email should be valid")
    private String email;
    private String phone;
    private UserType type;
    private List<AddressRequest> addresses;
}

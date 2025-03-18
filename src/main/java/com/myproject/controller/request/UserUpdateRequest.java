package com.myproject.controller.request;

import com.myproject.common.Gender;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
public class UserUpdateRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Gender gender;
    private Date birthday;
    private String username;
    private List<AddressRequest> addresses;
}

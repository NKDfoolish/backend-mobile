package com.myproject.controller.request;

import lombok.Getter;

import java.util.Date;

@Getter
public class UserUpdateRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    private Date birthDate;
    private String username;
}

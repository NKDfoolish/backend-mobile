package com.myproject.controller.request;

import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
public class UserCreationRequest implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    private Date birthDate;
}

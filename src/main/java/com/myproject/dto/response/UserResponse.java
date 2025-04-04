package com.myproject.dto.response;

import com.myproject.common.Gender;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Gender gender;
    private Date birthday;
    private String username;
}

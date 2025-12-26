package com.myproject.service;

import com.myproject.dto.request.UserCreationRequest;
import com.myproject.dto.request.UserPasswordRequest;
import com.myproject.dto.request.UserUpdateRequest;
import com.myproject.dto.response.UserPageResponse;
import com.myproject.dto.response.UserResponse;

public interface UserService {

    UserPageResponse findAll(String keyword, String sort, int page, int size);

    UserResponse findById(Long id);

    UserResponse findByUsername(String username);

    UserResponse findByEmail(String email);

    long save(UserCreationRequest req);

    void update(UserUpdateRequest req);

    void changePassword(UserPasswordRequest req);

    void delete(Long id);

    Long findUserIdByUsername(String username);
}

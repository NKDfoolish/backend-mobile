package com.myproject.service;

import com.myproject.common.Gender;
import com.myproject.common.UserStatus;
import com.myproject.common.UserType;
import com.myproject.controller.request.AddressRequest;
import com.myproject.controller.request.UserCreationRequest;
import com.myproject.controller.request.UserPasswordRequest;
import com.myproject.controller.request.UserUpdateRequest;
import com.myproject.controller.response.UserPageResponse;
import com.myproject.controller.response.UserResponse;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.UserEntity;
import com.myproject.repository.AddressRepository;
import com.myproject.repository.UserRepository;
import com.myproject.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    private @Mock UserRepository userRepository;
    private @Mock EmailService emailService;
    private @Mock PasswordEncoder passwordEncoder;
    private @Mock AddressRepository addressRepository;

    private static UserEntity adela;
    private static UserEntity illiao;

    @BeforeAll
    static void beforeAll() {
        adela = new UserEntity();
        adela.setId(1L);
        adela.setFirstName("Adela");
        adela.setLastName("Mavis");
        adela.setGender(Gender.MALE);
        adela.setBirthday(new Date());
        adela.setEmail("adela@gmail.com");
        adela.setPhone("1234567890");
        adela.setUsername("adelamavis");
        adela.setPassword("password");
        adela.setType(UserType.USER);
        adela.setStatus(UserStatus.INACTIVE);

        illiao = new UserEntity();
        illiao.setId(2L);
        illiao.setFirstName("Illiao");
        illiao.setLastName("Vey");
        illiao.setGender(Gender.FEMALE);
        illiao.setBirthday(new Date());
        illiao.setEmail("illiaovey@gmail.com");
        illiao.setPhone("0987654321");
        illiao.setUsername("illiaovey");
        illiao.setPassword("password");
        illiao.setType(UserType.USER);
        illiao.setStatus(UserStatus.INACTIVE);

    }

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository,addressRepository,passwordEncoder,emailService);
    }

    @Test
    void testGetListUsers_Success() {
        // fake method
        Page<UserEntity> usePage = new PageImpl<>(Arrays.asList(adela, illiao));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(usePage);

        // call method want to test
        UserPageResponse result = userService.findAll(null, null, 0, 2);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testSearchUsers_Success() {
        // fake method
        Page<UserEntity> usePage = new PageImpl<>(Arrays.asList(illiao));
        when(userRepository.searchByKeyword(any(), any(Pageable.class))).thenReturn(usePage);

        // call method want to test
        UserPageResponse result = userService.findAll("illiao", null, 0, 2);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetListUsers_Empty() {
        // fake method
        Page<UserEntity> usePage = new PageImpl<>(List.of());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(usePage);

        // call method want to test
        UserPageResponse result = userService.findAll(null, null, 0, 2);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testGetUserById_Success() {
        // fake method
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(adela));

        // call method want to test
        UserResponse result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetUserById_Failure() {
        // fake method
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.findById(2L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testSaveUser_Success() {
        when(userRepository.save(any(UserEntity.class))).thenReturn(adela);

        UserCreationRequest userCreationRequest = new UserCreationRequest();
        userCreationRequest.setFirstName("Adela");
        userCreationRequest.setLastName("Mavis");
        userCreationRequest.setGender(Gender.MALE);
        userCreationRequest.setBirthday(new Date());
        userCreationRequest.setEmail("adelamavis@gmail.com");
        userCreationRequest.setPhone("1234567890");
        userCreationRequest.setUsername("adelamavis");

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setApartmentNumber("123");
        addressRequest.setFloor("1");
        addressRequest.setBuilding("A");
        addressRequest.setStreetNumber("123");
        addressRequest.setStreet("ABC");
        addressRequest.setCity("XYZ");
        addressRequest.setCountry("Vietnam");
        addressRequest.setAddressType(1);

        userCreationRequest.setAddresses(List.of(addressRequest));

        long userId = userService.save(userCreationRequest);

        assertEquals(1L, userId);
    }

    @Test
    void update() {
        Long userId = 2L;

        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(userId);
        updatedUser.setFirstName("Illiao");
        updatedUser.setLastName("Vey");
        updatedUser.setGender(Gender.FEMALE);
        updatedUser.setBirthday(new Date());
        updatedUser.setEmail("illiaovey@gmail.com");
        updatedUser.setPhone("0987654321");
        updatedUser.setUsername("illiaovey");
        updatedUser.setType(UserType.USER);
        updatedUser.setStatus(UserStatus.INACTIVE);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.ofNullable(illiao));
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUser);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setId(userId);
        userUpdateRequest.setFirstName("Jamy");
        userUpdateRequest.setLastName("Cold");
        userUpdateRequest.setGender(Gender.FEMALE);
        userUpdateRequest.setBirthday(new Date());
        userUpdateRequest.setEmail("illiaovey@gmail.com");
        userUpdateRequest.setPhone("0987654321");
        userUpdateRequest.setUsername("illiaovey");

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setApartmentNumber("123");
        addressRequest.setFloor("1");
        addressRequest.setBuilding("A");
        addressRequest.setStreetNumber("123");
        addressRequest.setStreet("ABC");
        addressRequest.setCity("XYZ");
        addressRequest.setCountry("Vietnam");
        addressRequest.setAddressType(1);

        userUpdateRequest.setAddresses(List.of(addressRequest));

        userService.update(userUpdateRequest);

        UserResponse result = userService.findById(userId);
        assertNotNull(result);
        assertEquals("Jamy", result.getFirstName());
        assertEquals("Cold", result.getLastName());

    }

    @Test
    void testChangePassword_Success() {
        Long userId = 2L;

        UserPasswordRequest request = new UserPasswordRequest();
        request.setId(userId);
        request.setPassword("newPassword");
        request.setConfirmPassword("newPassword");

        // Simulate the behavior of the repository and password encoder
        when(userRepository.findById(userId)).thenReturn(Optional.of(illiao));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedNewPassword");

        // call method want to test
        userService.changePassword(request);

        // Verify the result
        assertEquals("encodedNewPassword", illiao.getPassword());
        verify(userRepository, times(1)).save(illiao);
        verify(passwordEncoder, times(1)).encode(request.getPassword());
    }

    @Test
    void testDeleteUser_Success() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.ofNullable(adela));

        userService.delete(userId);

        assertEquals(UserStatus.INACTIVE, adela.getStatus());
        verify(userRepository, times(1)).save(adela);
    }

    @Test
    void testUserNotFound_ThrowsException() {
        // repair data
        Long userId = 1L;

        // simulate the behavior of the repository
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // call method want to test
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.delete(userId));

        // check the result
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}
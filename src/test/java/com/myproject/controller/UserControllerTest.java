package com.myproject.controller;

import com.myproject.common.Gender;
import com.myproject.dto.response.UserPageResponse;
import com.myproject.dto.response.UserResponse;
import com.myproject.service.JwtService;
import com.myproject.service.UserService;
import com.myproject.service.UserServiceDetail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserServiceDetail userServiceDetail;

    @MockBean
    private JwtService jwtService;

    private static UserResponse adelaJava;
    private static UserResponse johnDoe;

    @BeforeAll
    static void setUp() {

        adelaJava = new UserResponse();
        adelaJava.setId(1L);
        adelaJava.setFirstName("adela");
        adelaJava.setLastName("Java");
        adelaJava.setGender(Gender.MALE);
        adelaJava.setBirthday(new Date());
        adelaJava.setEmail("adelajava@gmail.com");
        adelaJava.setPhone("0975118228");
        adelaJava.setUsername("adelajava");

        johnDoe = new UserResponse();
        johnDoe.setId(2L);
        johnDoe.setFirstName("John");
        johnDoe.setLastName("Doe");
        johnDoe.setGender(Gender.FEMALE);
        johnDoe.setBirthday(new Date());
        johnDoe.setEmail("johndoe@gmail.com");
        johnDoe.setPhone("0123456789");
        johnDoe.setUsername("johndoe");
    }

    @Test
    @WithMockUser(authorities = {"admin","manager"})
    void testGetUser() throws Exception {
        List<UserResponse> users = List.of(adelaJava, johnDoe);

        UserPageResponse userPageResponse = new UserPageResponse();
        userPageResponse.setPageNumber(0);
        userPageResponse.setPageSize(20);
        userPageResponse.setTotalPages(1);
        userPageResponse.setTotalElements(2);
        userPageResponse.setUsers(users);

        when(userService.findAll(null, null, 0, 20)).thenReturn(userPageResponse);

        // Perform the test
        mockMvc.perform(get("/user/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("user list")))
                .andExpect(jsonPath("$.data.totalPages", is(1)))
                .andExpect(jsonPath("$.data.totalElements", is(2)))
                .andExpect(jsonPath("$.data.users[0].id", is(1)))
                .andExpect(jsonPath("$.data.users[0].username", is("adelajava")));
    }

//    @Test
//    @WithMockUser(authorities = {"user"})
//    void shouldGetUserDetail() throws Exception {
//        when(userService.findById(anyLong())).thenReturn(adelaJava);
//
//        // Perform the test
//        mockMvc.perform(get("/user/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status", is(200)))
//                .andExpect(jsonPath("$.message", is("user")))
//                .andExpect(jsonPath("$.data.id", is(1)))
//                .andExpect(jsonPath("$.data.firstName", is("adela")))
//                .andExpect(jsonPath("$.data.lastName", is("Java")))
//                .andExpect(jsonPath("$.data.email", is("adelajava@gmail.com")));
//    }
}

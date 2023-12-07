package com.bit.galleog.runtracker.controller;

import static com.bit.galleog.runtracker.TestFixtures.BARBARA_MOORE;
import static com.bit.galleog.runtracker.TestFixtures.JOHN_SMITH;
import static com.bit.galleog.runtracker.TestFixtures.NEW_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bit.galleog.runtracker.domain.User;
import com.bit.galleog.runtracker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

/**verify(service).deleteUser(JOHN_SMITH.getId());
 * Tests for {@link }
 *
 * @author Oleg_Galkin
 */
@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@ExtendWith({
        SpringExtension.class,
        MockitoExtension.class
})
class UserControllerTest {
    private static final String BASE_URL = "/users";

    @MockBean
    private UserService service;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void shouldGetUserById() throws Exception {
        when(service.getUserById(JOHN_SMITH.getId())).thenReturn(Optional.of(JOHN_SMITH));

        mockMvc.perform(get(BASE_URL + "/" + JOHN_SMITH.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(JOHN_SMITH.getId()))
                .andExpect(jsonPath("$.firstName").value(JOHN_SMITH.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(JOHN_SMITH.getLastName()))
                .andExpect(jsonPath("$.birthDate")
                        .value(JOHN_SMITH.getBirthDate().toString()))
                .andExpect(jsonPath("$.sex").value(JOHN_SMITH.getSex().name()));

        verify(service).getUserById(JOHN_SMITH.getId());
    }

    @Test
    void shouldFailIfUserDoesNotExist() throws Exception {
        when(service.getUserById(JOHN_SMITH.getId())).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/" + JOHN_SMITH.getId()))
                .andExpect(status().isNotFound());

        verify(service).getUserById(JOHN_SMITH.getId());
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        when(service.getAllUsers()).thenReturn(List.of(JOHN_SMITH, BARBARA_MOORE));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(JOHN_SMITH.getId()))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value(JOHN_SMITH.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(JOHN_SMITH.getLastName()))
                .andExpect(jsonPath("$[0].birthDate")
                        .value(JOHN_SMITH.getBirthDate().toString()))
                .andExpect(jsonPath("$[0].sex").value(JOHN_SMITH.getSex().name()))
                .andExpect(jsonPath("$[1].id").value(BARBARA_MOORE.getId()))
                .andExpect(jsonPath("$[1].firstName")
                        .value(BARBARA_MOORE.getFirstName()))
                .andExpect(jsonPath("$[1].lastName").value(BARBARA_MOORE.getLastName()))
                .andExpect(jsonPath("$[1].birthDate")
                        .value(BARBARA_MOORE.getBirthDate().toString()))
                .andExpect(jsonPath("$[1].sex").value(BARBARA_MOORE.getSex().name()));

        verify(service).getAllUsers();
    }

    @Test
    void shouldCreateNewUser() throws Exception {
        when(service.createUser(userCaptor.capture())).thenReturn(JOHN_SMITH);

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(NEW_USER))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(JOHN_SMITH.getId()))
                .andExpect(jsonPath("$.firstName").value(JOHN_SMITH.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(JOHN_SMITH.getLastName()))
                .andExpect(jsonPath("$.birthDate")
                        .value(JOHN_SMITH.getBirthDate().toString()))
                .andExpect(jsonPath("$.sex").value(JOHN_SMITH.getSex().name()));

        assertThat(userCaptor.getValue()).extracting(
                User::getId,
                User::getFirstName,
                User::getLastName,
                User::getBirthDate,
                User::getSex
        ).containsExactly(
                null,
                NEW_USER.getFirstName(),
                NEW_USER.getLastName(),
                NEW_USER.getBirthDate(),
                NEW_USER.getSex()
        );
    }

    @Test
    void shouldUpdateUser() throws Exception {
        when(service.updateUser(eq(JOHN_SMITH.getId()), userCaptor.capture()))
                .thenReturn(Optional.of(JOHN_SMITH));

        mockMvc.perform(
                        put(BASE_URL + "/" + JOHN_SMITH.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(NEW_USER))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(JOHN_SMITH.getId()))
                .andExpect(jsonPath("$.firstName").value(JOHN_SMITH.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(JOHN_SMITH.getLastName()))
                .andExpect(jsonPath("$.birthDate")
                        .value(JOHN_SMITH.getBirthDate().toString()))
                .andExpect(jsonPath("$.sex").value(JOHN_SMITH.getSex().name()));

        assertThat(userCaptor.getValue()).extracting(
                User::getId,
                User::getFirstName,
                User::getLastName,
                User::getBirthDate,
                User::getSex
        ).containsExactly(
                null,
                NEW_USER.getFirstName(),
                NEW_USER.getLastName(),
                NEW_USER.getBirthDate(),
                NEW_USER.getSex()
        );
    }

    @Test
    void shouldNotUpdateUserThatDoesNotExists() throws Exception {
        when(service.updateUser(eq(JOHN_SMITH.getId()), userCaptor.capture()))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                put(BASE_URL + "/" + JOHN_SMITH.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(NEW_USER))
        ).andExpect(status().isNotFound());

        assertThat(userCaptor.getValue()).extracting(
                User::getId,
                User::getFirstName,
                User::getLastName,
                User::getBirthDate,
                User::getSex
        ).containsExactly(
                null,
                NEW_USER.getFirstName(),
                NEW_USER.getLastName(),
                NEW_USER.getBirthDate(),
                NEW_USER.getSex()
        );
    }

    @Test
    void shouldDeleteUser() throws Exception {
        when(service.deleteUser(JOHN_SMITH.getId())).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/" + JOHN_SMITH.getId()))
                .andExpect(status().isNoContent());

        verify(service).deleteUser(JOHN_SMITH.getId());
    }

    @Test
    void shouldNotDeleteUser() throws Exception {
        when(service.deleteUser(JOHN_SMITH.getId())).thenReturn(false);

        mockMvc.perform(delete(BASE_URL + "/" + JOHN_SMITH.getId()))
                .andExpect(status().isNotFound());

        verify(service).deleteUser(JOHN_SMITH.getId());
    }
}
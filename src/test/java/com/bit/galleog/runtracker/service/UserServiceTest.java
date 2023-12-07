package com.bit.galleog.runtracker.service;

import static com.bit.galleog.runtracker.TestFixtures.BARBARA_MOORE;
import static com.bit.galleog.runtracker.TestFixtures.JOHN_SMITH;
import static com.bit.galleog.runtracker.TestFixtures.NEW_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bit.galleog.runtracker.domain.User;
import com.bit.galleog.runtracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

/**
 * Tests for {@link UserService}.
 *
 * @author Oleg_Galkin
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repository;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(repository);
    }

    @Test
    void shouldGetUserById() {
        when(repository.getById(JOHN_SMITH.getId())).thenReturn(Optional.of(JOHN_SMITH));

        var optional = service.getUserById(JOHN_SMITH.getId());

        assertThat(optional).isPresent();
        assertThat(optional.get()).extracting(
                User::getId,
                User::getFirstName,
                User::getLastName,
                User::getBirthDate,
                User::getSex
        ).containsExactly(
                JOHN_SMITH.getId(),
                JOHN_SMITH.getFirstName(),
                JOHN_SMITH.getLastName(),
                JOHN_SMITH.getBirthDate(),
                JOHN_SMITH.getSex()
        );

        verify(repository).getById(JOHN_SMITH.getId());
    }

    @Test
    void shouldGetAllUsers() {
        when(repository.getAllUsers()).thenReturn(List.of(JOHN_SMITH, BARBARA_MOORE));

        var users = service.getAllUsers();

        assertThat(users).extracting(
                User::getId,
                User::getFirstName,
                User::getLastName,
                User::getBirthDate,
                User::getSex
        ).containsExactly(
                tuple(
                        JOHN_SMITH.getId(),
                        JOHN_SMITH.getFirstName(),
                        JOHN_SMITH.getLastName(),
                        JOHN_SMITH.getBirthDate(),
                        JOHN_SMITH.getSex()
                ),
                tuple(
                        BARBARA_MOORE.getId(),
                        BARBARA_MOORE.getFirstName(),
                        BARBARA_MOORE.getLastName(),
                        BARBARA_MOORE.getBirthDate(),
                        BARBARA_MOORE.getSex()
                )
        );

        verify(repository).getAllUsers();
    }

    @Test
    void shouldCreateUser() {
        when(repository.create(userCaptor.capture())).thenReturn(JOHN_SMITH);

        var created = service.createUser(NEW_USER);

        assertThat(created).extracting(
                User::getId,
                User::getFirstName,
                User::getLastName,
                User::getBirthDate,
                User::getSex
        ).containsExactly(
                JOHN_SMITH.getId(),
                JOHN_SMITH.getFirstName(),
                JOHN_SMITH.getLastName(),
                JOHN_SMITH.getBirthDate(),
                JOHN_SMITH.getSex()
        );

        assertThat(userCaptor.getValue()).extracting(
                User::getFirstName,
                User::getLastName,
                User::getBirthDate,
                User::getSex
        ).containsExactly(
                JOHN_SMITH.getFirstName(),
                JOHN_SMITH.getLastName(),
                JOHN_SMITH.getBirthDate(),
                JOHN_SMITH.getSex()
        );
    }

    @Test
    void shouldUpdateExistingUser() {
        when(repository.update(eq(JOHN_SMITH.getId()), userCaptor.capture()))
                .thenReturn(Optional.of(JOHN_SMITH));

        var optional = service.updateUser(JOHN_SMITH.getId(), JOHN_SMITH);

        assertThat(optional).isPresent();
        assertThat(optional.get()).extracting(
                User::getId,
                User::getFirstName,
                User::getLastName,
                User::getBirthDate,
                User::getSex
        ).containsExactly(
                JOHN_SMITH.getId(),
                JOHN_SMITH.getFirstName(),
                JOHN_SMITH.getLastName(),
                JOHN_SMITH.getBirthDate(),
                JOHN_SMITH.getSex()
        );

        assertThat(userCaptor.getValue()).extracting(
                User::getFirstName,
                User::getLastName,
                User::getBirthDate,
                User::getSex
        ).containsExactly(
                JOHN_SMITH.getFirstName(),
                JOHN_SMITH.getLastName(),
                JOHN_SMITH.getBirthDate(),
                JOHN_SMITH.getSex()
        );
    }

    @Test
    void shouldDeleteUser() {
        when(repository.delete(JOHN_SMITH.getId())).thenReturn(true);

        assertThat(service.deleteUser(JOHN_SMITH.getId())).isTrue();

        verify(repository).delete(JOHN_SMITH.getId());
    }
}
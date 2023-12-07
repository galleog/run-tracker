package com.bit.galleog.runtracker.service;

import com.bit.galleog.runtracker.domain.User;
import com.bit.galleog.runtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for users
 *
 * @author Oleg_Galkin
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    /**
     * Gets a user by its identifier.
     *
     * @param id the user's identifier
     * @return the user with the given identifier, or {@link Optional#empty()}
     * if there is no user with that identifier
     */
    public Optional<User> getUserById(long id) {
        return repository.getById(id);
    }

    /**
     * Gets a list of all registered users
     */
    public List<User> getAllUsers() {
        return repository.getAllUsers();
    }

    /**
     * Creates a new user.
     *
     * @param user the attributes of the user to be created
     * @return the created user
     */
    public User createUser(@NonNull User user) {
        Validate.notNull(user);
        return repository.create(user);
    }

    /**
     * Updates the user with the given identifier.
     *
     * @param id   the user's identifier
     * @param user the attributes of the user to be updated
     * @return the updated user or {@link Optional#empty()}
     * if there is no user with the specified identifier
     */
    public Optional<User> updateUser(long id, @NonNull User user) {
        Validate.notNull(user);
        return repository.update(id, user);
    }

    /**
     * Deletes the user identified by their identifier.
     *
     * @param id the identifier of the user to be deleted
     * @return {@code true} if the user was deleted;
     * {@code false} if there is no user with the specified identifier
     */
    public boolean deleteUser(long id) {
        return repository.delete(id);
    }
}

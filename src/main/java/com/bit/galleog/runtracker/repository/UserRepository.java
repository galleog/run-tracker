package com.bit.galleog.runtracker.repository;

import com.bit.galleog.runtracker.domain.User;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link User}.
 *
 * @author Oleg_Galkin
 */
public interface UserRepository {
    /**
     * Gets a user by its identifier.
     *
     * @param id the user's identifier
     * @return the user with the given identifier, or {@link Optional#empty()}
     * if there is no user with that identifier
     */
    Optional<User> getById(long id);

    /**
     * Gets a list of all registered users ordered by their identifiers.
     */
    List<User> getAllUsers();

    /**
     * Creates a new user.
     *
     * @param user the attributes of the user to be created
     * @return the created user
     */
    User create(@NonNull User user);

    /**
     * Updates the user with the given identifier.
     *
     * @param id   the user's identifier
     * @param user the attributes of the user to be updated
     * @return the updated user or {@link Optional#empty()}
     * if there is no user with the specified identifier
     */
    Optional<User> update(long id, @NonNull User user);

    /**
     * Deletes the user identified by their id.
     *
     * @param id the identifier of the user to be deleted
     * @return {@code true} if the user was deleted;
     * {@code false} if there is no user with the specified identifier
     */
    boolean delete(long id);
}

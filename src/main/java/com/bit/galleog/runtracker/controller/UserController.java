package com.bit.galleog.runtracker.controller;

import com.bit.galleog.runtracker.domain.User;
import com.bit.galleog.runtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller to work with users.
 *
 * @author Oleg_Galkin
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    /**
     * Gets a list of all registered users.
     */
    @GetMapping
    public List<User> getAllUsers() {
        // it's better to use pagination here otherwise wr can get OutOfMemory here
        // if there're a lot of users, but the test conditions don't mention it
        return service.getAllUsers();
    }

    /**
     * Gets the user by their identifier.
     *
     * @param id the user's identifier
     * @return the found user or {@link HttpStatus#NOT_FOUND}
     * if there is no user with the specified identifier
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") long id) {
        return service.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new user.
     *
     * @param user the attributes of the user to be created
     * @return the created user
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return service.createUser(user);
    }

    /**
     * Updates the user with the given identifier.
     *
     * @param id   the user's identifier
     * @param user the attributes of the user to be updated
     * @return the updated user or {@link HttpStatus#NOT_FOUND}
     * if there is no user with the specified identifier
     */
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@PathVariable("id") long id, @RequestBody User user) {
        return service.updateUser(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes the user with the given identifier.
     *
     * @param id the identifier of the user to be deleted
     * @return {@link HttpStatus#NO_CONTENT} if the user was deleted, or {@link HttpStatus#NOT_FOUND}
     * if there is no user with the specified identifier
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
        var response = service.deleteUser(id) ?
                ResponseEntity.noContent() : ResponseEntity.notFound();
        return response.build();
    }
}

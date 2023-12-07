package com.bit.galleog.runtracker.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

/**
 * Entity for users of the application.
 *
 * @author Oleg_Galkin
 */
@Getter
@JsonDeserialize(builder = User.UserBuilder.class)
public final class User {
    /**
     * User's identifier.
     */
    private final Long id;

    /**
     * User's first name.
     */
    private String firstName;

    /**
     * User's last name.
     */
    private String lastName;

    /**
     * User's birth date.
     */
    private LocalDate birthDate;

    /**
     * User's gender.
     */
    private Sex sex;

    @Builder
    private User(@Nullable Long id, @NonNull String firstName, @NonNull String lastName,
                 @NonNull LocalDate birthDate, @NonNull Sex sex) {
        this.id = id;
        setFirstName(firstName);
        setLastName(lastName);
        setBirthDate(birthDate);
        setSex(sex);
    }

    private void setFirstName(String name) {
        Validate.notEmpty(name);
        this.firstName = name;
    }

    private void setLastName(String name) {
        Validate.notEmpty(name);
        this.lastName = name;
    }

    private void setBirthDate(LocalDate date) {
        Validate.notNull(date);
        this.birthDate = date;
    }

    private void setSex(Sex sex) {
        Validate.notNull(sex);
        this.sex = sex;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", getId())
                .append("name", getFirstName() + " " + getLastName())
                .build();
    }

    @JsonPOJOBuilder(withPrefix = StringUtils.EMPTY)
    public static final class UserBuilder {
    }
}
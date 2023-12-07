package com.bit.galleog.runtracker.repository.jooq;

import static com.bit.galleog.runtracker.domain.Sequences.USERS_SEQ;
import static com.bit.galleog.runtracker.domain.Tables.USERS;

import com.bit.galleog.runtracker.domain.User;
import com.bit.galleog.runtracker.domain.tables.records.UsersRecord;
import com.bit.galleog.runtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link UserRepository} using <a href="https://www.jooq.org/">jOOQ</a>.
 *
 * @author Oleg_Galkin
 */
@Repository
@RequiredArgsConstructor
public class JooqUserRepository implements UserRepository {
    private final DSLContext ctx;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getById(long id) {
        return ctx.selectFrom(USERS)
                .where(USERS.ID.eq(id))
                .fetchOptionalInto(UsersRecord.class)
                .map(this::toUser);
    }

    @Override
    public List<User> getAllUsers() {
        return ctx.selectFrom(USERS)
                .orderBy(USERS.LAST_NAME, USERS.FIRST_NAME)
                .fetchInto(UsersRecord.class)
                .stream()
                .map(this::toUser)
                .toList();
    }

    @Override
    @Transactional
    public User create(@NonNull User user) {
        var record = ctx.newRecord(USERS);
        record.from(user);
        record.setId(ctx.nextval(USERS_SEQ));

        record.insert();

        return toUser(record);
    }

    @Override
    @Transactional
    public Optional<User> update(long id, @NonNull User user) {
        return ctx.update(USERS)
                .set(USERS.FIRST_NAME, user.getFirstName())
                .set(USERS.LAST_NAME, user.getLastName())
                .set(USERS.BIRTH_DATE, user.getBirthDate())
                .set(USERS.SEX, user.getSex())
                .where(USERS.ID.eq(id))
                .returning()
                .fetchOptionalInto(UsersRecord.class)
                .map(this::toUser);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        var count = ctx.deleteFrom(USERS)
                .where(USERS.ID.eq(id))
                .execute();
        return count == 1;
    }

    private User toUser(UsersRecord record) {
        return User.builder()
                .id(record.getId())
                .firstName(record.getFirstName())
                .lastName(record.getLastName())
                .birthDate(record.getBirthDate())
                .sex(record.getSex())
                .build();
    }
}

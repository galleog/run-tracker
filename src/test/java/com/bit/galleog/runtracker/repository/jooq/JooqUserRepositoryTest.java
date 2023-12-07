package com.bit.galleog.runtracker.repository.jooq;

import static com.bit.galleog.runtracker.TestFixtures.BARBARA_MOORE;
import static com.bit.galleog.runtracker.TestFixtures.ELAINE_JOHNSON;
import static com.bit.galleog.runtracker.TestFixtures.JOHN_SMITH;
import static com.bit.galleog.runtracker.TestFixtures.NEW_USER;
import static com.bit.galleog.runtracker.domain.Tables.RUNS;
import static com.bit.galleog.runtracker.domain.Tables.USERS;
import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.bit.galleog.runtracker.domain.User;
import com.bit.galleog.runtracker.repository.UserRepository;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.assertj.db.api.Assertions;
import org.assertj.db.type.Table;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

/**
 * Tests for {@link JooqUserRepository}
 *
 * @author Oleg_Galkin
 */
@JooqTest
@ActiveProfiles("test")
class JooqUserRepositoryTest {
    private static final DbSetupTracker DB_SETUP_TRACKER = new DbSetupTracker();

    @Autowired
    private DataSource dataSource;
    @Autowired
    private DSLContext ctx;

    private UserRepository repository;
    private DataSourceDestination destination;
    private DataSource txDataSource;

    @BeforeEach
    void setUp() {
        repository = new JooqUserRepository(ctx);

        txDataSource = new TransactionAwareDataSourceProxy(dataSource);
        destination = DataSourceDestination.with(txDataSource);
    }

    @Nested
    class ReadTest {
        @BeforeEach
        void setUp() {
            var operation = sequenceOf(
                    deleteAllFrom(RUNS.getName(), USERS.getName()),
                    insertInto(USERS.getName())
                            .row()
                            .column(USERS.ID.getName(), JOHN_SMITH.getId())
                            .column(USERS.FIRST_NAME.getName(), JOHN_SMITH.getFirstName())
                            .column(USERS.LAST_NAME.getName(), JOHN_SMITH.getLastName())
                            .column(USERS.BIRTH_DATE.getName(), JOHN_SMITH.getBirthDate())
                            .column(USERS.SEX.getName(), JOHN_SMITH.getSex().name())
                            .end()
                            .row()
                            .column(USERS.ID.getName(), BARBARA_MOORE.getId())
                            .column(USERS.FIRST_NAME.getName(), BARBARA_MOORE.getFirstName())
                            .column(USERS.LAST_NAME.getName(), BARBARA_MOORE.getLastName())
                            .column(USERS.BIRTH_DATE.getName(), BARBARA_MOORE.getBirthDate())
                            .column(USERS.SEX.getName(), BARBARA_MOORE.getSex().name())
                            .end()
                            .row()
                            .column(USERS.ID.getName(), ELAINE_JOHNSON.getId())
                            .column(USERS.FIRST_NAME.getName(), ELAINE_JOHNSON.getFirstName())
                            .column(USERS.LAST_NAME.getName(), ELAINE_JOHNSON.getLastName())
                            .column(USERS.BIRTH_DATE.getName(), ELAINE_JOHNSON.getBirthDate())
                            .column(USERS.SEX.getName(), ELAINE_JOHNSON.getSex().name())
                            .end()
                            .build()
            );

            var dbSetup = new DbSetup(destination, operation);
            DB_SETUP_TRACKER.launchIfNecessary(dbSetup);
        }

        @Test
        void shouldGetUserById() {
            DB_SETUP_TRACKER.skipNextLaunch();

            var optional = repository.getById(BARBARA_MOORE.getId());

            assertThat(optional).isPresent();
            assertThat(optional.get()).extracting(
                    User::getId,
                    User::getFirstName,
                    User::getLastName,
                    User::getBirthDate,
                    User::getSex
            ).containsExactly(
                    BARBARA_MOORE.getId(),
                    BARBARA_MOORE.getFirstName(),
                    BARBARA_MOORE.getLastName(),
                    BARBARA_MOORE.getBirthDate(),
                    BARBARA_MOORE.getSex()
            );
        }

        @Test
        void shouldNotFindUserById() {
            DB_SETUP_TRACKER.skipNextLaunch();

            assertThat(repository.getById(4)).isEmpty();
        }

        @Test
        void shouldReturnAllUsers() {
            var users = repository.getAllUsers();

            assertThat(users).extracting(
                    User::getId,
                    User::getFirstName,
                    User::getLastName,
                    User::getBirthDate,
                    User::getSex
            ).containsExactly(
                    tuple(
                            ELAINE_JOHNSON.getId(),
                            ELAINE_JOHNSON.getFirstName(),
                            ELAINE_JOHNSON.getLastName(),
                            ELAINE_JOHNSON.getBirthDate(),
                            ELAINE_JOHNSON.getSex()
                    ),
                    tuple(
                            BARBARA_MOORE.getId(),
                            BARBARA_MOORE.getFirstName(),
                            BARBARA_MOORE.getLastName(),
                            BARBARA_MOORE.getBirthDate(),
                            BARBARA_MOORE.getSex()
                    ),
                    tuple(
                            JOHN_SMITH.getId(),
                            JOHN_SMITH.getFirstName(),
                            JOHN_SMITH.getLastName(),
                            JOHN_SMITH.getBirthDate(),
                            JOHN_SMITH.getSex()
                    )
            );
        }
    }

    @Nested
    class CreateTest {
        @BeforeEach
        void setUp() {
            var operation = sequenceOf(deleteAllFrom(RUNS.getName(), USERS.getName()));
            var dbSetup = new DbSetup(destination, operation);
            dbSetup.launch();
        }

        @Test
        void shouldCreateUser() {
            var created = repository.create(NEW_USER);

            assertThat(created.getId()).isNotNull();
            assertThat(created).extracting(
                    User::getFirstName,
                    User::getLastName,
                    User::getBirthDate,
                    User::getSex
            ).containsExactly(
                    NEW_USER.getFirstName(),
                    NEW_USER.getLastName(),
                    NEW_USER.getBirthDate(),
                    NEW_USER.getSex()
            );

            var users = new Table(txDataSource, USERS.getName());
            Assertions.assertThat(users).hasNumberOfRows(1)
                    .column(USERS.ID.getName()).value().isEqualTo(created.getId())
                    .column(USERS.FIRST_NAME.getName()).value().isEqualTo(JOHN_SMITH.getFirstName())
                    .column(USERS.LAST_NAME.getName()).value().isEqualTo(JOHN_SMITH.getLastName())
                    .column(USERS.BIRTH_DATE.getName()).value().isEqualTo(JOHN_SMITH.getBirthDate())
                    .column(USERS.SEX.getName()).value().isEqualTo(JOHN_SMITH.getSex().name());
        }
    }

    @Nested
    class UpdateAndDeleteTest {
        @BeforeEach
        void setUp() {
            var operation = sequenceOf(
                    deleteAllFrom(RUNS.getName(), USERS.getName()),
                    insertInto(USERS.getName())
                            .row()
                            .column(USERS.ID.getName(), BARBARA_MOORE.getId())
                            .column(USERS.FIRST_NAME.getName(), BARBARA_MOORE.getFirstName())
                            .column(USERS.LAST_NAME.getName(), BARBARA_MOORE.getLastName())
                            .column(USERS.BIRTH_DATE.getName(), BARBARA_MOORE.getBirthDate())
                            .column(USERS.SEX.getName(), BARBARA_MOORE.getSex().name())
                            .end()
                            .build()
            );

            var dbSetup = new DbSetup(destination, operation);
            dbSetup.launch();
        }

        @Test
        void shouldUpdateUser() {
            var optional = repository.update(BARBARA_MOORE.getId(), ELAINE_JOHNSON);

            assertThat(optional).isPresent();
            assertThat(optional.get()).extracting(
                    User::getId,
                    User::getFirstName,
                    User::getLastName,
                    User::getBirthDate,
                    User::getSex
            ).containsExactly(
                    BARBARA_MOORE.getId(),
                    ELAINE_JOHNSON.getFirstName(),
                    ELAINE_JOHNSON.getLastName(),
                    ELAINE_JOHNSON.getBirthDate(),
                    ELAINE_JOHNSON.getSex()
            );

            var users = new Table(txDataSource, USERS.getName());
            Assertions.assertThat(users).hasNumberOfRows(1)
                    .column(USERS.ID.getName()).value().isEqualTo(BARBARA_MOORE.getId())
                    .column(USERS.FIRST_NAME.getName())
                        .value().isEqualTo(ELAINE_JOHNSON.getFirstName())
                    .column(USERS.LAST_NAME.getName())
                        .value().isEqualTo(ELAINE_JOHNSON.getLastName())
                    .column(USERS.BIRTH_DATE.getName())
                        .value().isEqualTo(ELAINE_JOHNSON.getBirthDate())
                    .column(USERS.SEX.getName()).value().isEqualTo(ELAINE_JOHNSON.getSex().name());
        }

        @Test
        void shouldNotUpdateUserThatDoesNotExists() {
            assertThat(repository.update(JOHN_SMITH.getId(), JOHN_SMITH)).isEmpty();

            var users = new Table(txDataSource, USERS.getName());
            Assertions.assertThat(users).hasNumberOfRows(1)
                    .column(USERS.ID.getName()).value().isEqualTo(BARBARA_MOORE.getId())
                    .column(USERS.FIRST_NAME.getName())
                        .value().isEqualTo(BARBARA_MOORE.getFirstName())
                    .column(USERS.LAST_NAME.getName())
                        .value().isEqualTo(BARBARA_MOORE.getLastName())
                    .column(USERS.BIRTH_DATE.getName())
                        .value().isEqualTo(BARBARA_MOORE.getBirthDate())
                    .column(USERS.SEX.getName()).value().isEqualTo(BARBARA_MOORE.getSex().name());
        }

        @Test
        void shouldDeleteUser() {
            assertThat(repository.delete(BARBARA_MOORE.getId())).isTrue();

            var users = new Table(txDataSource, USERS.getName());
            Assertions.assertThat(users).isEmpty();
        }

        @Test
        void shouldNotDeleteUserThatNotExists() {
            assertThat(repository.delete(JOHN_SMITH.getId())).isFalse();

            var users = new Table(txDataSource, USERS.getName());
            Assertions.assertThat(users).hasNumberOfRows(1);
        }
    }
}
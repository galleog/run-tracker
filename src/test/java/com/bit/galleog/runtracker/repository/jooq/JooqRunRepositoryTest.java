package com.bit.galleog.runtracker.repository.jooq;

import static com.bit.galleog.runtracker.TestFixtures.BARBARA_MOORE;
import static com.bit.galleog.runtracker.TestFixtures.DISTANCE;
import static com.bit.galleog.runtracker.TestFixtures.ELAINE_JOHNSON;
import static com.bit.galleog.runtracker.TestFixtures.FINISHED_RUN_BARBARA_MOORE_1;
import static com.bit.galleog.runtracker.TestFixtures.FINISHED_RUN_JOHN_SMITH_1;
import static com.bit.galleog.runtracker.TestFixtures.FINISHED_RUN_JOHN_SMITH_2;
import static com.bit.galleog.runtracker.TestFixtures.FINISHED_RUN_JOHN_SMITH_3;
import static com.bit.galleog.runtracker.TestFixtures.FINISH_POINT_1;
import static com.bit.galleog.runtracker.TestFixtures.JOHN_SMITH;
import static com.bit.galleog.runtracker.TestFixtures.STARTED_RUN_JOHN_SMITH_1;
import static com.bit.galleog.runtracker.TestFixtures.START_POINT_1;
import static com.bit.galleog.runtracker.domain.Tables.RUNS;
import static com.bit.galleog.runtracker.domain.Tables.USERS;
import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.groups.Tuple.tuple;

import com.bit.galleog.runtracker.domain.Run;
import com.bit.galleog.runtracker.domain.RunStats;
import com.bit.galleog.runtracker.repository.RunRepository;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.assertj.core.data.Offset;
import org.assertj.db.api.Assertions;
import org.assertj.db.type.Table;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;

/**
 * Tests for {@link JooqRunRepository}.
 *
 * @author Oleg_Galkin
 */
@JooqTest
@ActiveProfiles("test")
class JooqRunRepositoryTest {
    private static final DbSetupTracker DB_SETUP_TRACKER = new DbSetupTracker();

    @Autowired
    private DataSource dataSource;
    @Autowired
    private DSLContext ctx;
    @Autowired
    private TransactionTemplate txTemplate;

    private RunRepository repository;
    private DataSourceDestination destination;
    private DataSource txDataSource;

    @BeforeEach
    void setUp() {
        repository = new JooqRunRepository(ctx);

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
                            .build(),
                    insertInto(RUNS.getName())
                            .row()
                            .column(RUNS.ID.getName(), STARTED_RUN_JOHN_SMITH_1.getId())
                            .column(RUNS.USER_ID.getName(), STARTED_RUN_JOHN_SMITH_1.getUserId())
                            .column(
                                    RUNS.START_DATETIME.getName(),
                                    STARTED_RUN_JOHN_SMITH_1.getStartPoint().getDatetime()
                            ).column(
                                    RUNS.START_LATITUDE.getName(),
                                    STARTED_RUN_JOHN_SMITH_1.getStartPoint().getLatitude()
                            ).column(
                                    RUNS.START_LONGITUDE.getName(),
                                    STARTED_RUN_JOHN_SMITH_1.getStartPoint().getLongitude()
                            ).column(RUNS.FINISH_DATETIME.getName(), null)
                            .column(RUNS.FINISH_LATITUDE.getName(), null)
                            .column(RUNS.FINISH_LONGITUDE.getName(), null)
                            .column(RUNS.DISTANCE.getName(), null)
                            .end()
                            .row()
                            .column(RUNS.ID.getName(), FINISHED_RUN_JOHN_SMITH_1.getId())
                            .column(RUNS.USER_ID.getName(), FINISHED_RUN_JOHN_SMITH_1.getUserId())
                            .column(
                                    RUNS.START_DATETIME.getName(),
                                    FINISHED_RUN_JOHN_SMITH_1.getStartPoint().getDatetime()
                            ).column(
                                    RUNS.START_LATITUDE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_1.getStartPoint().getLatitude()
                            ).column(
                                    RUNS.START_LONGITUDE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_1.getStartPoint().getLongitude()
                            ).column(
                                    RUNS.FINISH_DATETIME.getName(),
                                    FINISHED_RUN_JOHN_SMITH_1.getFinishPoint().getDatetime()
                            ).column(
                                    RUNS.FINISH_LATITUDE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_1.getFinishPoint().getLatitude()
                            ).column(
                                    RUNS.FINISH_LONGITUDE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_1.getFinishPoint().getLongitude()
                            ).column(
                                    RUNS.DISTANCE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_1.getDistance()
                            ).end()
                            .row()
                            .column(RUNS.ID.getName(), FINISHED_RUN_JOHN_SMITH_2.getId())
                            .column(RUNS.USER_ID.getName(), FINISHED_RUN_JOHN_SMITH_2.getUserId())
                            .column(
                                    RUNS.START_DATETIME.getName(),
                                    FINISHED_RUN_JOHN_SMITH_2.getStartPoint().getDatetime()
                            ).column(
                                    RUNS.START_LATITUDE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_2.getStartPoint().getLatitude()
                            ).column(
                                    RUNS.START_LONGITUDE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_2.getStartPoint().getLongitude()
                            ).column(
                                    RUNS.FINISH_DATETIME.getName(),
                                    FINISHED_RUN_JOHN_SMITH_2.getFinishPoint().getDatetime()
                            ).column(
                                    RUNS.FINISH_LATITUDE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_2.getFinishPoint().getLatitude()
                            ).column(
                                    RUNS.FINISH_LONGITUDE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_2.getFinishPoint().getLongitude()
                            ).column(
                                    RUNS.DISTANCE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_2.getDistance()
                            ).end()
                            .row()
                            .column(RUNS.ID.getName(), FINISHED_RUN_JOHN_SMITH_3.getId())
                            .column(RUNS.USER_ID.getName(), FINISHED_RUN_JOHN_SMITH_3.getUserId())
                            .column(
                                    RUNS.START_DATETIME.getName(),
                                    FINISHED_RUN_JOHN_SMITH_3.getStartPoint().getDatetime()
                            ).column(
                                    RUNS.START_LATITUDE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_3.getStartPoint().getLatitude()
                            ).column(
                                    RUNS.START_LONGITUDE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_3.getStartPoint().getLongitude()
                            ).column(
                                    RUNS.FINISH_DATETIME.getName(),
                                    FINISHED_RUN_JOHN_SMITH_3.getFinishPoint().getDatetime()
                            ).column(
                                    RUNS.FINISH_LATITUDE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_3.getFinishPoint().getLatitude()
                            ).column(
                                    RUNS.FINISH_LONGITUDE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_3.getFinishPoint().getLongitude()
                            ).column(
                                    RUNS.DISTANCE.getName(),
                                    FINISHED_RUN_JOHN_SMITH_3.getDistance()
                            ).end()
                            .row()
                            .column(RUNS.ID.getName(), FINISHED_RUN_BARBARA_MOORE_1.getId())
                            .column(
                                    RUNS.USER_ID.getName(),
                                    FINISHED_RUN_BARBARA_MOORE_1.getUserId()
                            ).column(
                                    RUNS.START_DATETIME.getName(),
                                    FINISHED_RUN_BARBARA_MOORE_1.getStartPoint().getDatetime()
                            ).column(
                                    RUNS.START_LATITUDE.getName(),
                                    FINISHED_RUN_BARBARA_MOORE_1.getStartPoint().getLatitude()
                            ).column(
                                    RUNS.START_LONGITUDE.getName(),
                                    FINISHED_RUN_BARBARA_MOORE_1.getStartPoint().getLongitude()
                            ).column(
                                    RUNS.FINISH_DATETIME.getName(),
                                    FINISHED_RUN_BARBARA_MOORE_1.getFinishPoint().getDatetime()
                            ).column(
                                    RUNS.FINISH_LATITUDE.getName(),
                                    FINISHED_RUN_BARBARA_MOORE_1.getFinishPoint().getLatitude()
                            ).column(
                                    RUNS.FINISH_LONGITUDE.getName(),
                                    FINISHED_RUN_BARBARA_MOORE_1.getFinishPoint().getLongitude()
                            ).column(
                                    RUNS.DISTANCE.getName(),
                                    FINISHED_RUN_BARBARA_MOORE_1.getDistance()
                            ).end()
                            .build()
            );

            var dbSetup = new DbSetup(destination, operation);
            DB_SETUP_TRACKER.launchIfNecessary(dbSetup);
        }

        @Test
        void shouldGetRunById() {
            DB_SETUP_TRACKER.skipNextLaunch();

            var optional = repository.getByIdForUpdate(STARTED_RUN_JOHN_SMITH_1.getId());

            assertThat(optional).isPresent();
            assertThat(optional.get()).extracting(
                    Run::getId,
                    Run::getUserId,
                    Run::getStartPoint,
                    Run::getFinishPoint,
                    Run::getDistance
            ).containsExactly(
                    STARTED_RUN_JOHN_SMITH_1.getId(),
                    STARTED_RUN_JOHN_SMITH_1.getUserId(),
                    STARTED_RUN_JOHN_SMITH_1.getStartPoint(),
                    null,
                    null
            );
        }

        @Test
        void shouldLockRun() {
            DB_SETUP_TRACKER.skipNextLaunch();

            assertThat(repository.getByIdForUpdate(STARTED_RUN_JOHN_SMITH_1.getId())).isPresent();

            // record is locked, the second transaction should fail
            txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            txTemplate.setTimeout(1);

            assertThatExceptionOfType(DataAccessException.class).isThrownBy(() ->
                    txTemplate.execute(status ->
                            repository.getByIdForUpdate(STARTED_RUN_JOHN_SMITH_1.getId())
                    )
            );
        }

        @Test
        void shouldNotFindRunById() {
            DB_SETUP_TRACKER.skipNextLaunch();

            assertThat(repository.getByIdForUpdate(23)).isEmpty();
        }

        @Test
        void shouldGetRunsByUser() {
            DB_SETUP_TRACKER.skipNextLaunch();

            var fromDatetime = LocalDateTime.of(2023, 9, 1, 0, 0);
            var toDatetime = LocalDateTime.of(2023, 9, 30, 23, 59, 59);
            var runs = repository.getRunsByUser(JOHN_SMITH.getId(), fromDatetime, toDatetime);

            assertThat(runs).hasSize(1);
            assertThat(runs).extracting(
                    Run::getId,
                    Run::getUserId,
                    Run::getStartPoint,
                    Run::getFinishPoint,
                    Run::getDistance
            ).containsExactly(
                    tuple(
                            FINISHED_RUN_JOHN_SMITH_1.getId(),
                            FINISHED_RUN_JOHN_SMITH_1.getUserId(),
                            FINISHED_RUN_JOHN_SMITH_1.getStartPoint(),
                            FINISHED_RUN_JOHN_SMITH_1.getFinishPoint(),
                            FINISHED_RUN_JOHN_SMITH_1.getDistance()
                    )
            );

            fromDatetime = LocalDateTime.of(2023, 11, 1, 0, 0);
            runs = repository.getRunsByUser(JOHN_SMITH.getId(), fromDatetime, null);

            assertThat(runs).hasSize(2);
            assertThat(runs).extracting(
                    Run::getId,
                    Run::getUserId,
                    Run::getStartPoint,
                    Run::getFinishPoint,
                    Run::getDistance
            ).containsExactly(
                    tuple(
                            FINISHED_RUN_JOHN_SMITH_2.getId(),
                            FINISHED_RUN_JOHN_SMITH_2.getUserId(),
                            FINISHED_RUN_JOHN_SMITH_2.getStartPoint(),
                            FINISHED_RUN_JOHN_SMITH_2.getFinishPoint(),
                            FINISHED_RUN_JOHN_SMITH_2.getDistance()
                    ),
                    tuple(
                            FINISHED_RUN_JOHN_SMITH_3.getId(),
                            FINISHED_RUN_JOHN_SMITH_3.getUserId(),
                            FINISHED_RUN_JOHN_SMITH_3.getStartPoint(),
                            FINISHED_RUN_JOHN_SMITH_3.getFinishPoint(),
                            FINISHED_RUN_JOHN_SMITH_3.getDistance()
                    )
            );

            toDatetime = LocalDateTime.of(2023, 11, 1, 0, 0);
            runs = repository.getRunsByUser(JOHN_SMITH.getId(), null, toDatetime);

            assertThat(runs).hasSize(1);
            assertThat(runs).extracting(
                    Run::getId,
                    Run::getUserId,
                    Run::getStartPoint,
                    Run::getFinishPoint,
                    Run::getDistance
            ).containsExactly(
                    tuple(
                            FINISHED_RUN_JOHN_SMITH_1.getId(),
                            FINISHED_RUN_JOHN_SMITH_1.getUserId(),
                            FINISHED_RUN_JOHN_SMITH_1.getStartPoint(),
                            FINISHED_RUN_JOHN_SMITH_1.getFinishPoint(),
                            FINISHED_RUN_JOHN_SMITH_1.getDistance()
                    )
            );

            runs = repository.getRunsByUser(JOHN_SMITH.getId(), null, null);

            assertThat(runs).hasSize(3);
            assertThat(runs).extracting(
                    Run::getId,
                    Run::getUserId,
                    Run::getStartPoint,
                    Run::getFinishPoint,
                    Run::getDistance
            ).containsExactly(
                    tuple(
                            FINISHED_RUN_JOHN_SMITH_1.getId(),
                            FINISHED_RUN_JOHN_SMITH_1.getUserId(),
                            FINISHED_RUN_JOHN_SMITH_1.getStartPoint(),
                            FINISHED_RUN_JOHN_SMITH_1.getFinishPoint(),
                            FINISHED_RUN_JOHN_SMITH_1.getDistance()
                    ),
                    tuple(
                            FINISHED_RUN_JOHN_SMITH_2.getId(),
                            FINISHED_RUN_JOHN_SMITH_2.getUserId(),
                            FINISHED_RUN_JOHN_SMITH_2.getStartPoint(),
                            FINISHED_RUN_JOHN_SMITH_2.getFinishPoint(),
                            FINISHED_RUN_JOHN_SMITH_2.getDistance()
                    ),
                    tuple(
                            FINISHED_RUN_JOHN_SMITH_3.getId(),
                            FINISHED_RUN_JOHN_SMITH_3.getUserId(),
                            FINISHED_RUN_JOHN_SMITH_3.getStartPoint(),
                            FINISHED_RUN_JOHN_SMITH_3.getFinishPoint(),
                            FINISHED_RUN_JOHN_SMITH_3.getDistance()
                    )
            );
        }

        @Test
        void shouldGetStats() {
            DB_SETUP_TRACKER.skipNextLaunch();

            var fromDatetime = LocalDateTime.of(2023, 9, 1, 0, 0);
            var toDatetime = LocalDateTime.of(2023, 9, 30, 23, 59, 59);
            var stats = repository.getStatsByUser(JOHN_SMITH.getId(), fromDatetime, toDatetime);

            var count = 1;
            var distance = FINISHED_RUN_JOHN_SMITH_1.getDistance();
            var avgSpeed = FINISHED_RUN_JOHN_SMITH_1.getAvgSpeed();

            assertThat(stats).extracting(
                    RunStats::getCount, RunStats::getDistance, RunStats::getAvgSpeed
            ).containsExactly(
                    count, distance, avgSpeed
            );

            fromDatetime = LocalDateTime.of(2023, 11, 1, 0, 0);
            stats = repository.getStatsByUser(JOHN_SMITH.getId(), fromDatetime, null);

            count = 2;
            distance = FINISHED_RUN_JOHN_SMITH_2.getDistance() +
                    FINISHED_RUN_JOHN_SMITH_3.getDistance();
            avgSpeed = (FINISHED_RUN_JOHN_SMITH_2.getAvgSpeed() +
                    FINISHED_RUN_JOHN_SMITH_3.getAvgSpeed()) / count;

            assertThat(stats).extracting(RunStats::getCount, RunStats::getDistance)
                    .containsExactly(count, distance);
            assertThat(stats.getAvgSpeed()).isCloseTo(avgSpeed, Offset.offset(0.001));

            toDatetime = LocalDateTime.of(2023, 11, 1, 0, 0);
            stats = repository.getStatsByUser(JOHN_SMITH.getId(), null, toDatetime);

            count = 1;
            distance = FINISHED_RUN_JOHN_SMITH_1.getDistance();
            avgSpeed = FINISHED_RUN_JOHN_SMITH_1.getAvgSpeed();

            assertThat(stats).extracting(
                    RunStats::getCount, RunStats::getDistance, RunStats::getAvgSpeed
            ).containsExactly(
                    count, distance, avgSpeed
            );

            stats = repository.getStatsByUser(JOHN_SMITH.getId(), null, null);

            count = 3;
            distance = FINISHED_RUN_JOHN_SMITH_1.getDistance() +
                    FINISHED_RUN_JOHN_SMITH_2.getDistance() +
                    FINISHED_RUN_JOHN_SMITH_3.getDistance();
            avgSpeed = (FINISHED_RUN_JOHN_SMITH_1.getAvgSpeed() +
                    FINISHED_RUN_JOHN_SMITH_2.getAvgSpeed() +
                    FINISHED_RUN_JOHN_SMITH_3.getAvgSpeed()) / count;

            assertThat(stats).extracting(RunStats::getCount, RunStats::getDistance)
                    .containsExactly(count, distance);
            assertThat(stats.getAvgSpeed()).isCloseTo(avgSpeed, Offset.offset(0.001));

            stats = repository.getStatsByUser(ELAINE_JOHNSON.getId(), null, null);

            assertThat(stats).extracting(
                    RunStats::getCount, RunStats::getDistance, RunStats::getAvgSpeed
            ).containsExactly(0, 0, 0.0D);
        }
    }

    @Nested
    class StartRunTest {
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
                            .build()
            );

            var dbSetup = new DbSetup(destination, operation);
            dbSetup.launch();
        }

        @Test
        void shouldStartRun() {
            var created = repository.startRun(JOHN_SMITH.getId(), START_POINT_1);

            assertThat(created.getId()).isNotZero();
            assertThat(created).extracting(
                    Run::getUserId, Run::getStartPoint, Run::getFinishPoint, Run::getDistance
            ).containsExactly(
                    JOHN_SMITH.getId(), START_POINT_1, null, null
            );

            var runs = new Table(txDataSource, RUNS.getName());
            Assertions.assertThat(runs).hasNumberOfRows(1)
                    .column(RUNS.ID.getName()).value().isEqualTo(created.getId())
                    .column(RUNS.USER_ID.getName()).value().isEqualTo(JOHN_SMITH.getId())
                    .column(RUNS.START_DATETIME.getName())
                    .value().isEqualTo(START_POINT_1.getDatetime())
                    .column(RUNS.START_LATITUDE.getName())
                    .value().isEqualTo(START_POINT_1.getLatitude())
                    .column(RUNS.START_LONGITUDE.getName())
                    .value().isEqualTo(START_POINT_1.getLongitude())
                    .column(RUNS.FINISH_DATETIME.getName()).value().isNull()
                    .column(RUNS.FINISH_LATITUDE.getName()).value().isNull()
                    .column(RUNS.FINISH_LONGITUDE.getName()).value().isNull()
                    .column(RUNS.DISTANCE.getName()).value().isNull();
        }
    }

    @Nested
    class FinishRunTest {
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
                            .build(),
                    insertInto(RUNS.getName())
                            .row()
                            .column(RUNS.ID.getName(), STARTED_RUN_JOHN_SMITH_1.getId())
                            .column(RUNS.USER_ID.getName(), STARTED_RUN_JOHN_SMITH_1.getUserId())
                            .column(
                                    RUNS.START_DATETIME.getName(),
                                    STARTED_RUN_JOHN_SMITH_1.getStartPoint().getDatetime()
                            ).column(
                                    RUNS.START_LATITUDE.getName(),
                                    STARTED_RUN_JOHN_SMITH_1.getStartPoint().getLatitude()
                            ).column(
                                    RUNS.START_LONGITUDE.getName(),
                                    STARTED_RUN_JOHN_SMITH_1.getStartPoint().getLongitude()
                            ).end()
                            .build()
            );

            var dbSetup = new DbSetup(destination, operation);
            DB_SETUP_TRACKER.launchIfNecessary(dbSetup);
        }

        @Test
        void shouldFinishRun() {
            DB_SETUP_TRACKER.skipNextLaunch();

            var optional = repository.finishRun(STARTED_RUN_JOHN_SMITH_1.getId(),
                    FINISH_POINT_1, DISTANCE);

            assertThat(optional).isPresent();
            assertThat(optional.get()).extracting(
                    Run::getId,
                    Run::getUserId,
                    Run::getStartPoint,
                    Run::getFinishPoint,
                    Run::getDistance
            ).containsExactly(
                    STARTED_RUN_JOHN_SMITH_1.getId(),
                    STARTED_RUN_JOHN_SMITH_1.getUserId(),
                    STARTED_RUN_JOHN_SMITH_1.getStartPoint(),
                    FINISH_POINT_1,
                    DISTANCE
            );

            var runs = new Table(txDataSource, RUNS.getName());
            Assertions.assertThat(runs).hasNumberOfRows(1)
                    .column(RUNS.ID.getName())
                    .value().isEqualTo(STARTED_RUN_JOHN_SMITH_1.getId())
                    .column(RUNS.USER_ID.getName())
                    .value().isEqualTo(STARTED_RUN_JOHN_SMITH_1.getUserId())
                    .column(RUNS.START_DATETIME.getName())
                    .value().isEqualTo(STARTED_RUN_JOHN_SMITH_1.getStartPoint().getDatetime())
                    .column(RUNS.START_LATITUDE.getName())
                    .value().isEqualTo(STARTED_RUN_JOHN_SMITH_1.getStartPoint().getLatitude())
                    .column(RUNS.START_LONGITUDE.getName())
                    .value().isEqualTo(STARTED_RUN_JOHN_SMITH_1.getStartPoint().getLongitude())
                    .column(RUNS.FINISH_DATETIME.getName())
                    .value().isEqualTo(FINISH_POINT_1.getDatetime())
                    .column(RUNS.FINISH_LATITUDE.getName())
                    .value().isEqualTo(FINISH_POINT_1.getLatitude())
                    .column(RUNS.FINISH_LONGITUDE.getName())
                    .value().isEqualTo(FINISH_POINT_1.getLongitude())
                    .column(RUNS.DISTANCE.getName()).value().isEqualTo(DISTANCE);
        }

        @Test
        void shouldNotFinishRunIfItDoesNotExist() {
            DB_SETUP_TRACKER.skipNextLaunch();

            assertThat(repository.finishRun(7, FINISH_POINT_1, DISTANCE)).isEmpty();
        }
    }
}
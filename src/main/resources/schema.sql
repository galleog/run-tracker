create table if not exists users
(
    id         bigint primary key,
    first_name varchar(100) not null,
    last_name  varchar(100) not null,
    birth_date date         not null,
    sex        varchar(9)   not null
);

create index if not exists idx_last_first_names on users (last_name, first_name);

create sequence if not exists users_seq;

create table if not exists runs
(
    id               bigint primary key,
    user_id          bigint        not null,
    start_datetime   timestamp     not null,
    start_latitude   decimal(8, 6) not null,
    start_longitude  decimal(9, 6) not null,
    finish_datetime  timestamp,
    finish_latitude  decimal(8, 6),
    finish_longitude decimal(9, 6),
    distance         integer,
    foreign key (user_id) references users (id)
);

create index if not exists idx_runs_user_id_finish_datetime on runs (user_id, finish_datetime);

create index if not exists idx_runs_start_datetime on runs (start_datetime);

create sequence if not exists runs_seq;
drop table if exists statistics;

create table if not exists statistics
(
    id        bigint generated by default as identity
        constraint statistics_pk
            primary key,
    app       varchar   not null,
    uri       varchar   not null,
    ip        varchar   not null,
    timestamp timestamp not null
);
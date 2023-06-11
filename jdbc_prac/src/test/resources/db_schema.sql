drop table if exists users;


create table users (
    userId      varchar(12)     not null,
    password    varbinary(64)   not null,
    name        varchar(20)     not null,
    email       varchar(255),

    primary key (userId)
);
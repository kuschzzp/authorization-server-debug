drop table if exists oauth_test_user;
create table oauth_test_user
(
    username    varchar(255) not null primary key,
    password    varchar(255) not null,
    auth_codes  varchar(1000)
);
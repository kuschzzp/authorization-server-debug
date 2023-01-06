drop table if exists oauth_authorization;
CREATE TABLE oauth_authorization
(
    id                            varchar(255) NOT NULL,
    registered_client_id          varchar(255) NOT NULL,
    principal_name                varchar(255) NOT NULL,
    authorization_grant_type      varchar(255) NOT NULL,
    -- 可选授权范围 的单词，一般不会太多
    authorized_scopes             varchar(512) null,
    attributes                    TEXT          DEFAULT NULL,
    state                         varchar(500)  DEFAULT NULL,
    -- 不放东西 固定128
    authorization_code_value      varchar(255)  DEFAULT NULL,
    authorization_code_issued_at  timestamp     DEFAULT NULL,
    authorization_code_expires_at timestamp     DEFAULT NULL,
    -- 不放东西基本不会超过 100
    authorization_code_metadata   varchar(255)  DEFAULT NULL,
    -- 放置约 30 个属性 约两千多
    access_token_value            varchar(3000) DEFAULT NULL,
    access_token_issued_at        timestamp     DEFAULT NULL,
    access_token_expires_at       timestamp     DEFAULT NULL,
    access_token_metadata         varchar(2000) DEFAULT NULL,
    access_token_type             varchar(255)  DEFAULT NULL,
    access_token_scopes           varchar(512)  DEFAULT NULL,
    -- 不放东西 固定128
    refresh_token_value           varchar(255)  DEFAULT NULL,
    refresh_token_issued_at       timestamp     DEFAULT NULL,
    refresh_token_expires_at      timestamp     DEFAULT NULL,
    -- 不放东西基本不会超过 100
    refresh_token_metadata        varchar(255)  DEFAULT NULL,
    -- 放置20个属性 约两千多
    oidc_id_token_value           varchar(3000) DEFAULT NULL,
    oidc_id_token_issued_at       timestamp     DEFAULT NULL,
    oidc_id_token_expires_at      timestamp     DEFAULT NULL,
    -- 下面这俩，放置20个属性，长度在 一千两三百左右
    oidc_id_token_metadata        varchar(2000) DEFAULT NULL,
    oidc_id_token_claims          varchar(2000) DEFAULT NULL,
    PRIMARY KEY (id)
);

drop table if exists oauth_authorization_consent;
CREATE TABLE oauth_authorization_consent
(
    registered_client_id varchar(255)  NOT NULL,
    principal_name       varchar(255)  NOT NULL,
    authorities          varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

drop table if exists oauth_client;
CREATE TABLE oauth_client
(
    id                            varchar(255)                            NOT NULL,
    client_id                     varchar(255)                            NOT NULL,
    client_id_issued_at           timestamp     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret                 varchar(255)  DEFAULT NULL,
    client_secret_expires_at      timestamp     DEFAULT NULL,
    client_name                   varchar(255)                            NOT NULL,
    client_authentication_methods varchar(1000)                           NOT NULL,
    authorization_grant_types     varchar(1000)                           NOT NULL,
    redirect_uris                 varchar(1000) DEFAULT NULL,
    scopes                        varchar(1000)                           NOT NULL,
    client_settings               varchar(2000)                           NOT NULL,
    token_settings                varchar(2000)                           NOT NULL,
    PRIMARY KEY (id)
);
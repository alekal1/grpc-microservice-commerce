CREATE TABLE client.client
(
    id                              bigserial   NOT NULL,
    name                            text        NOT NULL,
    identifier_code                 text        NOT NULL,
    CONSTRAINT message_pkey PRIMARY KEY (id)
);

ALTER TABLE client.client
    OWNER TO client;

GRANT ALL PRIVILEGES ON TABLE client.client to client_adm_role;
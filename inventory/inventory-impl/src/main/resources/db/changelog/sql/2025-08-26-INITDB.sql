CREATE TABLE inventory.unit
(
    id                  bigserial   NOT NULL,
    name                text        NOT NULL,
    type                text        NOT NULL,
    price               numeric     NOT NULL,
    quantity            numeric     NOT NULL,
    CONSTRAINT message_pkey PRIMARY KEY (id)
);

ALTER TABLE inventory.unit
    OWNER TO inventory;

GRANT ALL PRIVILEGES ON TABLE inventory.unit to inventory_adm_role;
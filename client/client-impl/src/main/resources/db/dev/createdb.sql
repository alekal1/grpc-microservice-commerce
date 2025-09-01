CREATE SCHEMA client;
CREATE SCHEMA liquidbase;

CREATE ROLE client_adm_role;
GRANT client_adm_role TO client;

GRANT ALL PRIVILEGES ON SCHEMA client TO client_adm_role;
GRANT ALL PRIVILEGES ON SCHEMA liquidbase TO client_adm_role;
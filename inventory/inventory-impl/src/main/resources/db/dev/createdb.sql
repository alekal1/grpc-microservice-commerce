CREATE SCHEMA inventory;
CREATE SCHEMA liquidbase;

CREATE ROLE inventory_adm_role;
GRANT inventory_adm_role TO inventory;

GRANT ALL PRIVILEGES ON SCHEMA inventory TO inventory_adm_role;
GRANT ALL PRIVILEGES ON SCHEMA liquidbase TO inventory_adm_role;
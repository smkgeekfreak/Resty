# Add Account
# --- !Ups
DROP SEQUENCE IF EXISTS account_row_id_seq;
CREATE SEQUENCE account_row_id_seq CYCLE;

-- place unique constraint on account accountname
CREATE TABLE account
(
  uid bigint NOT NULL DEFAULT nextval('account_row_id_seq'::regclass) PRIMARY KEY,
  email character varying(255),
  status integer,
  created TIMESTAMP WITHOUT TIME ZONE DEFAULT statement_timestamp(),
  modified TIMESTAMP WITHOUT TIME ZONE DEFAULT statement_timestamp()
);

-- # --- !Downs
-- DROP SEQUENCE IF EXISTS account_row_id_seq;
-- DROP TABLE IF EXISTS account;

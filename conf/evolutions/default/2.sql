# Account Stored Procedures
# --- !Ups
-- Function: create_account
CREATE OR REPLACE FUNCTION create_account(_email varchar, _status integer)
RETURNS TABLE(
	ret_uid bigint,
	ret_email varchar,
	ret_status integer,
	ret_created TIMESTAMP WITHOUT TIME ZONE,
	ret_modified TIMESTAMP WITHOUT TIME ZONE
	)
as $func$

	BEGIN
		--
	RETURN QUERY
		INSERT INTO account(email, status)
		VALUES (_email, _status)
		RETURNING
			uid,
			email,
			status,
			created,
			modified;;
	END;;
$func$ language 'plpgsql';
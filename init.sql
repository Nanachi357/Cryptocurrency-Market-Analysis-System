DO
$$
    BEGIN
        IF EXISTS (
            SELECT
            FROM   pg_catalog.pg_roles
            WHERE  rolname = 'myuser') THEN

            ALTER ROLE myuser WITH PASSWORD 'mypassword';
        ELSE
            CREATE ROLE myuser LOGIN PASSWORD 'mypassword';
        END IF;
    END
$$;

DO
$$
    BEGIN
        IF NOT EXISTS (
            SELECT
            FROM   pg_catalog.pg_database
            WHERE  datname = 'mydatabase') THEN

            CREATE DATABASE mydatabase OWNER myuser;
        END IF;
    END
$$;

GRANT ALL PRIVILEGES ON DATABASE mydatabase TO myuser;
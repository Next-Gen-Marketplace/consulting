ALTER TABLE requests
    DROP COLUMN title;

ALTER TABLE requests
    ADD COLUMN product TEXT,
    ADD COLUMN full_name TEXT,
    ADD COLUMN phone TEXT;
ALTER TYPE user_role RENAME TO user_role_old;

CREATE TYPE user_role AS ENUM ('CLIENT', 'CONSULTANT', 'ADMIN');

ALTER TABLE users ALTER COLUMN role DROP DEFAULT;

ALTER TABLE users
ALTER COLUMN role TYPE user_role
  USING role::text::user_role;

DROP TYPE user_role_old;
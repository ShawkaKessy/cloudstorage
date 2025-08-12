TRUNCATE TABLE users RESTART IDENTITY CASCADE;

INSERT INTO users (login, password, auth_token)
VALUES ('user', '$2a$10$7Qj6KqULvY0zZKsmvV/fOeR6cT0jFbslHbJYqk0h6z2bW7lE3kH9y', NULL);

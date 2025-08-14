INSERT INTO users (login, password, auth_token)
SELECT 'user', '$2a$10$7Qj6KqULvY0zZKsmvV/fOeR6cT0jFbslHbJYqk0h6z2bW7lE3kH9y', NULL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE login='user');
-- пароль: password

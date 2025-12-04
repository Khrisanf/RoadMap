-- Пользователь, с которым будем работать в UC-01
INSERT INTO users (id, login, password)
VALUES (1, 'demo_user', 'password');

-- Пара стран для выбора в анкете
INSERT INTO countries (id, name)
VALUES (1, 'Россия'),
       (2, 'Киргизия'),
       (3, 'Узбекистан');

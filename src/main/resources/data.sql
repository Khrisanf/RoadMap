-- Пользователь, с которым будем работать в UC-01
INSERT INTO users (id, login, password)
VALUES (1, 'demo_user', 'password');

-- Пара стран для выбора в анкете
INSERT INTO countries (id, name)
VALUES (1, 'Россия'),
       (2, 'Киргизия'),
       (3, 'Узбекистан');

INSERT INTO RULES
(ACTIVE, CONDITION, DESCRIPTION, NAME, PRIORITY, RELATIVE_DEADLINE, TEMPLATE_TEXT)
VALUES
    (TRUE,
     'purpose=WORK',
     'Простое правило для демонстрации дорожной карты',
     'Базовое правило патента',
     1,
     'P30D', -- например, "относительный дедлайн 30 дней" (как строка)
     'Шаблон шага для оформления патента'
    );

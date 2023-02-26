-- Заполнение БД тестовыми данными
INSERT INTO users (name, email)
VALUES ('Иван', 'ivan@ya.ru'),
       ('Сергей', 'sergey@ya.ru'),
       ('Степан', 'stepan@ya.ru'),
       ('Петр', 'petr@ya.ru'),
       ('Андрей', 'andrey@ya.ru'),
       ('Алексей', 'alexey@ya.ru'),
       ('Антон', 'anton@ya.ru'),
       ('Олег', 'oleg@ya.ru'),
       ('Вячеслав', 'vyacheslav@ya.ru'),
       ('Николай', 'nikolay@ya.ru');

INSERT INTO item_requests (description, created, creator_id)
VALUES ('Нужен перфоратор', now() - interval '10' day, 10),
       ('Ищу молоток', now() - interval '3' day, 8),
       ('Нужен хороший шуруповерт', now() - interval '6' day, 1),
       ('Ищу подругу', now() - interval '156' day, 8),
       ('Есть у кого-то отбойный молоток?', now() - interval '1' day, 3);

INSERT INTO items (name, description, is_available, owner_id, request_id)
VALUES ('Дрель', 'Привет соседям', true, 1, null),
       ('Отвертка', 'Чтобы закрутить', true, 1, null),
       ('Гаечный ключ', 'На 17-19', true, 1, null),
       ('Фонарь', 'Компактный фонарь', true, 2, null),
       ('Перфоратор', 'Соседи в шоке', true, 2, 1),
       ('Молоток', 'С гвоздодером', true, 5, 2),
       ('Ключ-трещетка', 'С набором головок', true, 6, null),
       ('Рубанок', 'Старый дедовский', true, 7, null),
       ('Болгарка', 'Без дисков', true, 9, null),
       ('Шуруповерт', 'Переносной шуруповерт с аккумулятором', true, 9, 3),
       ('Шуруповерт', 'Шуруповерт проводной мощный', false, 9, null);

INSERT INTO bookings (start_date, end_date, status, item_id, booker_id)
VALUES (now() - interval '10' day, now() - interval '5' day, 'APPROVED', 1, 3),
       (now() - interval '9' day, now() - interval '5' day, 'APPROVED', 2, 4),
       (now() + interval '2' day, now() + interval '5' day, 'WAITING', 3, 10),
       (now() - interval '4' day, now() - interval '3' day, 'REJECTED', 1, 10),
       (now() - interval '8' day, now() - interval '2' day, 'CANCELED', 1, 4),
       (now() - interval '1' day, now() + interval '5' day, 'APPROVED', 5, 3),
       (now() - interval '2' day, now() + interval '5' day, 'APPROVED', 6, 9),
       (now() + interval '2' day, now() + interval '5' day, 'WAITING', 10, 3);

INSERT INTO comments (item_id, text, author_name, created)
VALUES (1, 'Соседи вообще кайфанули!', 'Степан', now() - interval '1' day),
       (2, 'Отвертка то что надо! ручка очень удобная.', 'Петр', now() - interval '3' day);
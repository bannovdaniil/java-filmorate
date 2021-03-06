-- genres добавление значений по умолчанию
INSERT INTO `GENRES` (GENRE_ID, NAME)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');


-- ratings
-- G — у фильма нет возрастных ограничений,
--     PG — детям рекомендуется смотреть фильм с родителями,
--     PG-13 — детям до 13 лет просмотр не желателен,
--     R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
--     NC-17 — лицам до 18 лет просмотр запрещён.

INSERT INTO `RATINGS` (RATING_ID, NAME)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

-- INSERT INTO "films" (NAME, DESCRIPTION, DURATION, LIKES, RELEASE_DATE, RATING_ID)
-- VALUES ( 'Tarantino', 'Desc Film', 50, 200, '2022-01-03', 1 );

INSERT INTO `EVENT_TYPES` (EVENT_TYPE_ID, NAME)
VALUES (1, 'LIKE'),
       (2, 'REVIEW'),
       (3, 'FRIEND');

INSERT INTO `EVENT_OPERATIONS` (EVENT_OPERATION_ID, NAME)
VALUES (1, 'ADD'),
       (2, 'UPDATE'),
       (3, 'REMOVE');
# #7 Задание для взаимопроверки
В этом задании вы будете проектировать базу данных для проекта, основываясь на уже существующей функциональности. Вносить какие-либо изменения в код не потребуется.

![This is an image](src/main/resources/filmorate-2.png)

# #4 Полный REST

- Убедитесь, что ваше приложение возвращает корректные HTTP-коды
    - 400 — если ошибка валидации: ValidationException;
    - 404 — для всех ситуаций, если искомый объект не найден;
    - 500 — если возникло исключение.

# #3 Новая логика

Обеспечим возможность пользователям добавлять друг друга в друзья и ставить фильмам лайки.

- Создайте UserService, который будет отвечать за такие операции с пользователями, как
    - добавление в друзья,
    - удаление из друзей,
    - вывод списка общих друзей.
- Создайте FilmService, который будет отвечать за операции с фильмами,
    - добавление и удаление лайка,
    - вывод 10 наиболее популярных фильмов по количеству лайков.
    - каждый пользователь может поставить лайк фильму только один раз.
- Добавьте к ним аннотацию @Service — тогда к ним можно будет получить доступ из контроллера.

# #2 Архитектура

- Вынесите хранение данных о фильмах и пользователях в отдельные классы.
- Назовём их «хранилищами» (англ. storage) — так будет сразу понятно, что они делают.
- Создайте интерфейсы **FilmStorage** и **UserStorage**, в которых
  будут определены методы: _**добавления, удаления и модификации объектов**_.
- Создайте классы InMemoryFilmStorage и InMemoryUserStorage, имплементирующие новые интерфейсы, и перенесите туда всю
  логику хранения, обновления и поиска объектов.
- Добавьте к InMemoryFilmStorage и InMemoryUserStorage аннотацию @Component, чтобы впоследствии пользоваться внедрением
  зависимостей и передавать хранилища сервисам.

- Чтобы объединить хранилища, создайте новый пакет storage. В нём будут только классы и интерфейсы, имеющие отношение к
  хранению данных.

# #1 Наводим порядок в репозитории

- Создайте новую ветку, которая будет называться **add-friends-likes**

# нужны отзывы пользователей.

А для улучшения рекомендаций по просмотру хорошо бы объединить пользователей в комьюнити.

В этот раз улучшим API приложения до соответствия REST, а также изменим архитектуру приложения с помощью внедрения
зависимостей.

# #6 Тестирование

- Добавьте тесты для валидации

# #5 Логирование

- Воспользуйтесь библиотекой slf4j
- объявляйте логер для каждого класса
- акже можете применить аннотацию @Slf4j

# #4 Валидация

- **Film**
    - название не может быть пустым;
    - максимальная длина описания — 200 символов;
    - дата релиза — не раньше 28 декабря 1895 года;
    - продолжительность фильма должна быть положительной.
- **User**
    - электронная почта не может быть пустой и должна содержать символ @;
    - логин не может быть пустым и содержать пробелы;
    - имя для отображения может быть пустым — в таком случае будет использован логин;
    - дата рождения не может быть в будущем.

# #3 Хранение данных / REST-контроллеры

- пакет controller
- Создайте два класса-контроллера
    - **FilmController**
    - **UserController**
- Добавьте в классы-контроллеры эндпоинты с подходящим типом запроса

# #2 Модели данных

- Создайте пакет **model**
- Добавьте в него два класса — **Film** и **User**
- добавить поля для Film
- добавить поля для User
- прописать Lombok **@Data**

# #1 Создаем собственную ветку

- В репозитории создайте ветку *controllers-films-users*
- Создайте заготовку проекта с помощью *Spring Initializr*

# java-filmorate

Template repository for Filmorate project.

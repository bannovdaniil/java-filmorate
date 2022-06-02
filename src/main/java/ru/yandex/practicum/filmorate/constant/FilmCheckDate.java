package ru.yandex.practicum.filmorate.constant;

/**
 * дата релиза — не раньше 28 декабря 1895 года;
 */
public final class FilmCheckDate {
    public static final String OLDEST_DATE = "1895-12-28";
    public final static String OLDEST_DATE_ERROR_MESSAGE = "Data must be after " + OLDEST_DATE;
}

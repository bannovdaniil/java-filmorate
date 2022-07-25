package ru.yandex.practicum.filmorate.model;

public enum LikeStatus {
    LIKE(true),
    DISLIKE(false);

    private final boolean isLike;

    LikeStatus(boolean isLike) {
        this.isLike = isLike;
    }

    public boolean getBoolean() {
        return isLike;
    }
}
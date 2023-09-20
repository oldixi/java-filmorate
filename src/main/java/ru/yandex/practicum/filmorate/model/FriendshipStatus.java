package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FriendshipStatus {
    private int id;
    private String name;

    public FriendshipStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

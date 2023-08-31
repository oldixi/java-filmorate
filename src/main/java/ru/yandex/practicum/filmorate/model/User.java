package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class User {
    private long id;

    @Email
    private String email;

    @NotNull
    @NotEmpty
    @NotBlank
    private String login;

    private String name;
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

    public void addFriend(Long friendId) {
        friends.add(friendId);
    }

    public void removeFriend(Long friendId) {
        friends.remove(friendId);
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.WrongIdException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director getDirectorById(int id) {
        if (isIncorrectId(id)) {
            throw new WrongIdException("Param must be more then 0");
        }
        Optional<Director> directorOpt = directorStorage.getDirectorById(id);
        if (directorOpt.isEmpty()) {
            throw new WrongIdException("No director with id = " + id + " in DB was found.");
        }
        return directorOpt.get();
    }

    public List<Director> getDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        if (isIllegalDirectorId(director.getId())) {
            throw new WrongIdException("No director with id = " + director.getId() + " in DB was found.");
        }
        return directorStorage.updateDirector(director);
    }

    public long deleteDirector(int id) {
        if (isIncorrectId(id))  {
            throw new WrongIdException("Param must be more then 0");
        }
        return directorStorage.deleteDirector(id);
    }

    public boolean isIllegalDirectorId(int directorId) {
        return getDirectorById(directorId) == null;
    }

    private boolean isIncorrectId(int id) {
        return id <= 0;
    }
}

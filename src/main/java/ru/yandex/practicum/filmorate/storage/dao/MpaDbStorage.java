package ru.yandex.practicum.filmorate.storage.dao;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("mpaDbStorage")
@Slf4j
@Data
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa addMpa(Mpa mpa) {
        jdbcTemplate.update("insert into mpa_dic(mpa_code, mpa) values(?, ?)", mpa.getId(), mpa.getName());
        return mpa;
    }

    public Mpa updateMpa(Mpa mpa) {
        jdbcTemplate.update("update mpa_dic set mpa = ? where mpa_code = ?", mpa.getName(), mpa.getId());
        return mpa;
    }

    public Mpa findPmaByCode(Integer code) {
        SqlRowSet mpaDicRows = jdbcTemplate.queryForRowSet("select * from mpa_dic where mpa_code = ?", code);
        if (mpaDicRows.next()) {
            log.info("Найден рейтинг: {} {}", mpaDicRows.getInt("mpa_code"), mpaDicRows.getString("mpa"));
            return new Mpa(mpaDicRows.getInt("mpa_code"), mpaDicRows.getString("mpa"));
        } else {
            log.info("Термин рейтинга с кодом {} не найден.", code);
            throw new MpaNotFoundException(String.format("Термин рейтинга с кодом %d не найден.", code));
        }
    }

    public List<Mpa> findAllPma() {
        String sql = "select * from mpa_dic";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makePma(rs));
    }

    private Mpa makePma(ResultSet rs) throws SQLException {
        int id = rs.getInt("mpa_code");
        String name = rs.getString("mpa");
        log.info("Рейтинг: {} имеет код: {}", name, id);

        return new Mpa(id, name);
    }
}

package ru.oksei.Albumify.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.oksei.Albumify.Models.Person;


@Component
public class PersonDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PersonDAO() {
        this.jdbcTemplate = null;
    }

    public int registration(Person person){
        assert jdbcTemplate != null;
        return jdbcTemplate.update("INSERT INTO users(email, fio, nickname, password) VALUES(?, ?, ?, ?)",
                person.getEmail(), person.getFio(), person.getNickname(), person.getPassword());
    }

    public Person login(Person person){
        return jdbcTemplate.query("SELECT * FROM USERS WHERE email = ? AND password = ?",
                        new Object[]{person.getEmail(), person.getPassword()}, new PersonMapper())
                .stream().findAny().orElse(null);
    }

    public Person ourProfile(String nickname){
        return jdbcTemplate.query("SELECT * FROM USERS WHERE nickname = ?",
                        new Object[]{nickname}, new PersonMapper())
                .stream().findAny().orElse(null);
    }
}

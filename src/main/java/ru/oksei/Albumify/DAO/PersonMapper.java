package ru.oksei.Albumify.DAO;

import org.springframework.jdbc.core.RowMapper;
import ru.oksei.Albumify.Models.Person;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException{
        Person person = new Person();
        try{
            person.setId(resultSet.getInt("id"));
            person.setEmail(resultSet.getString("email"));
            person.setFio(resultSet.getString("fio"));
            person.setNickname(resultSet.getString("nickname"));
            person.setPassword(resultSet.getString("password"));
        }
        catch (Exception e){
            return new Person("-", "-", "-", "-", "-");
        }
        return person;
    }
}

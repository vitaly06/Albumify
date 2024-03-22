package ru.oksei.Albumify.DAO;

import org.springframework.jdbc.core.RowMapper;
import ru.oksei.Albumify.Models.Album;
import ru.oksei.Albumify.Models.Person;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AlbumMapper implements RowMapper<Album> {
    @Override
    public Album mapRow(ResultSet resultSet, int i) throws SQLException {
        Album album = new Album();
        try{
            album.setId(resultSet.getInt("id"));
            album.setName(resultSet.getString("name"));
            album.setType(resultSet.getString("type"));
            album.setDescription(resultSet.getString("description"));
            album.setUserId(resultSet.getInt("userId"));
        }
        catch (Exception e){
            return new Album();
        }
        return album;
    }
}

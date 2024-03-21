package ru.oksei.Albumify.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.oksei.Albumify.Models.Album;

import java.util.List;

@Component
public class AlbumDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AlbumDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AlbumDAO() {
        this.jdbcTemplate = null;
    }

    public int addAlbum(Album album){
        return jdbcTemplate.update("INSERT INTO ALBUMS(name, description, userId) VALUES(?, ?, ?)",
                album.getName(), album.getDescription(), album.getUserId());
    }

    public List<Album> getAllUserAlbums(int userId){
        return jdbcTemplate.query("SELECT * FROM ALBUMS WHERE userId = ?",
                new Object[]{userId}, new AlbumMapper());
    }

    public Album getAlbum(String albumName){
        return jdbcTemplate.query("SELECT * FROM ALBUMS WHERE name = ?",
                new Object[]{albumName}, new AlbumMapper())
                .stream().findAny().orElse(null);
    }
}

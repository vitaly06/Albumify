package ru.oksei.Albumify.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.oksei.Albumify.Models.Album;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        return jdbcTemplate.update("INSERT INTO ALBUMS(name, description, type, userId) VALUES(?, ?, ?, ?)",
                album.getName(), album.getDescription(), album.getType(), album.getUserId());
    }

    public List<Album> getAllUserAlbums(int userId){
        return jdbcTemplate.query("SELECT * FROM ALBUMS WHERE userId = ?",
                new Object[]{userId}, new AlbumMapper());
    }

    // Все альбомы чужого профиля
    public List<Album> getAllOurProfileAlbums(int userId){
        return jdbcTemplate.query("SELECT * FROM ALBUMS WHERE userId = ? AND NOT(type = 'closed')",
                new Object[]{userId}, new AlbumMapper());
    }

    public List<Album> getAllUserClosedAlbums(int userId){
        return jdbcTemplate.query("SELECT * FROM ALBUMS WHERE userId = ? AND type = 'closed'",
                new Object[]{userId}, new AlbumMapper());
    }

    public List<Album> getAllUserGroupeAlbums(int userId){
        return jdbcTemplate.query("SELECT * FROM ALBUMS WHERE userId = ? AND type = 'groupe'",
                new Object[]{userId}, new AlbumMapper());
    }

    public List<Album> getAllUserPersonalAlbums(int userId){
        return jdbcTemplate.query("SELECT * FROM ALBUMS WHERE userId = ? AND type = 'personal'",
                new Object[]{userId}, new AlbumMapper());
    }

    public Album getAlbum(String albumName){
        return jdbcTemplate.query("SELECT * FROM ALBUMS WHERE name = ?",
                new Object[]{albumName}, new AlbumMapper())
                .stream().findAny().orElse(null);
    }


}

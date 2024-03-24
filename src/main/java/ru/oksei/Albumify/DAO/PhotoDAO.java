package ru.oksei.Albumify.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.oksei.Albumify.Models.Album;
import ru.oksei.Albumify.Models.Photo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
public class PhotoDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PhotoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public PhotoDAO() {
        this.jdbcTemplate = null;
    }

    public int savePhoto(Photo photo) throws IOException {
        System.out.println(photo.getFile());
        return jdbcTemplate.update("INSERT INTO PHOTO(file, name, description, album, tags, userId) VALUES(?, ?, ?, ?, ?, ?)",
                photo.getFile().getOriginalFilename(), photo.getName(),
                photo.getDescription(), photo.getAlbum(), photo.getTags(), photo.getUserId());
    }

    public List<Photo> getPhotos(int userId, String albumName) throws IOException {
        assert jdbcTemplate != null;
        return jdbcTemplate.query("SELECT * FROM photo WHERE userId = ? AND album = ?",
                new Object[]{userId, albumName}, new PhotoMapper());
    }

    // Страница с фотографией
    public Photo getPhotoForSite(int userId, String photoName){
        return jdbcTemplate.query("SELECT * FROM photo WHERE userId = ? AND name = ?",
                new Object[]{userId, photoName}, new PhotoMapper())
                .stream().findAny().orElse(null);
    }

    // Фотографии в ленте
    public List<Photo> getIndexPhoto() {
        return jdbcTemplate.query("SELECT photo.id, photo.file, photo.name, photo.description, photo.album, " +
                        "photo.tags, photo.userId from photo INNER JOIN albums ON photo.album = albums.name AND NOT(albums.type = 'closed');",
                new PhotoMapper());
    }

    public void deletePhotos(String fileNames, String albumName){
        String[] files = fileNames.split(",");
        for (String file : files){
            jdbcTemplate.update("DELETE FROM photo WHERE file = ? AND album = ?",
                    file, albumName);
        }
    }
}

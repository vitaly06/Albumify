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
        /*byte[] image = extractBytes(photo.getFile().getOriginalFilename());
        for (byte x : image){
            System.out.println(x);
        }*/

    }

    /*public byte[] extractBytes (String ImageName) throws IOException {
        // open image
        File imgPath = new File(ImageName);
        BufferedImage bufferedImage = ImageIO.read(imgPath);

        // get DataBufferBytes from Raster
        WritableRaster raster = bufferedImage .getRaster();
        DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

        return ( data.getData() );
    }*/
}

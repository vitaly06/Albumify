package ru.oksei.Albumify.DAO;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.multipart.MultipartFile;
import ru.oksei.Albumify.Models.Person;
import ru.oksei.Albumify.Models.Photo;

import java.io.*;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PhotoMapper implements RowMapper<Photo> {
    @Override
    public Photo mapRow(ResultSet resultSet, int i) throws SQLException{
        Photo photo = new Photo();
        String fileName = resultSet.getString("file");
        try {
            File file = new File("./src/main/resources/static/data/" + fileName);
            /*if (file.exists()) {
                //System.out.println("File Exist => " + file.getName() + " :: " + file.getAbsolutePath());
            }*/
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain",
                    IOUtils.toByteArray(input));
            photo.setFile(multipartFile);
        } catch (IOException e) {
            System.out.println("Exception => " + e.getLocalizedMessage());
        }
        try{
            photo.setId(resultSet.getInt("id"));
            photo.setName(resultSet.getString("name"));
            photo.setDescription(resultSet.getString("description"));
            photo.setAlbum(resultSet.getString("album"));
            photo.setTags(resultSet.getString("tags"));
            photo.setUserId(resultSet.getInt("userId"));
            photo.setType(resultSet.getString("type"));
        }
        catch (Exception e){
            return new Photo();
        }
        return photo;
    }
}

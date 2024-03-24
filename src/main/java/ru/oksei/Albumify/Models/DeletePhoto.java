package ru.oksei.Albumify.Models;

public class DeletePhoto {
    private String albumName, fileName;
    public DeletePhoto(){}

    public DeletePhoto(String albumName, String fileName){
        this.albumName = albumName;
        this.fileName = fileName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

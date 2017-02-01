package com.ahchim.android.musicplayer;

/**
 * Created by Ahchim on 2017-02-01.
 */

public class Music {
    private String id;
    private String album_id;
    private String imagePath;
    private String artist;
    private String title;

    public void setId(String id) {
        this.id = id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }
}

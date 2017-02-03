package com.ahchim.android.musicplayer;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Ahchim on 2017-02-01.
 */

public class Music {
    private String id;
    private String album_id;
    private String imagePath;
    private String artist;
    private String title;
    private Uri album_image;
    private Bitmap bitmap_image;
    private Uri uri;

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

    public void setAlbum_image(Uri album_image) { this.album_image = album_image; }

    public void setBitmap_image(Bitmap bitmap_image) {
        this.bitmap_image = bitmap_image;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
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

    public Uri getAlbum_image() { return album_image; }

    public Bitmap getBitmap_image() {
        return bitmap_image;
    }

    public Uri getUri() {
        return uri;
    }
}

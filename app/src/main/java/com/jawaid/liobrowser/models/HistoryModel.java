package com.jawaid.liobrowser.models;

public class HistoryModel {

    private String title;
    private String url;
    private String date;
    private byte[] image;


    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public HistoryModel(String title, String url,String date, byte[] image) {
        this.title = title;
        this.url = url;
        this.image=image;
        this.date=date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

package com.jawaid.liobrowser.models;

public class DownloadModel {

    private String title;
    private String url;
    private String isDownloading;
    private int downloadid;

    public DownloadModel() {
    }

    public DownloadModel(String title, String url, String isDownloading, int downloadid) {
        this.title = title;
        this.url = url;
        this.isDownloading = isDownloading;
        this.downloadid = downloadid;
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

    public String getIsDownloading() {
        return isDownloading;
    }

    public void setIsDownloading(String isDownloading) {
        this.isDownloading = isDownloading;
    }

    public int getDownloadid() {
        return downloadid;
    }

    public void setDownloadid(int downloadid) {
        this.downloadid = downloadid;
    }
}

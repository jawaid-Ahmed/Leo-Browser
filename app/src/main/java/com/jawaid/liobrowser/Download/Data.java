package com.jawaid.liobrowser.Download;

import android.net.Uri;
import android.os.Environment;

import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;


public final class Data {

    public static List<String> downloadUrls=new ArrayList<>();;

    public static final String[] sampleUrls = new String[]{
            "http://speedtest.ftp.otenet.gr/files/test100Mb.db",
            "http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_640x360.m4v",
            "http://media.mongodb.org/zips.json",
            "http://www.exampletonyotest/some/unknown/123/Errorlink.txt",
            "http://storage.googleapis.com/ix_choosemuse/uploads/2016/02/android-logo.png",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"};

    public static List<String> getUrls(){
//        downloadUrls.add(sampleUrls[0]);
//        downloadUrls.add(sampleUrls[1]);
//        downloadUrls.add(sampleUrls[2]);
//        downloadUrls.add(sampleUrls[3]);
//        downloadUrls.add(sampleUrls[4]);
//        downloadUrls.add(sampleUrls[5]);

        return downloadUrls;
    }

    private Data() {

    }

    @NonNull
    private static List<Request> getFetchRequests() {
        final List<Request> requests = new ArrayList<>();
        for (String sampleUrl : getUrls()) {
            final Request request = new Request(sampleUrl, getFilePath(sampleUrl));
            requests.add(request);
        }
        return requests;
    }

    @NonNull
    public static List<Request> getFetchRequestWithGroupId(final int groupId) {
        final List<Request> requests = getFetchRequests();
        for (Request request : requests) {
            request.setGroupId(groupId);
        }
        return requests;
    }

    @NonNull
    private static String getFilePath(@NonNull final String url) {
        final Uri uri = Uri.parse(url);
        final String fileName = uri.getLastPathSegment();
        final String dir = getSaveDir();
        return (dir + "/" + fileName);
    }

    @NonNull
    static String getNameFromUrl(final String url) {
        return Uri.parse(url).getLastPathSegment();
    }

    @NonNull
    public static List<Request> getGameUpdates() {
        final List<Request> requests = new ArrayList<>();
        final String url = "http://speedtest.ftp.otenet.gr/files/test100k.db";
        for (int i = 0; i < 10; i++) {
            final String filePath = getSaveDir() + "/gameAssets/" + "asset_" + i + ".asset";
            final Request request = new Request(url, filePath);
            request.setPriority(Priority.HIGH);
            requests.add(request);
        }
        return requests;
    }

    @NonNull
    public static String getSaveDir() {
        return Environment.getExternalStorageDirectory().toString() + "/LeoBrowserDownloads";
    }

}

package com.jawaid.liobrowser.Helpers;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.jawaid.liobrowser.Download.Data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public  class DownPicUtil {

    public static void downPic(String url, DownFinishListener downFinishListener){
        downPic(url, null, null, null, downFinishListener);
    }

    public static void downPic(String url, String userAgent, String referer, String cookie, DownFinishListener downFinishListener){
        File file=new File(Data.getSaveDir(),"/Images");
        if(!file.exists()){
            file.mkdirs();
        }
        loadPic(file.getPath(), url, userAgent, referer, cookie, downFinishListener);

    }

    private static void loadPic(final String filePath, final String url, final String userAgent, final String referer, final String cookie, final DownFinishListener downFinishListener) {
        new AsyncTask<Void,Void,String>(){
            String fileName;
            InputStream is;
            OutputStream out;

            @Override
            protected String doInBackground(Void... voids) {
                String[] split = url.split("/");
                fileName = split[split.length - 1];
                String now = String.valueOf(System.currentTimeMillis());
                File picFile = new File(filePath + File.separator + now);
                if(! picFile.exists()) {
                    try {
                        Base64ImgHelper base64ImgHelper = new Base64ImgHelper(url);
                        if (base64ImgHelper.isBase64Img()) {
                            fileName = now;
                            byte[] image = base64ImgHelper.decode();
                            is = new ByteArrayInputStream(image);
                        } else {
                            URL picUrl = new URL(url);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) picUrl.openConnection();
                            httpURLConnection.setConnectTimeout(10000);
                            httpURLConnection.setReadTimeout(30000);
                            httpURLConnection.setDoInput(true);
                            httpURLConnection.setDoOutput(false);
                            httpURLConnection.setRequestMethod("GET");
                            httpURLConnection.setUseCaches(false);

                            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            if (!TextUtils.isEmpty(userAgent)) {
                                httpURLConnection.setRequestProperty("User-Agent", userAgent);
                            }
                            if (!TextUtils.isEmpty(referer)) {
                                httpURLConnection.setRequestProperty("Referer", referer);
                            }
                            if (!TextUtils.isEmpty(cookie)) {
                                httpURLConnection.setRequestProperty("Cookie", cookie);
                            }
                            httpURLConnection.connect();

                            int response = httpURLConnection.getResponseCode();
                            if (response == HttpURLConnection.HTTP_OK || response == HttpURLConnection.HTTP_NOT_MODIFIED) {
                                is = httpURLConnection.getInputStream();
                                if (is == null) {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                        }
                        out = new FileOutputStream(picFile);
                        byte[] b = new byte[1024];
                        int end;
                        while ((end = is.read(b)) != -1) {
                            out.write(b, 0, end);
                        }

                        if (is != null) {
                            is.close();
                        }

                        if (out != null) {
                            out.close();
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                String extension = FormatHelper.getExtension(picFile);
                String[] extensions = fileName.split("\\.");
                int splitLength = extensions.length;
                String newFileNameNoExtension;
                if (splitLength > 1) {
                    newFileNameNoExtension = fileName.substring(0, fileName.length() - extensions[splitLength - 1].length() - 1);
                } else {
                    newFileNameNoExtension = fileName;
                }


                if (extension == null) {

                    if (splitLength > 1) {

                        return renamePic(picFile, filePath, newFileNameNoExtension, extensions[splitLength - 1], MODE.MODE_INCREMENT);
                    } else {
                        return renamePic(picFile, filePath, newFileNameNoExtension, null, MODE.MODE_INCREMENT);
                    }
                }
                String md5 = Md5Helper.getFileMD5ToString(picFile);
                if (TextUtils.isEmpty(md5)) {
                    return renamePic(picFile, filePath, newFileNameNoExtension, extension, MODE.MODE_INCREMENT);
                } else {
                    return renamePic(picFile, filePath, md5.substring(0, 16), extension, MODE.MODE_IGNORE);
                }
            }

            @Override
            protected void onPostExecute(String path) {
                super.onPostExecute(path);
                if(path!=null){
                    downFinishListener.onDownFinish(path);
                } else {
                    downFinishListener.onError();
                }
            }
        }.execute();
    }

    private enum MODE{
        MODE_INCREMENT,

        MODE_IGNORE,
        MODE_OVERRIDE
    }

    private static String renamePic(File picFile, String filePath, String newFileNameNoExtension, String extension, MODE mode) {
        String extensionWithPoint = TextUtils.isEmpty(extension)? "": "." + extension;
        String newFileName = newFileNameNoExtension + extensionWithPoint;
        File newFile = new File(filePath + File.separator + newFileName);
        switch (mode) {

            case MODE_INCREMENT:
                int count = 1;
                while (newFile.exists()) {
                    // if file of new name exists, add (count) in filename;
                    newFileName = newFileNameNoExtension + "(" + count + ")" + extensionWithPoint;
                    newFile = new File(filePath + File.separator + newFileName);
                    count ++;
                }
                break;
            case MODE_IGNORE:
                if (newFile.exists()) {
                    return newFile.getPath();
                }
                break;
            case MODE_OVERRIDE:
                if (newFile.exists()) {
                    if (!newFile.delete()) {
                        return null;
                    }
                }
                break;
        }

        if (rename(picFile, newFileName)) {
            return newFile.getPath();
        } else {
            return null;
        }
    }

    private static boolean rename(final File file, final String newName) {
        if (file == null) return false;
        if (!file.exists()) return false;
        if (TextUtils.isEmpty(newName)) return false;
        if (newName.equals(file.getName())) return true;
        File newFile = new File(file.getParent() + File.separator + newName);
        return !newFile.exists()
                && file.renameTo(newFile);
    }


    public interface DownFinishListener{
        void onDownFinish(String path);
        void onError();
    }
}
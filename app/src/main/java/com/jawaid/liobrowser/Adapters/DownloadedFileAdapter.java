package com.jawaid.liobrowser.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jawaid.liobrowser.Download.Utils;
import com.jawaid.liobrowser.Interfaces.OnItemClickListener;
import com.jawaid.liobrowser.R;

import java.io.File;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadedFileAdapter extends RecyclerView.Adapter<DownloadedFileAdapter.MyViewHolder> {

    List<File> files;
    Context context;
    OnItemClickListener onItemClickListener;

    public DownloadedFileAdapter(List<File> files, Context context, OnItemClickListener onItemClickListener) {
        this.files = files;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.downloaded_file_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        MyViewHolder holder1 = holder;
        File file = files.get(position);

        holder1.dateTextView.setText(getModifiedDate(file));
        holder1.title.setText(file.getName());
        holder1.size.setText(Utils.getStringSizeLengthFile(file.length()));
        //String fileExtension = MimeTypeMap.getFileExtensionFromUrl(Uri.parse(file.getPath()).toString());
       // holder.extensionTextView.setText(fileExtension);

        setIcon(holder1,file.getPath());
    }

    private void setIcon(MyViewHolder holder, String url) {

        Uri uri = Uri.parse(url);
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            Glide.with(context).load(R.drawable.documents).into(holder.icon);
        } else if (url.toString().contains(".pdf")) {
            Glide.with(context).load(R.drawable.documents).into(holder.icon);
        } else if (url.toString().contains(".apk")) {
            Glide.with(context).load(R.drawable.apks).into(holder.icon);
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            Glide.with(context).load(R.drawable.documents).into(holder.icon);

        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            Glide.with(context).load(R.drawable.documents).into(holder.icon);
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            Glide.with(context).load(R.drawable.zips).into(holder.icon);

        } else if (url.toString().contains(".rtf")) {
            Glide.with(context).load(R.drawable.documents).into(holder.icon);
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            Glide.with(context).load(R.drawable.music).into(holder.icon);
        } else if (url.toString().contains(".gif")) {
            Glide.with(context).load(R.drawable.galary).into(holder.icon);
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg")
                || url.toString().contains(".svg") || url.toString().contains(".png")) {

            Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeFile(url),
                    42,
                    42);
            Glide.with(context).load(thumbImage).into(holder.icon);
        } else if (url.toString().contains(".txt")) {
            Glide.with(context).load(R.drawable.documents).into(holder.icon);
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                url.toString().contains(".mpeg") || url.toString().contains(".flv") || url.toString().contains(".mkv") ||
                url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail( uri.getPath() , MediaStore.Images.Thumbnails.MINI_KIND );
            Glide.with(context).load(bitmap2).into(holder.icon);
        }else {
            Glide.with(context).load(R.drawable.documents).into(holder.icon);
        }
    }

    private String getModifiedDate(File file) {
        String datestr = " ";
        Date date = new Date(file.lastModified());
        datestr = date.getDate() + "/" + date.getMonth() + "/" + date.getYear();
        return datestr;
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, size;
        ImageButton shareBtn;
        ImageView icon;
        TextView extensionTextView;
        TextView dateTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            size = itemView.findViewById(R.id.size_Downloaded);
            title = itemView.findViewById(R.id.titleDownloaded);
            icon = itemView.findViewById(R.id.icon_downloaded);
            shareBtn = itemView.findViewById(R.id.share_downloaded);
            dateTextView = itemView.findViewById(R.id.date_Downloaded);
            extensionTextView = itemView.findViewById(R.id.fileExtensionDownloaded);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.onLongItemClick(getAdapterPosition());
                    return true;

                }
            });
            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });


        }
    }
}

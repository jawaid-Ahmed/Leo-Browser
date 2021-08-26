package com.jawaid.liobrowser.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jawaid.liobrowser.Activities.BookmarksActivity;
import com.jawaid.liobrowser.Interfaces.BookmarkOnItemClickListener;
import com.jawaid.liobrowser.R;
import com.jawaid.liobrowser.models.HistoryModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookmarksRecyclerAdapter extends RecyclerView.Adapter<BookmarksRecyclerAdapter.HistoryViewHolder> {


    private Context context;
    private List<HistoryModel> histories;
    private BookmarksActivity historyActivity;
    BookmarkOnItemClickListener onHItemClickListener;


    public BookmarksRecyclerAdapter(BookmarksActivity historyActivity, List<HistoryModel> histories, BookmarkOnItemClickListener onHItemClickListener) {
        this.histories=histories;
        this.historyActivity = historyActivity;
        this.onHItemClickListener=onHItemClickListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.historysingleitem, parent, false);;

        HistoryViewHolder viewHolder=new HistoryViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        HistoryModel dataModel = histories.get(position);

        if (!historyActivity.isContextualModeEnabled){
            holder.checkBox.setVisibility(View.INVISIBLE);
        }else {
            holder.checkBox.setVisibility(View.VISIBLE);
        }
        holder.title.setText(dataModel.getTitle());
        holder.url.setText(dataModel.getUrl());
        Bitmap fevicon = BitmapFactory.decodeByteArray(dataModel.getImage(), 0, dataModel.getImage().length);
        holder.bitmap.setImageBitmap(fevicon);


        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyActivity.checkSelection(view,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView url;
        ImageView bitmap;
        RelativeLayout root;
        CheckBox checkBox;

        public HistoryViewHolder(@NonNull View view) {
            super(view);
            title=view.findViewById(R.id.history_title);
            url=view.findViewById(R.id.history_url);
            bitmap =view.findViewById(R.id.btmapimage);
            root=view.findViewById(R.id.history_rootView);
            checkBox=view.findViewById(R.id.checkbox_history);

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onHItemClickListener.onBItemClick(getAdapterPosition());
                }
            });
            root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onHItemClickListener.onBLongItemClick(getAdapterPosition());
                    return true;
                }
            });
        }

    }

}

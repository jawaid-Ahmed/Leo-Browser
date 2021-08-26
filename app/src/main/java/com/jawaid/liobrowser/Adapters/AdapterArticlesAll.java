package com.jawaid.liobrowser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jawaid.liobrowser.Interfaces.OnHorizontalItemClickListener;
import com.jawaid.liobrowser.R;
import com.jawaid.liobrowser.models.Article;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterArticlesAll extends RecyclerView.Adapter<AdapterArticlesAll.MyViewHolder> {

    List<Article> articles;
    Context context;
    OnHorizontalItemClickListener onHItemClickListener;

    public AdapterArticlesAll(List<Article> articles, Context context, OnHorizontalItemClickListener onItemClickListener) {
        this.articles = articles;
        this.context = context;
        this.onHItemClickListener=onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_news,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (position==0){
            MyViewHolder holder1 = holder;
            Article article = articles.get(position);


            holder1.author.setText("Author: " + article.getAuthor());
            holder1.title.setText(article.getTitle());
            holder1.desc.setText(article.getDescripion());
            holder1.url.setText(article.getUrl());
            holder1.publishedAt.setText(article.getPublishedAt());
            holder1.content.setText(article.getContent());

        }else {

            MyViewHolder holder1 = holder;
            Article article = articles.get(position);


            holder1.author.setText("Author: " + article.getAuthor());
            holder1.title.setText(article.getTitle());
            holder1.desc.setText(article.getDescripion());
            holder1.url.setText(article.getUrl());
            holder1.publishedAt.setText(article.getPublishedAt());
            holder1.content.setText(article.getContent());


            Glide.with(context) //passing context
                    .load(article.getUrlToImage()) //passing your url to load image.
                    .placeholder(R.color.darkColor) //this would be your default image (like default profile or logo etc). it would be loaded at initial time and it will replace with your loaded image once glide successfully load image using url.
                    .diskCacheStrategy(DiskCacheStrategy.ALL) //using to load into cache then second time it will load fast.// when image (url) will be loaded by glide then this face in animation help to replace url image in the place of placeHolder (default) image.
                    .fitCenter()//this method help to fit image into center of your ImageView
                    .into(holder.urlImage);

        }

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title,url,desc,author,publishedAt,content;

        ImageView urlImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            author=itemView.findViewById(R.id.author);
            title=itemView.findViewById(R.id.title);
            desc=itemView.findViewById(R.id.description);
            url=itemView.findViewById(R.id.url);
            urlImage=itemView.findViewById(R.id.urlImage);
            publishedAt=itemView.findViewById(R.id.publisheddate);
            content=itemView.findViewById(R.id.content);

            urlImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onHItemClickListener.onHItemClick(getAdapterPosition());
                }
            });
            urlImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onHItemClickListener.onHLongItemClick(getAdapterPosition());
                    return true;

                }
            });




        }
    }
}

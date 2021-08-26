package com.jawaid.liobrowser.Adapters;

import android.content.Context;

import com.jawaid.liobrowser.Activities.DownloadActivity;
import com.jawaid.liobrowser.Interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {

    List<Fragment> arraylist=new ArrayList<>();
    OnItemClickListener onItemClickListener;
    private Context context;

    public ViewPagerFragmentAdapter(@NonNull Context context,OnItemClickListener onItemClickListener) {
        super((FragmentActivity) context);

        this.onItemClickListener=onItemClickListener;
        this.context=context;
    }

    public ViewPagerFragmentAdapter(DownloadActivity downloadActivity) {
        super(downloadActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return arraylist.get(position);
    }

    public void addFragment(Fragment fragment){
        arraylist.add(fragment);
    }

    public Fragment getItemPosition(int pos){
        return arraylist.get(pos);
    }


    @Override
    public int getItemCount() {
        return arraylist.size();
    }
}
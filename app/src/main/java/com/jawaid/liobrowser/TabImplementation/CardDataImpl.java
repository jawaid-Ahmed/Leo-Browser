package com.jawaid.liobrowser.TabImplementation;

import android.view.View;

import com.jawaid.liobrowser.R;

import java.util.ArrayList;
import java.util.List;

public class CardDataImpl implements ECCardData<String> {

    private String cardTitle;
    private View headBackgroundResource;


    public CardDataImpl(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    @Override
    public Integer getHeadBackgroundResource() {
        return R.layout.home_fragment;
    }

    public static List<ECCardData> generateExampleData() {
        List<ECCardData> list = new ArrayList<>();

        list.add(new CardDataImpl("Card 1"));
        return list;
    }
}
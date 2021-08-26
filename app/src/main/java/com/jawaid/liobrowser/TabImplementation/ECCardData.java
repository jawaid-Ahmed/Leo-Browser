package com.jawaid.liobrowser.TabImplementation;


import androidx.annotation.LayoutRes;

/**
 * Implement this interface to provide data to pager view and content list inside pager card
 *
 * @param <T> Type of items in card content list
 */
public interface ECCardData<T> {

    @LayoutRes
    Integer getHeadBackgroundResource();



}

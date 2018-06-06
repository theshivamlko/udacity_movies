package com.navoki.megamovies.Callbacks;

import android.widget.ImageView;

/**
 * Created by Shivam Srivastava on 6/6/2018.
 */
public interface OnAdapterListener {
    void getNextPagingData();
    void moveToDetailsScreen(ImageView imageView, String imgPath, String id, String title);
}

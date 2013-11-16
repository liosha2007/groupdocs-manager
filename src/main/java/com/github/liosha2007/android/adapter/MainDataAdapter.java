package com.github.liosha2007.android.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by liosha on 07.11.13.
 */
public class MainDataAdapter extends SimpleAdapter {
    /**
     * Constructor
     *
     * @param context  The rootFragment where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a rootFragment layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */
    public MainDataAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public void setViewImage(ImageView imageView, String value) {
        super.setViewImage(imageView, value);
    }

    @Override
    public void setViewImage(ImageView imageView, int value) {
        imageView.setImageResource(value);
    }

    @Override
    public void setViewText(TextView textView, String text) {
        super.setViewText(textView, text);
    }
}

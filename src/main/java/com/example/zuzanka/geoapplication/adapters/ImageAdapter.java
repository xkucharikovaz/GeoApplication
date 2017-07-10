package com.example.zuzanka.geoapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zuzanka.geoapplication.BillboardTypeActivity;
import com.example.zuzanka.geoapplication.R;

/**
 * Created by zuzanka on 11. 10. 2016.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.billboard_type_item, parent, false);

        relativeLayout.setTag(mTextIds[position]);

        ImageView imageView = (ImageView) relativeLayout.findViewById(R.id.typeImageView);

        imageView.setImageResource(mThumbIds[position]);
        TextView textView = (TextView) relativeLayout.findViewById(R.id.textView);
        textView.setText(mTextIds[position]);

        return relativeLayout;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.billboard,
            R.drawable.megaboard,
            R.drawable.citylight,
            R.drawable.trojnozka,
            R.drawable.hypercube
    };

    private Integer[] mTextIds = {
            R.string.billboard,
            R.string.megaboard,
            R.string.citylight,
            R.string.trojnozka,
            R.string.hypercube
    };
}

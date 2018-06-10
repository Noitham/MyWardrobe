package com.soft.morales.mysmartwardrobe.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.soft.morales.mysmartwardrobe.R;
import com.soft.morales.mysmartwardrobe.model.Garment;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    /**
     * CustomAdapter for our listview.
     * It will show our list of items with a description and a thumbnail.
     */

    private Context context;
    private List<Garment> rowItems;

    // Constructor
    public CustomAdapter(Context context, List<Garment> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
    }

    // Method that will get the count of items
    @Override
    public int getCount() {
        return rowItems.size();
    }


    // Method for getting the position of our item
    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    // Method for getting the item id
    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView garmentPic;
        TextView garmentName;
        TextView garmentBrand;
        TextView garmentCategory;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            assert mInflater != null;
            convertView = mInflater.inflate(R.layout.list_item, null, false);
            holder = new ViewHolder();

            holder.garmentName = (TextView) convertView
                    .findViewById(R.id.garment_name);
            holder.garmentPic = (ImageView) convertView
                    .findViewById(R.id.garment_pic);
            holder.garmentBrand = (TextView) convertView.findViewById(R.id.brand);
            holder.garmentCategory = (TextView) convertView
                    .findViewById(R.id.category);

            Garment row_pos = rowItems.get(position);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 20;

            Uri myUri = Uri.parse(row_pos.getPhoto().getPath());

            holder.garmentPic.setScaleType(ImageView.ScaleType.FIT_CENTER);
            holder.garmentPic.getLayoutParams().width = 200;
            Glide.with(context).load(myUri).into(holder.garmentPic);
            holder.garmentName.setText(row_pos.getName());
            holder.garmentBrand.setText(row_pos.getBrand());
            holder.garmentCategory.setText(row_pos.getCategory());

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public void removeItem(int pos) {
        rowItems.remove(pos);
    }
}
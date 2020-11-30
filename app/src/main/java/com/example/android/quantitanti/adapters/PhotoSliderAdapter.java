package com.example.android.quantitanti.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.quantitanti.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PhotoSliderAdapter extends SliderViewAdapter<PhotoSliderAdapter.PhotoSliderAdapterVH> {

    private Context context;
    private Map<String, String> mSliderItems = new HashMap<>();
    private List<String> mSliderNames = new ArrayList<>();
    // private Set<String> mSliderNames = new HashSet<>();


    public PhotoSliderAdapter(Context context) {
        this.context = context;
    }


    @Override
    public PhotoSliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_image_layout_custom, null);
        return new PhotoSliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(PhotoSliderAdapter.PhotoSliderAdapterVH viewHolder, final int position) {
        String sliderName = mSliderNames.get(position);

        viewHolder.textViewDescription.setText(sliderName);
        viewHolder.textViewDescription.setTextSize(16);
        viewHolder.textViewDescription.setTextColor(Color.WHITE);
        Glide.with(viewHolder.itemView)
                .load(mSliderItems.get(sliderName))
                .fitCenter()
                .into(viewHolder.imageViewBackground);

    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        if (mSliderNames == null) {
            return 0;
        }
        return mSliderNames.size();
    }

    public void setmSliderItems(Map<String, String> photos) {
        mSliderItems = photos;
        for (Map.Entry<String, String> entry : photos.entrySet()) {
            mSliderNames.add(entry.getKey());
        }
        notifyDataSetChanged();
    }

    public static class PhotoSliderAdapterVH extends SliderViewAdapter.ViewHolder {
        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public PhotoSliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}

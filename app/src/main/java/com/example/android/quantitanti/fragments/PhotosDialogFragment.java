package com.example.android.quantitanti.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.android.quantitanti.R;
import com.example.android.quantitanti.adapters.PhotoSliderAdapter;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PhotosDialogFragment extends DialogFragment {

    public static final String BUNDLE_PHOTOS = "bundlePhotos";
    private SliderView sliderView;
    private PhotoSliderAdapter sliderAdapter;

    private Map<String, String> photos = new HashMap<>();

    public PhotosDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static PhotosDialogFragment newInstance(Map<String, String> hashmap) {
        PhotosDialogFragment photosDialogFragment = new PhotosDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_PHOTOS, (Serializable) hashmap);
        photosDialogFragment.setArguments(args);
        return photosDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photos_dialog, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sliderView = view.findViewById(R.id.imageSlider);
        sliderAdapter = new PhotoSliderAdapter(getContext());

        sliderView.setSliderAdapter(sliderAdapter);
        if (getArguments() != null) {
            photos = (Map<String, String>) getArguments().getSerializable(BUNDLE_PHOTOS);
            sliderAdapter.setmSliderItems(photos);

            sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
//            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
//            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
//            sliderView.setIndicatorSelectedColor(Color.WHITE);
//            sliderView.setIndicatorUnselectedColor(Color.GRAY);
//            sliderView.setScrollTimeInSec(1); //set scroll delay in seconds :
//            sliderView.startAutoCycle();
        }
    }


}

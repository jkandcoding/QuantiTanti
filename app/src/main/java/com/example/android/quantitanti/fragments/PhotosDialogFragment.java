package com.example.android.quantitanti.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.example.android.quantitanti.R;
import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.helpers.Helper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PhotosDialogFragment extends DialogFragment {

    public static final String BUNDLE_PHOTOS = "bundlePhotos";
    private SliderLayout slider;

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
        slider = view.findViewById(R.id.slider);
        if (getArguments() != null) {
            photos = (Map<String, String>) getArguments().getSerializable(BUNDLE_PHOTOS);

            for (Map.Entry<String, String> entry : photos.entrySet()) {

                TextSliderView textSliderView = new TextSliderView(getContext());
                textSliderView
                        .description(entry.getKey())
                        .image(entry.getValue())
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop);

                //view transition effects:
                slider.setPresetTransformer(5);
                if (photos.size() == 1) {
                    slider.stopAutoCycle();
                    slider.setPagerTransformer(false, new BaseTransformer() {
                        @Override
                        protected void onTransform(View view, float position) {
                        }
                    });
                }
                slider.addSlider(textSliderView);
                slider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
            }
        }
    }

    @Override
    public void onStop() {
        slider.stopAutoCycle();
        super.onStop();
    }
}

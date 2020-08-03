package com.example.android.quantitanti.fragments;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.example.android.quantitanti.R;
import com.example.android.quantitanti.viewmodels.FilterViewModel;
import com.google.android.material.transition.platform.MaterialFade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FilterDialogFragment extends DialogFragment {

    private FilterViewModel viewModel;

    private List<String> categoriesList;
    private List<String> tagsList;
    private List<String> categoriesForFilter;
    private List<String> tagsForFilter;


    private TextView tv_category_filter;
    private TextView tv_categoryList_filter;
    private TextView tv_tag_filter;
    private TextView tv_tagList_filter;

    private Button btn_ok_filter;
    private Button btn_cancel_filter;
    private Button cp_clear_categories;
    private Button cp_clear_tags;

    private OnDataPass dataPasser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_filter_dialog, container, false);

        tv_category_filter = v.findViewById(R.id.tv_category_filter);
        tv_categoryList_filter = v.findViewById(R.id.tv_categoryList_filter);
        tv_tag_filter = v.findViewById(R.id.tv_tag_filter);
        tv_tagList_filter = v.findViewById(R.id.tv_tagList_filter);

        btn_ok_filter = v.findViewById(R.id.btn_ok_filter);
        btn_cancel_filter = v.findViewById(R.id.btn_cancel_filter);
        cp_clear_categories = v.findViewById(R.id.cp__clear_categories);
        cp_clear_tags = v.findViewById(R.id.cp__clear_tags);

        setupViewModel();
        setupViews();
        setupOnClickListeners();

        return v;
    }


    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        viewModel.getCategoryNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> categoriesList) {
                setCategoryNames(categoriesList);
            }
        });

        viewModel.getTagNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> tagsList) {
                setTagNames(tagsList);
            }
        });
    }

    private void setupViews() {
        if (viewModel.getCategoriesForFilter() == null || viewModel.getCategoriesForFilter().size() == 0) {
            tv_categoryList_filter.setText("NO FILTER");
        } else {
            for (String category : viewModel.getCategoriesForFilter()) {
                tv_categoryList_filter.append(category + "\n");
            }
        }

        if (viewModel.getTagsForFilter() == null || viewModel.getTagsForFilter().size() == 0) {
            tv_tagList_filter.setText("NO FILTER");
        } else {
            for (String tag : viewModel.getTagsForFilter()) {
                tv_tagList_filter.append(tag + "\n");
            }
        }

        //"clear all" chips
        if (!tv_categoryList_filter.getText().toString().equals("NO FILTER")) {
            cp_clear_categories.setVisibility(View.VISIBLE);
        } else {
            cp_clear_categories.setVisibility(View.GONE);
        }

        if (!tv_tagList_filter.getText().toString().equals("NO FILTER")) {
            cp_clear_tags.setVisibility(View.VISIBLE);
        } else {
            cp_clear_tags.setVisibility(View.GONE);
        }
    }

    private void setupOnClickListeners() {
        tv_category_filter.setOnClickListener(v -> openCategoryMultiSelectDialog(categoriesList));

        tv_tag_filter.setOnClickListener(v -> openTagMultiSelectDialog(tagsList));

        btn_ok_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (categoriesForFilter == null) {
//                    categoriesForFilter = new ArrayList<>();
//                    categoriesForFilter.addAll(categoriesList);
//                }
//
//                if (tagsForFilter == null) {
//                    tagsForFilter = new ArrayList<>();
//                    tagsForFilter.addAll(tagsList);
//                }
                dataPasser.onDataPass(viewModel.getCategoriesForFilter(), viewModel.getTagsForFilter());
                FilterDialogFragment.this.dismiss();
            }
        });

        btn_cancel_filter.setOnClickListener(v -> dismiss());

        cp_clear_categories.setOnClickListener(v -> {
            tv_categoryList_filter.setText("NO FILTER");
            viewModel.setCategoriesForFilter(null);
            cp_clear_categories.setVisibility(View.GONE);
        });

        cp_clear_tags.setOnClickListener(v -> {
            tv_tagList_filter.setText("NO FILTER");
            viewModel.setTagsForFilter(null);
            cp_clear_tags.setVisibility(View.GONE);
        });
    }

    private void openCategoryMultiSelectDialog(List<String> categoriesList) {
        Dialog dialog;
        int size = categoriesList.size();
        String[] categories = categoriesList.toArray(new String[size]);

        //it will check items previously selected
        boolean[] checkedItems = new boolean[size];

        if (viewModel.getCategoriesForFilter() != null) {
            for (String categoryForFilter : viewModel.getCategoriesForFilter()) {
                for (String categoryAll : categoriesList) {
                    if (categoryForFilter.equals(categoryAll)) {
                        //checkedItemsList.set(categoriesList.indexOf(categoryAll), true);
                        int i = categoriesList.indexOf(categoryAll);
                        checkedItems[i] = true;
                    }
                }
            }
        }

        List<Integer> itemsSelected = new ArrayList<>();

        for (int i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i]) {
                itemsSelected.add(i);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMultiChoiceItems(categories, checkedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedItemId,
                                        boolean isSelected) {

                        if (isSelected) {
                            itemsSelected.add(selectedItemId);
                        } else if (itemsSelected.contains(selectedItemId)) {
                            itemsSelected.remove(Integer.valueOf(selectedItemId));
                        }

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //send itemsSelected to previous filter pop up
                        categoriesForFilter = new ArrayList<>();
                        if (itemsSelected.size() != 0) {
                            tv_categoryList_filter.setText("");

                            for (int item : itemsSelected) {
                                categoriesForFilter.add(categoriesList.get(item));
                                viewModel.setCategoriesForFilter(categoriesForFilter);
                            }
                            for (String category : viewModel.getCategoriesForFilter()) {
                                tv_categoryList_filter.append(category + "\n");
                                cp_clear_categories.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    private void openTagMultiSelectDialog(List<String> tagsList) {
        Dialog dialog;
        int size = tagsList.size();
        String[] tags = tagsList.toArray(new String[size]);

        //it will check items previously selected
        boolean[] checkedItems = new boolean[size];

        if (viewModel.getTagsForFilter() != null) {
            for (String tagForFilter : viewModel.getTagsForFilter()) {
                for (String tagAll : tagsList) {
                    if (tagForFilter.equals(tagAll)) {
                        int i = tagsList.indexOf(tagAll);
                        checkedItems[i] = true;
                    }
                }
            }
        }

        List<Integer> itemsSelected = new ArrayList<>();

        for (int i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i]) {
                itemsSelected.add(i);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMultiChoiceItems(tags, checkedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedItemId,
                                        boolean isSelected) {

                        if (isSelected) {
                            itemsSelected.add(selectedItemId);
                        } else if (itemsSelected.contains(selectedItemId)) {
                            itemsSelected.remove(Integer.valueOf(selectedItemId));
                        }

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //send itemsSelected to previous filter pop up
                        tagsForFilter = new ArrayList<>();
                        if (itemsSelected.size() != 0) {
                            tv_tagList_filter.setText("");

                            for (int item : itemsSelected) {
                                tagsForFilter.add(tagsList.get(item));
                                viewModel.setTagsForFilter(tagsForFilter);
                            }
                            for (String category : viewModel.getTagsForFilter()) {
                                tv_tagList_filter.append(category + "\n");
                                cp_clear_tags.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        dialog = builder.create();
        dialog.show();

    }

    private void setCategoryNames(List<String> categoryNames) {
        categoriesList = categoryNames;
    }

    private void setTagNames(List<String> tagNames) {
        tagsList = tagNames;
    }

    public interface OnDataPass {
        void onDataPass(List<String> dataCategories, List<String> dataTags);

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) getTargetFragment();
    }


}

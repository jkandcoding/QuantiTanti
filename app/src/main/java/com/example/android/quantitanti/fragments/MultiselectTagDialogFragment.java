package com.example.android.quantitanti.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.AppExecutors;
import com.example.android.quantitanti.R;
import com.example.android.quantitanti.adapters.TagsAdapter;
import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.database.TagEntry;
import com.example.android.quantitanti.models.TagsPojo;
import com.example.android.quantitanti.viewmodels.TagsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import static com.example.android.quantitanti.helpers.Helper.hideKeyboardFromFragment;

public class MultiselectTagDialogFragment extends DialogFragment implements TagsAdapter.ItemClickListener {

    private TagsViewModel viewModel;

    // String key for MultiSelectTagDialogFragment
    public static final String BUNDLE_TAGS = "bundleTags";

    List<TagsPojo> tagsChecked = new ArrayList<>();
    ArrayList<String> choosenTagsArray = new ArrayList<>();

    List<Integer> expansesIdsForDel = new ArrayList<>();
    String tagNameForDel;

    // private TagsAdapter tagsAdapter;
    private TagsAdapter tagsAdapter;
    private RecyclerView rv_tagRecyclerview;
    private RecyclerView.LayoutManager layoutManager;

    public CostDatabase mDb;

    private EditText et_AddATag;
    private Button btn_addTagDialog;
    private Button btn_cancelDialog;
    private Button btn_selectDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            choosenTagsArray = getArguments().getStringArrayList(BUNDLE_TAGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tag_dialog, container, false);

        rv_tagRecyclerview = v.findViewById(R.id.rv_tagRecyclerview);
        et_AddATag = v.findViewById(R.id.et_addATag);
        btn_addTagDialog = v.findViewById(R.id.btn_addTagDialog);
        btn_cancelDialog = v.findViewById(R.id.btn_cancelDialog);
        btn_selectDialog = v.findViewById(R.id.btn_selectTagDialog);


        layoutManager = new LinearLayoutManager(getContext());
        rv_tagRecyclerview.setLayoutManager(layoutManager);
        tagsAdapter = new TagsAdapter(this);
        rv_tagRecyclerview.setAdapter(tagsAdapter);

        mDb = CostDatabase.getInstance(getContext());

        makeNewTag();

        viewModel = new ViewModelProvider(this).get(TagsViewModel.class);
        //get tags from TagsViewModels LiveData:
        viewModel.getTagsForDialog().observe(getViewLifecycleOwner(), new Observer<List<TagsPojo>>() {
            @Override
            public void onChanged(List<TagsPojo> tagsForDialog) {
                //setChecked if opening dialog when updating cost which already has tags
                for (TagsPojo tag : tagsForDialog) {
                    for (String stag : choosenTagsArray) {
                        if (tag.getTagName().equals(stag)) {
                            tag.setSelected(true);
                        }
                    }
                }
                //List<TagsPojo> checked tags written in AddCostActivity after dismiss MultiSTDFragment
                tagsChecked.addAll(tagsForDialog);
                tagsAdapter.setTags(tagsForDialog);
            }
        });

        dismissMultiSelectDialog();
        return v;
    }

    private void makeNewTag() {
        btn_addTagDialog.setOnClickListener(v -> {
            final String newTag = et_AddATag.getText().toString().trim();
            if (TextUtils.isEmpty(newTag)) {
                Toast.makeText(getContext(), "Add a new tag", Toast.LENGTH_SHORT).show();
                return;
            }

            final TagEntry tagEntry = new TagEntry(newTag);
            AppExecutors.getInstance().diskIO().execute(() -> {
                // check if tag with that name already exists
                int tagExists = mDb.tagsDao().tagExists(newTag);
                if (tagExists == 1) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Tag \"" + newTag + "\" already exists", Toast.LENGTH_SHORT).show());
                } else if (tagExists == 0) {
                    mDb.tagsDao().insertTag(tagEntry);
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Tag \"" + newTag + "\" added", Toast.LENGTH_SHORT).show());

                }
            });

            et_AddATag.setText("");

            hideKeyboardFromFragment(requireContext(), v);
        });
    }

    @Override
    public void onItemLongClickListener(int tagIdForDel) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AppExecutors.getInstance().diskIO().execute(() -> {
            expansesIdsForDel = mDb.expenses_tags_join_dao().loadExpenseIdsForTag(tagIdForDel);
            tagNameForDel = mDb.tagsDao().loadTagName(tagIdForDel);
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await();
            //if tag has no costs -> delete tag:
            if (expansesIdsForDel == null || expansesIdsForDel.isEmpty()) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    mDb.tagsDao().deleteTagWithId(tagIdForDel);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireActivity(), "Tag \"" + tagNameForDel + "\" deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                //if tag is asigned to costs:
            } else {
               deletingTagasignedToCosts(tagIdForDel);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void deletingTagasignedToCosts(int tagIdForDel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.okvir_za_datum_warning);
        builder.setTitle("Deleting tag...");
        builder.setMessage("This tag is already assigned to some costs. Do you want to delete these costs entirely, or remove just the tag from them?");
        builder.setPositiveButton("Delete costs", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    //deleting costs entirely(tocke 1-4)
                    mDb.expenses_tags_join_dao().deleteWithTagId(tagIdForDel);  // 1
                    for (int costId : expansesIdsForDel) {
                        mDb.picsDao().deletePicForCostWithCostId(costId);   // 2
                        mDb.costDao().deleteCostWithId(costId);   // 3
                    }
                    mDb.tagsDao().deleteTagWithId(tagIdForDel);  // 4

                    requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Tag \"" + tagNameForDel + "\" deleted, \nrelated cost(s) deleted", Toast.LENGTH_LONG).show());
                });
            }
        });
        builder.setNegativeButton("Remove tag", (dialog, which) -> AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                //removing just the tag from costs (tocke 1-2)
                mDb.expenses_tags_join_dao().deleteWithTagId(tagIdForDel);  // 1
                mDb.tagsDao().deleteTagWithId(tagIdForDel);                 // 2

                requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Tag \"" + tagNameForDel + "\" deleted and removed from costs", Toast.LENGTH_LONG).show());
            }
        }));
        builder.setNeutralButton("Cancel", (dialog, which) -> {
            // back to MultiSTDFragment dialog
        });
        builder.create();
        builder.show();
    }

    public interface OnDataPass {
        void onDataPass(List<String> data);
    }

    private OnDataPass dataPasser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    private void
    dismissMultiSelectDialog() {
        btn_cancelDialog.setOnClickListener(v -> dismiss());

        btn_selectDialog.setOnClickListener(v -> {
            List<String> selectedTags = new ArrayList<>();
            for (TagsPojo selTag : tagsChecked) {
                if (selTag.isSelected()) {
                    selectedTags.add(selTag.getTagName());
                }
            }
            if (selectedTags.isEmpty()) {
                dismiss();
            } else {
                dataPasser.onDataPass(selectedTags);
                dismiss();
            }
        });
    }
}

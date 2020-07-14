package com.example.android.quantitanti.fragments;

import android.content.Context;
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
import com.example.android.quantitanti.models.Tags;
import com.example.android.quantitanti.viewmodels.TagsViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.quantitanti.helpers.Helper.hideKeyboardFromFragment;

public class MultiselectTagDialogFragment extends DialogFragment {

    private TagsViewModel viewModel;

    // String key for MultiSelectTagDialogFragment
    public static final String BUNDLE_TAGS = "bundleTags";

    List<Tags> tags = new ArrayList<>();
    List<String> tagNames = new ArrayList<>();
    ArrayList<String> choosenTagsArray = new ArrayList<>();

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
        tagsAdapter = new TagsAdapter(tags);
        rv_tagRecyclerview.setAdapter(tagsAdapter);

        mDb = CostDatabase.getInstance(getContext());

        makeNewTag();

        viewModel = new ViewModelProvider(this).get(TagsViewModel.class);
        //get tags from TagsViewModels LiveData:
        viewModel.getTagNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                //       viewModel.getTagNames().removeObserver(this);

                tagNames.clear();
                tagNames.addAll(strings);
                fillTagObj();

            }
        });

        dismissMultiSelectDialog();
        return v;

    }

    // 1) iz baze izvucem tag 2) spremim ga u List<String> tagNames
    // 3) iz te liste spremim u Tags.class koja pomocu adaptera puni dialog
    private void fillTagObj() {
        tags.clear();
        for (String s : tagNames) {
            Tags tag = new Tags(s);
            tag.setTagName(s); //object List<Tags> tag
            for (String stag : choosenTagsArray) {
                if (tag.getTagName().equals(stag)) {
                    tag.setSelected(true);
                }
            }
            tags.add(tag);      //lista List<Tags> tags
            Log.d(String.valueOf(tag), "tagsss");

        }
    }

    private void makeNewTag() {
        btn_addTagDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newTag = et_AddATag.getText().toString().trim();
                if (TextUtils.isEmpty(newTag)) {
                    Toast.makeText(getContext(), "Add a new tag", Toast.LENGTH_SHORT).show();
                    return;
                }

                final TagEntry tagEntry = new TagEntry(newTag);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.tagsDao().insertTag(tagEntry);
                        Log.d(String.valueOf(tagEntry), "tagEntry");
                    }
                });

                et_AddATag.setText("");
                Toast.makeText(getContext(), "Tag \"" + newTag + "\" added", Toast.LENGTH_SHORT).show();
                hideKeyboardFromFragment(getContext(), v);


            }
        });
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

    private void dismissMultiSelectDialog() {
        btn_cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btn_selectDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> selectedTags = new ArrayList<>();
                for (Tags selTag : tags) {
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
            }
        });
    }

}

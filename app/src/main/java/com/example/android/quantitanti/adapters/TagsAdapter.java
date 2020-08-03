package com.example.android.quantitanti.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.R;
import com.example.android.quantitanti.models.TagsPojo;

import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagViewHolder> {

    private List<TagsPojo> mTags;

    public TagsAdapter() {

    }


    @NonNull
    @Override
    public TagsAdapter.TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout to the view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_row_dialog, parent, false);

        return new TagViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull TagsAdapter.TagViewHolder holder, int position) {
        TagsPojo tagsPojo = mTags.get(position);

        holder.tagName.setText(tagsPojo.getTagName());
        holder.chk.setChecked(tagsPojo.isSelected());
        holder.chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagsPojo.setSelected(!tagsPojo.isSelected());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mTags == null) {
            return 0;
        }
        return mTags.size();
    }

    //ne treba mi
//    public List<TagsPojo> getTags() {
//        return mTags;
//    }

    public void setTags(List<TagsPojo> tags) {
        mTags = tags;
        notifyDataSetChanged();
    }

    public class TagViewHolder extends RecyclerView.ViewHolder {

        private TextView tagName;
        private CheckBox chk;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagName = itemView.findViewById(R.id.tv_tagName_tagRowDialog);
            chk = itemView.findViewById(R.id.cb_tagRowDialog);
        }
    }
}





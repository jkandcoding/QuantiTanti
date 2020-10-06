package com.example.android.quantitanti.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.R;
import com.example.android.quantitanti.models.TagsPojo;

import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagViewHolder> {

    private List<TagsPojo> mTags;
    final private ItemClickListener mItemClickListener;

    public TagsAdapter(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
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


    public void setTags(List<TagsPojo> tags) {
        mTags = tags;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemLongClickListener(int tagIdforDel);
    }

    public class TagViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private TextView tagName;
        private CheckBox chk;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagName = itemView.findViewById(R.id.tv_tagName_tagRowDialog);
            chk = itemView.findViewById(R.id.cb_tagRowDialog);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            int tagIdforDel = mTags.get(getAbsoluteAdapterPosition()).getTagId();
            mItemClickListener.onItemLongClickListener(tagIdforDel);
            Log.d(String.valueOf(tagIdforDel), "tagId");
            return true;
        }
    }
}





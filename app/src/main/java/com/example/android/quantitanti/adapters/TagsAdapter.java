package com.example.android.quantitanti.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.R;
import com.example.android.quantitanti.models.Tags;

import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagViewHolder> {

    private List<Tags> mTags;

    public TagsAdapter(List<Tags> tags) {
        this.mTags = tags;
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
        Tags tags = mTags.get(position);

        holder.tagName.setText(tags.getTagName());
        holder.chk.setChecked(tags.isSelected());
        holder.chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tags.setSelected(!tags.isSelected());
            }
        });
    }

//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    @Override
    public int getItemCount() {
        if (mTags == null) {
            return 0;
        }
        return mTags.size();
    }

    //ne treba mi
//    public List<Tags> getTags() {
//        return mTags;
//    }

    public void setTags(List<Tags> tags) {
        mTags = tags;
        notifyDataSetChanged();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.tag_row_dialog, null);
//            holder = new ViewHolder();
//            holder.tagName = (TextView) convertView.findViewById(R.id.tv_tagName_tagRowDialog);
//            holder.chk = (CheckBox) convertView.findViewById(R.id.cb_tagRowDialog);
//            holder.chk
//                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//                        @Override
//                        public void onCheckedChanged(CompoundButton view,
//                                                     boolean isChecked) {
//                            int getPosition = (Integer) view.getTag();
//                            tags.get(getPosition).setSelected(view.isChecked());
//
//                        }
//                    });
//            convertView.setTag(holder);
//            convertView.setTag(R.id.title, holder.tagName);
//            convertView.setTag(R.id.checkbox, holder.chk);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        holder.chk.setTag(position);
//
//        holder.tagName.setText(tags.get(position).getTagName());
//        holder.chk.setChecked(tags.get(position).isSelected());
//
//        return convertView;
//    }

//    static class ViewHolder {
//        private TextView tagName;
//        private CheckBox chk;
//    }


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





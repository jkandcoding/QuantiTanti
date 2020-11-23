package com.example.android.quantitanti.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android.quantitanti.R;
import com.example.android.quantitanti.helpers.Helper;
import com.example.android.quantitanti.models.TotalCostPojo;
import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class ScreenSlidePagerAdapter extends RecyclerView.Adapter<ScreenSlidePagerAdapter.SlideViewHolder> {

    private List<TotalCostPojo> mTotalCostPojos;
    private LayoutInflater layoutInflater;
    private ViewPager2 viewPager2;
    private int mHeight;

    public ScreenSlidePagerAdapter(Context context, ViewPager2 viewPager2) {
        this.layoutInflater = LayoutInflater.from(context);
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.total_category_costs_view, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        TotalCostPojo totalCostPojo = mTotalCostPojos.get(position);

        String currency = totalCostPojo.getCurrency();

        //setting categories and their costs into Map:
        Map<String, Integer> categoryCosts = totalCostPojo.getCategoryCosts();

        //get TotalCost for holder.tv_total_cost:
        int totalCost = 0;
        for (Map.Entry<String, Integer> entry : categoryCosts.entrySet()) {
            totalCost += entry.getValue();
        }

        //      int hight = mViewPager2MaxHight.get(position);
        holder.tv_category_costs.setText("");
        holder.tv_category_costs.setMinLines(mHeight);
        if (mHeight > totalCostPojo.getCategoryCosts().size()) {
            int newLineNumber = mHeight - totalCostPojo.getCategoryCosts().size();
            holder.tv_category_costs.setText(new String(new char[newLineNumber]).replace("\0", "\n"));
        }

        Map.Entry<String, Integer> lastEntry = ((TreeMap<String, Integer>) categoryCosts).lastEntry();
        for (Map.Entry<String, Integer> entry : categoryCosts.entrySet()) {
            if ((entry.getKey() + "=" + entry.getValue()).equals(lastEntry.toString())) {

                holder.tv_category_costs.append(entry.getKey() + ": " + Helper.fromIntToDecimalString(entry.getValue()) + " " + currency);
            } else {
                holder.tv_category_costs.append(entry.getKey() + ": " + Helper.fromIntToDecimalString(entry.getValue()) + " " + currency + "\n");
            }
        }

        // total cost
        if (totalCost < 0) {
            holder.tv_total_cost.setText(": > 21 474 836.47 ");
            holder.cp_categoryCurrency.setText(currency);
        } else {
            holder.tv_total_cost.setText(": " + Helper.fromIntToDecimalString(totalCost));
            holder.cp_categoryCurrency.setText(currency);
        }
    }


    @Override
    public int getItemCount() {
        if (mTotalCostPojos == null) {
            return 0;
        }
        return mTotalCostPojos.size();
    }

    /**
     * When data changes, this method updates the list of costEntries
     * and notifies the adapter to use the new values on it
     */
    public void setmDailyCategoryCosts(List<TotalCostPojo> totalCostPojos, int height) {
        mTotalCostPojos = totalCostPojos;
        mHeight = height;
        notifyDataSetChanged();
    }

    public static class SlideViewHolder extends RecyclerView.ViewHolder {

        TextView tv_category_costs;
        TextView tv_total_cost;
        Chip cp_categoryCurrency;
        RelativeLayout rl_totalCost_slide;

        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category_costs = itemView.findViewById(R.id.tv_category_costs);
            tv_total_cost = itemView.findViewById(R.id.tv_total_cost);
            cp_categoryCurrency = itemView.findViewById(R.id.cp_categoryCurrency);
            rl_totalCost_slide = itemView.findViewById(R.id.totalCost_slide);
        }
    }
}

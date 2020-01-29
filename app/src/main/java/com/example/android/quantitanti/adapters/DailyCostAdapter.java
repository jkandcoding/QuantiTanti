package com.example.android.quantitanti.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.R;
import com.example.android.quantitanti.database.CostEntry;
import com.example.android.quantitanti.database.DailyExpensesView;
import com.example.android.quantitanti.helpers.Helper;

import java.util.List;

import static com.example.android.quantitanti.database.CostEntry.CATEGORY_1;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_2;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_3;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_4;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_5;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_6;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_7;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_8;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_9;

public class DailyCostAdapter extends RecyclerView.Adapter<DailyCostAdapter.DailyCostViewHolder> {

    // Member variable to handle item clicks
    final private DailyItemClickListener mDailyItemClickListener;

    // Class variables for the List that holds cost data and the Context
    private List<CostEntry> mCostEntries;
    private Context mContext;

    //private ImageView imgv_category;

    public DailyCostAdapter(DailyItemClickListener listener, Context context) {
        mDailyItemClickListener = listener;
        mContext = context;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new DailyCostViewHolder that holds the view for daily costs
     */
    @NonNull
    @Override
    public DailyCostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout to the view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.one_cost_item_view, parent, false);

        return new DailyCostViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(@NonNull DailyCostViewHolder holder, int position) {
        // Determine the values of the wanted data
        CostEntry costEntry = mCostEntries.get(position);

        // pojedinacan trosak na datum
        String oneCostCategory = costEntry.getCategory();
        String oneCostName = costEntry.getName();
        int oneCostValue = costEntry.getCost();
        String oneCostValueString = Helper.fromIntToDecimalString(oneCostValue);

        //Set values
        holder.tv_costDescription.setText(oneCostName);
        holder.tv_costValue.setText(oneCostValueString + " kn");

        // setting imgv depending on category
            switch (oneCostCategory) {
                case CATEGORY_1:
                    holder.imgv_category.setBackgroundResource(R.drawable.car);
                    break;
                case CATEGORY_2:
                    holder.imgv_category.setBackgroundResource(R.drawable.clothes);
                    break;
                case CATEGORY_3:
                    holder.imgv_category.setBackgroundResource(R.drawable.food);
                    break;
                case CATEGORY_4:
                    holder.imgv_category.setBackgroundResource(R.drawable.utilities);
                    break;
                case CATEGORY_5:
                    holder.imgv_category.setBackgroundResource(R.drawable.groceries);
                    break;
                case CATEGORY_6:
                    holder.imgv_category.setBackgroundResource(R.drawable.education);
                    break;
                case CATEGORY_7:
                    holder.imgv_category.setBackgroundResource(R.drawable.sport);
                    break;
               case CATEGORY_8:
                   holder.imgv_category.setBackgroundResource(R.drawable.cosmetics);
                   break;
               case CATEGORY_9:
                   holder.imgv_category.setBackgroundResource(R.drawable.other);
                   break;
                default:
                    break;
            }
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCostEntries == null) {
            return 0;
        }
        return mCostEntries.size();
    }

    public List<CostEntry> getDailyCosts() {
        return mCostEntries;
    }

    /**
     * When data changes, this method updates the list of costEntries
     * and notifies the adapter to use the new values on it
     */
    public void setmDailyCosts(List<CostEntry> costEntries) {
        mCostEntries = costEntries;
        notifyDataSetChanged();
    }

    public interface DailyItemClickListener {
        void onDailyItemClickListener(int itemId);
    }

    

    public class DailyCostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imgv_category;
        TextView tv_costDescription;
        TextView tv_costValue;

        /**
         * Constructor for the DailyCostViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public DailyCostViewHolder(@NonNull View itemView) {
            super(itemView);
            // TextViews for itemView
            imgv_category = itemView.findViewById(R.id.imgv_category);
            tv_costDescription = itemView.findViewById(R.id.tv_costDescription);
            tv_costValue = itemView.findViewById(R.id.tv_costValue);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int elementId = mCostEntries.get(getAdapterPosition()).getId();
            mDailyItemClickListener.onDailyItemClickListener(elementId);
        }
    }

}

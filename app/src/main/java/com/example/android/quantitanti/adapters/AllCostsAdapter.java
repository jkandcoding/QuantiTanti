package com.example.android.quantitanti.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.R;
import com.example.android.quantitanti.database.PicsEntry;
import com.example.android.quantitanti.fragments.PhotosDialogFragment;
import com.example.android.quantitanti.helpers.Helper;
import com.example.android.quantitanti.models.DailyExpenseTagsWithPicsPojo;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.threeten.bp.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.android.quantitanti.database.CostEntry.CATEGORY_1;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_2;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_3;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_4;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_5;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_6;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_7;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_8;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_9;
import static com.example.android.quantitanti.database.CostEntry.CURRENCY_1;
import static com.example.android.quantitanti.database.CostEntry.CURRENCY_2;
import static com.example.android.quantitanti.database.CostEntry.CURRENCY_3;
import static com.example.android.quantitanti.database.CostEntry.CURRENCY_4;
import static java.lang.String.valueOf;

public class AllCostsAdapter extends RecyclerView.Adapter<AllCostsAdapter.AllCostsViewHolder> {

    private static String currency1;
    private static String currency2;

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;

    // Class variables for the List that holds cost data and the Context
    private List<DailyExpenseTagsWithPicsPojo> mAllCosts;
    private Context mContext;

    public AllCostsAdapter(ItemClickListener listener, Context context) {
        mItemClickListener = listener;
        mContext = context;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new AllCostsViewHolder that holds the view for daily cost
     */
    @NonNull
    @Override
    public AllCostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout to the view
        View view = LayoutInflater.from(mContext).inflate(R.layout.all_costs_item_view, parent, false);
        return new AllCostsViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AllCostsViewHolder holder, int position) {
    DailyExpenseTagsWithPicsPojo allCostsItem = mAllCosts.get(position);
        String date_No = valueOf(LocalDate.parse(allCostsItem.getCostEntry().getDate()).getDayOfMonth());
        String month = Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(allCostsItem.getCostEntry().getDate()).getMonth().toString());
        String year = valueOf(LocalDate.parse(allCostsItem.getCostEntry().getDate()).getYear());
        String oneCostCategory = allCostsItem.getCostEntry().getCategory();
        String oneCostName = allCostsItem.getCostEntry().getName();
        int oneCostValue = allCostsItem.getCostEntry().getCost();
        String oneCostValueString = Helper.fromIntToDecimalString(oneCostValue);
        String currency = allCostsItem.getCostEntry().getCurrency();
        List<String> oneCostTags = allCostsItem.getTagNames();
        List<PicsEntry> picsEntries = allCostsItem.getPicsEntries();

        Map<String, String> photos = new HashMap<>();
        for (PicsEntry picsEntry : picsEntries) {
            photos.put(picsEntry.getPic_name(), picsEntry.getPic_uri());
        }

        if (!photos.isEmpty()) {
            holder.iv_getPic_all.setVisibility(View.VISIBLE);
            holder.iv_getPic_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    PhotosDialogFragment photosDialogFragment = PhotosDialogFragment.newInstance(photos);
                    photosDialogFragment.show(fm, "fragment_photo");
                }
            });
        }

        //set currency from DB
        if (currency != null) {         //todo brisi ovaj redak kada se iz baze izbrisu troskovi s null-avim currencyem
            switch (currency) {
                case CURRENCY_1:
                    currency1 = "";
                    currency2 = " kn";
                    break;
                case CURRENCY_2:
                    currency1 = "";
                    currency2 = " €";
                    break;
                case CURRENCY_3:
                    currency1 = "£";
                    currency2 = "";
                    break;
                case CURRENCY_4:
                    currency1 = "$";
                    currency2 = "";
                    break;
            }
        } else {
            currency1 = "?";
            currency2 = " ?";
        }

        //Set holder values
        holder.tv_costDescription_all.setText(oneCostName);
        holder.tv_costValue_all.setText(currency1 + oneCostValueString + currency2);
        holder.cg_tags_all.removeAllViews();
        if (oneCostTags != null) {
            for (String s : oneCostTags) {
                Chip chip = new Chip(mContext);
                chip.setText(s);
                chip.setClickable(false);
                holder.cg_tags_all.addView(chip);
            }
        }

        // setting imgv depending on category
        switch (oneCostCategory) {
            case CATEGORY_1:
                holder.imgv_category_all.setBackgroundResource(R.drawable.car);
                break;
            case CATEGORY_2:
                holder.imgv_category_all.setBackgroundResource(R.drawable.clothes);
                break;
            case CATEGORY_3:
                holder.imgv_category_all.setBackgroundResource(R.drawable.food);
                break;
            case CATEGORY_4:
                holder.imgv_category_all.setBackgroundResource(R.drawable.utilities);
                break;
            case CATEGORY_5:
                holder.imgv_category_all.setBackgroundResource(R.drawable.groceries);
                break;
            case CATEGORY_6:
                holder.imgv_category_all.setBackgroundResource(R.drawable.education);
                break;
            case CATEGORY_7:
                holder.imgv_category_all.setBackgroundResource(R.drawable.sport);
                break;
            case CATEGORY_8:
                holder.imgv_category_all.setBackgroundResource(R.drawable.cosmetics);
                break;
            case CATEGORY_9:
                holder.imgv_category_all.setBackgroundResource(R.drawable.other);
                break;
            default:
                break;
        }

        holder.tv_date_for_frontPage_all.setText(date_No + " " + month + ", " + year);

        //date, month & year -> grouping items
        if (position < mAllCosts.size() - 1) {
            if (LocalDate.parse(mAllCosts.get(position).getCostEntry().getDate())
                    .equals(LocalDate.parse(mAllCosts.get(position + 1).getCostEntry().getDate()))
                     ) {
                holder.tv_date_for_frontPage_all.setVisibility(View.GONE);
            } else {
                holder.tv_date_for_frontPage_all.setVisibility(View.VISIBLE);
            }
        } else {
            holder.tv_date_for_frontPage_all.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mAllCosts == null) {
            return 0;
        }
        return mAllCosts.size();
    }

    public List<DailyExpenseTagsWithPicsPojo> getmAllCosts() {
        return mAllCosts;
    }

    public void setAllCost(List<DailyExpenseTagsWithPicsPojo> allCosts) {
        mAllCosts = allCosts;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
        void onItemLongClickListener(int itemId);
    }

    public class AllCostsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView tv_date_for_frontPage_all;
        ImageView imgv_category_all;
        TextView tv_costDescription_all;
        TextView tv_costValue_all;
        ChipGroup cg_tags_all;
        ImageView iv_getPic_all;

        public AllCostsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date_for_frontPage_all = itemView.findViewById(R.id.tv_date_for_frontPage_all);
            imgv_category_all = itemView.findViewById(R.id.imgv_category_all);
            tv_costDescription_all = itemView.findViewById(R.id.tv_costDescription_all);
            tv_costValue_all = itemView.findViewById(R.id.tv_costValue_all);
            cg_tags_all = itemView.findViewById(R.id.cg_tags_all);
            iv_getPic_all = itemView.findViewById(R.id.iv_getPic_all);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int elementId = mAllCosts.get(getAbsoluteAdapterPosition()).getCostEntry().getId();
            mItemClickListener.onItemClickListener(elementId);
        }

        @Override
        public boolean onLongClick(View v) {
            int elementId = mAllCosts.get(getAbsoluteAdapterPosition()).getCostEntry().getId();
            mItemClickListener.onItemLongClickListener(elementId);
            return true;
        }
    }
}

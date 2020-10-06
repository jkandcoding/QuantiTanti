package com.example.android.quantitanti.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.R;
import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.helpers.Helper;
import com.example.android.quantitanti.models.TotalFrontCostPojo;

import org.threeten.bp.LocalDate;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;
import static java.lang.String.valueOf;

//Adapter for CostListActivity
public class CostAdapter extends RecyclerView.Adapter<CostAdapter.CostViewHolder> {

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;

    // Class variables for the List that holds cost data and the Context
    private List<TotalFrontCostPojo> mCostEntries;
    private Context mContext;
    private CostDatabase mDb;

    public CostAdapter(ItemClickListener listener, Context context) {
        mItemClickListener = listener;
        mContext = context;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new CostViewHolder that holds the view for daily cost
     */
    @NonNull
    @Override
    public CostAdapter.CostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout to the view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.cost_layout, parent, false);

        return new CostViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final CostAdapter.CostViewHolder holder, int position) {
        // Determine the values of the wanted data
        TotalFrontCostPojo totalFrontCostPojo = mCostEntries.get(position);
        String dateExpense = totalFrontCostPojo.getDate();
        String week_day = Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(totalFrontCostPojo.getDate()).getDayOfWeek().toString());
        String date_No = valueOf(LocalDate.parse(totalFrontCostPojo.getDate()).getDayOfMonth());
        String month = Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(totalFrontCostPojo.getDate()).getMonth().toString());
        String year = valueOf(LocalDate.parse(totalFrontCostPojo.getDate()).getYear());

        Map<String, Integer> totalCost = totalFrontCostPojo.getFrontCosts();

        holder.tv_weekDay.setText(week_day);
        holder.tv_dateNo.setText(date_No);

        holder.tv_mainCost.setText("");
        Map.Entry<String, Integer> lastEntry = ((TreeMap<String, Integer>) totalCost).lastEntry();
        for (Map.Entry<String, Integer> entry : totalCost.entrySet()) {
            Spannable sp = new SpannableString(entry.getKey());
            sp.setSpan(new StyleSpan(Typeface.BOLD), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if ((entry.getKey() + "=" + entry.getValue()).equals(lastEntry.toString())) {
                if (entry.getValue() < 0) {
                   // holder.tv_mainCost.setText(entry.getKey() + " to much");
                    holder.tv_mainCost.setText(sp);
                    holder.tv_mainCost.append(" to much");
                } else {
                   // holder.tv_mainCost.append(entry.getKey() + " " + Helper.fromIntToDecimalString(entry.getValue()));
                    holder.tv_mainCost.append(sp, 0, sp.length());
                    holder.tv_mainCost.append(" " + Helper.fromIntToDecimalString(entry.getValue()));
                }
            } else {
                if (entry.getValue() < 0) {
                   // holder.tv_mainCost.append(entry.getKey() + " to much '\n' ");
                    holder.tv_mainCost.append(sp, 0, sp.length());
                    holder.tv_mainCost.append(" to much '\n' ");
                } else {
                    //holder.tv_mainCost.append(sp + " " + Helper.fromIntToDecimalString(entry.getValue()) + '\n');
                    holder.tv_mainCost.append(sp, 0, sp.length());
                    holder.tv_mainCost.append(" " + Helper.fromIntToDecimalString(entry.getValue()) + '\n');

                }
            }
        }

        holder.tv_date_for_frontPage.setText(month + ", " + year);

        //month & year -> grouping items
        if (position < mCostEntries.size() - 1) {
            if (LocalDate.parse(mCostEntries.get(position).getDate()).getMonth()
                    .equals(LocalDate.parse(mCostEntries.get(position + 1).getDate()).getMonth())
                    && valueOf(LocalDate.parse(mCostEntries.get(position).getDate()).getYear())
                    .equals(valueOf(LocalDate.parse(mCostEntries.get(position + 1).getDate()).getYear()))) {
                holder.tv_date_for_frontPage.setVisibility(View.GONE);
            } else {
                holder.tv_date_for_frontPage.setVisibility(View.VISIBLE);
            }
        } else {
            holder.tv_date_for_frontPage.setVisibility(View.VISIBLE);
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

    public List<TotalFrontCostPojo> getDailyExpenses() {
        return mCostEntries;
    }

    /**
     * When data changes, this method updates the list of dailyexpenses
     * and notifies the adapter to use the new values on it
     *
     * @param totalFrontCostPojos
     */
    public void setmDailyExpenses(List<TotalFrontCostPojo> totalFrontCostPojos) {
        mCostEntries = totalFrontCostPojos;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(String itemDate);

        void onItemLongClickListener(String itemDate);
    }


    // Inner class for creating ViewHolders
    class CostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        // Class variables for the date and daily cost
        TextView tv_weekDay;
        TextView tv_dateNo;
        TextView tv_mainCost;
        TextView tv_date_for_frontPage;

        /**
         * Constructor for the CostViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public CostViewHolder(@NonNull View itemView) {
            super(itemView);
            // TextViews for itemView
            tv_weekDay = itemView.findViewById(R.id.tv_weekDay);
            tv_dateNo = itemView.findViewById(R.id.tv_dateNo);
            tv_mainCost = itemView.findViewById(R.id.tv_mainCost);
            tv_date_for_frontPage = itemView.findViewById(R.id.tv_date_for_frontPage);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String elementDate = mCostEntries.get(getAbsoluteAdapterPosition()).getDate();
            mItemClickListener.onItemClickListener(elementDate);
        }

        @Override
        public boolean onLongClick(View v) {
            String elementDate = mCostEntries.get(getAbsoluteAdapterPosition()).getDate();
            mItemClickListener.onItemLongClickListener(elementDate);
            return true;
        }
    }

}

package com.example.android.quantitanti.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.AppExecutors;
import com.example.android.quantitanti.R;
import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.database.CostEntry;
import com.example.android.quantitanti.helpers.Helper;

import org.threeten.bp.LocalDate;

import java.util.List;

import static com.example.android.quantitanti.CostListActivity.currency1;
import static com.example.android.quantitanti.CostListActivity.currency2;
import static java.lang.String.valueOf;

//Adapter for CostListActivity
public class CostAdapter extends RecyclerView.Adapter<CostAdapter.CostViewHolder>  {

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;

    // Class variables for the List that holds cost data and the Context
   // private List<DailyExpensesView> mDailyExpenses;
    private List<CostEntry> mCostEntries;
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
    @Override
    public void onBindViewHolder(@NonNull final CostAdapter.CostViewHolder holder, int position) {
        // Determine the values of the wanted data
       // DailyExpensesView dailyExpens = mDailyExpenses.get(position);
        final CostEntry costEntry = mCostEntries.get(position);
        final String dateExpense = costEntry.getDate();


        final String week_day = Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(costEntry.getDate()).getDayOfWeek().toString());
        final String date_No = valueOf(LocalDate.parse(costEntry.getDate()).getDayOfMonth());
        final String month = Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(costEntry.getDate()).getMonth().toString());
        final String year = valueOf(LocalDate.parse(costEntry.getDate()).getYear());

        //daily cost:
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb = CostDatabase.getInstance(mContext);
                //todo ispravi valutu (dodaj varijablu currency)
                final int dailyCost = mDb.costDao().loadTotalCost(dateExpense, "kn");

                final String mainCostString = Helper.fromIntToDecimalString(dailyCost);

                // Set values
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        holder.tv_weekDay.setText(week_day);
                        holder.tv_dateNo.setText(date_No);

                        if (dailyCost < 0) {
                            holder.tv_mainCost.setText("> " + currency1 + "21 474 836,47 " + currency2);
                        } else {
                            holder.tv_mainCost.setText(currency1 + mainCostString + currency2);
                        }
                            holder.tv_date_for_frontPage.setText(month + ", " + year);
                    }
                });
            }
        });

        //month & year -> grouping items
        if (position > 0) {
            if (LocalDate.parse(mCostEntries.get(position).getDate()).getMonth()
                    .equals(LocalDate.parse(mCostEntries.get(position - 1).getDate()).getMonth())
                    && valueOf(LocalDate.parse(mCostEntries.get(position).getDate()).getYear())
                    .equals(valueOf(LocalDate.parse(mCostEntries.get(position - 1).getDate()).getYear())) ) {
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

    public List<CostEntry> getDailyExpenses() {
        return mCostEntries;
    }

    /**
     * When data changes, this method updates the list of dailyexpenses
     * and notifies the adapter to use the new values on it
     */
    public void setmDailyExpenses(List<CostEntry> costEntries) {
        mCostEntries = costEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(String itemDate);
    }

    // Inner class for creating ViewHolders
    class CostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        }

        @Override
        public void onClick(View v) {
            String elementDate = mCostEntries.get(getAdapterPosition()).getDate();
            mItemClickListener.onItemClickListener(elementDate);
        }
    }

}

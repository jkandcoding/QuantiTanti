package com.example.android.quantitanti;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.adapters.DailyCostAdapter;
import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.database.CostEntry;
import com.example.android.quantitanti.factories.DailyExpensesViewModelFactory;
import com.example.android.quantitanti.helpers.Helper;
import com.example.android.quantitanti.models.DailyExpensesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.example.android.quantitanti.database.CostEntry.CATEGORY_1;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_2;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_3;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_4;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_5;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_6;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_7;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_8;
import static com.example.android.quantitanti.database.CostEntry.CATEGORY_9;
import static java.lang.String.valueOf;

public class DailyExpensesActivity extends AppCompatActivity implements DailyCostAdapter.DailyItemClickListener{

    // Constant for logging
    private static final String TAG = CostListActivity.class.getSimpleName();

    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private DailyCostAdapter mAdapter;

    private CostDatabase mDb;

    // Extra for the cost ID to be received after rotation
    public static final String INSTANCE_COST_ID = "instanceCostId";
    // Constant for default cost id to be used when not in update mode
    private static final int DEFAULT_COST_ID = -1;
    private int mCostId = DEFAULT_COST_ID;

    // Constant for default cost id to be used when not in update mode
    private static final String DEFAULT_COST_DATE = "2020-01-01";
    private String mCostDate = DEFAULT_COST_DATE;
    // Extra for the cost date to be received in the intent
    public static final String EXTRA_COST_DATE = "extraCostDate";
    //calender
    TextView tv_weekDay;
    TextView tv_dateNo;
    TextView tv_toolbar_mont_year;

    //sum category costs
    TextView tv_category_costs;
    TextView tv_total_cost;
    int totalCost;
    int category1Cost;
    int category2Cost;
    int category3Cost;
    int category4Cost;
    int category5Cost;
    int category6Cost;
    int category7Cost;
    int category8Cost;
    int category9Cost;

    String valueString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_daily_expenses);

        mDb = CostDatabase.getInstance(getApplicationContext());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_de);
        setSupportActionBar(myToolbar);
        setTitle("Selected");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set calender on Toolbar
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View calender = inflater.inflate(R.layout.calender_view, null);
            myToolbar.addView(calender);
         // set TextView on Toolbar
            LayoutInflater inflater2 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View monthYear = inflater2.inflate(R.layout.tv_toolbar_montyear, null);
            myToolbar.addView(monthYear);

        mRecyclerView = findViewById(R.id.recyclerViewDailyCost);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new DailyCostAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        //receiving intent when onItemClickListener in CostListActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_COST_DATE)) {
            if (mCostDate == DEFAULT_COST_DATE) {
                mCostDate = intent.getStringExtra(EXTRA_COST_DATE);

                calenderDateSetting();

                DailyExpensesViewModelFactory factory = new DailyExpensesViewModelFactory(mDb, mCostDate);

                final DailyExpensesViewModel viewModel = ViewModelProviders.of(this, factory).get(DailyExpensesViewModel.class);

                viewModel.getCosts().observe(this, new Observer<List<CostEntry>>() {
                    @Override
                    public void onChanged(List<CostEntry> costEntries) {
                        mAdapter.setmDailyCosts(costEntries);
                    }
                });
            }
        }



        /**
         * Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         * An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         * and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                // Here is where you'll implement swipe to delete
                AlertDialog.Builder builder = new AlertDialog.Builder(DailyExpensesActivity.this, R.style.AlertDialogStyle);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.okvir_za_datum_warning);
                builder.setTitle("Warning");
                builder.setMessage("Are you sure you want to delete this cost?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Get the position of the item to be deleted
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                int position = viewHolder.getAdapterPosition();
                                List<CostEntry> costEntries = mAdapter.getDailyCosts();
                                //costEntries -> all costs from RecyclerView on certain date
                                mDb.costDao().deleteCost(costEntries.get(position));
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog,
                        // so we will refresh the adapter to prevent hiding the item from UI
                        mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                });
                builder.create();
                builder.show();



            }
        }).attachToRecyclerView(mRecyclerView);

        /**
         * Set the Floating Action Button (FAB) to its corresponding View.
         * Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         * to launch the AddCostActivity.
         */
        FloatingActionButton fab = findViewById(R.id.fabDaily);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailyExpensesActivity.this, AddCostActivity.class);
                intent.putExtra(EXTRA_COST_DATE, mCostDate);
                startActivity(intent);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(AddCostActivity.INSTANCE_COST_ID)) {
            mCostId = savedInstanceState.getInt(AddCostActivity.INSTANCE_COST_ID, DEFAULT_COST_ID);
        }
        initViews();

    }

    private void initViews() {
        tv_category_costs = findViewById(R.id.tv_category_costs);
        tv_total_cost = findViewById(R.id.tv_total_cost);
    }

    private void calenderDateSetting() {
        //getting date for setting a calender
        String week_day = Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(mCostDate).getDayOfWeek().toString());
        String date_No = valueOf(LocalDate.parse(mCostDate).getDayOfMonth());
        String month =Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(mCostDate).getMonth().toString());
        String year = valueOf(LocalDate.parse(mCostDate).getYear());

        tv_weekDay = findViewById(R.id.tv_weekDay);
        tv_dateNo = findViewById(R.id.tv_dateNo);
        tv_toolbar_mont_year = findViewById(R.id.tv_toolbar_month_year);

        tv_weekDay.setText(week_day);
        tv_dateNo.setText(date_No);
        tv_toolbar_mont_year.setText(month + ", " + year);
        tv_toolbar_mont_year.setTypeface(null, Typeface.BOLD);
    }

    @Override
    public void onDailyItemClickListener(int itemId) {
        // Launch AddCostActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(DailyExpensesActivity.this, AddCostActivity.class);
        intent.putExtra(AddCostActivity.EXTRA_COST_ID, itemId);
        startActivity(intent);
        }

    @Override
    protected void onStart() {
        super.onStart();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                totalCost = mDb.costDao().loadTotalCost(mCostDate);
                final String totalCostString = Helper.fromIntToDecimalString(totalCost);

                final Map<String, Integer> categoryCosts = new TreeMap<String, Integer>();

                category1Cost = mDb.costDao().loadSumCategoryCost(mCostDate, CATEGORY_1);
                if (category1Cost != 0) {
                categoryCosts.put(CATEGORY_1, category1Cost);
                }

                category2Cost = mDb.costDao().loadSumCategoryCost(mCostDate, CATEGORY_2);
                if (category2Cost != 0) {
                categoryCosts.put(CATEGORY_2, category2Cost);
                }

                category3Cost = mDb.costDao().loadSumCategoryCost(mCostDate, CATEGORY_3);
                if (category3Cost != 0) {
                    categoryCosts.put(CATEGORY_3, category3Cost);
                }

                category4Cost = mDb.costDao().loadSumCategoryCost(mCostDate, CATEGORY_4);
                if (category4Cost != 0) {
                    categoryCosts.put(CATEGORY_4, category4Cost);
                }

                category5Cost = mDb.costDao().loadSumCategoryCost(mCostDate, CATEGORY_5);
                if (category5Cost != 0) {
                    categoryCosts.put(CATEGORY_5, category5Cost);
                }

                category6Cost = mDb.costDao().loadSumCategoryCost(mCostDate, CATEGORY_6);
                if (category6Cost != 0) {
                    categoryCosts.put(CATEGORY_6, category6Cost);
                }

                category7Cost = mDb.costDao().loadSumCategoryCost(mCostDate, CATEGORY_7);
                if (category7Cost != 0) {
                    categoryCosts.put(CATEGORY_7, category7Cost);
                }

                category8Cost = mDb.costDao().loadSumCategoryCost(mCostDate, CATEGORY_8);
                if (category8Cost != 0) {
                    categoryCosts.put(CATEGORY_8, category8Cost);
                }

                category9Cost = mDb.costDao().loadSumCategoryCost(mCostDate, CATEGORY_9);
                if (category9Cost != 0) {
                    categoryCosts.put(CATEGORY_9, category9Cost);
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // update categoryCosts
                        tv_category_costs.setText("");

                        Map.Entry<String, Integer> lastEntry = ((TreeMap<String, Integer>) categoryCosts).lastEntry();
                        for (Map.Entry<String, Integer> entry : categoryCosts.entrySet()) {
                            if ((entry.getKey() + "=" + entry.getValue()).equals(lastEntry.toString())) {
                                tv_category_costs.append(entry.getKey()+": "+ Helper.fromIntToDecimalString(entry.getValue()) + " kn");
                            } else {
                                tv_category_costs.append(entry.getKey() + ": " + Helper.fromIntToDecimalString(entry.getValue()) + " kn\n");

                            }

                        }


                        // update total cost
                        tv_total_cost.setText("TOTAL: " + totalCostString + " kn");


                    }
                });
            }
        });
    }

}


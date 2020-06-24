package com.example.android.quantitanti;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.example.android.quantitanti.adapters.DailyCostAdapter;

import com.example.android.quantitanti.adapters.ScreenSlidePagerAdapter;
import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.factories.DailyExpensesViewModelFactory;
import com.example.android.quantitanti.helpers.Helper;
import com.example.android.quantitanti.models.CostPojo;
import com.example.android.quantitanti.models.DailyExpenseTagsWithPicsPojo;
import com.example.android.quantitanti.models.DailyExpensesViewModel;
import com.example.android.quantitanti.models.TotalCostPojo;
import com.example.android.quantitanti.sharedpreferences.SettingsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
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
import static com.example.android.quantitanti.database.CostEntry.CURRENCY_1;
import static com.example.android.quantitanti.database.CostEntry.CURRENCY_2;
import static com.example.android.quantitanti.database.CostEntry.CURRENCY_3;
import static com.example.android.quantitanti.database.CostEntry.CURRENCY_4;
import static java.lang.String.valueOf;

public class DailyExpensesActivity extends AppCompatActivity implements DailyCostAdapter.DailyItemClickListener {

    // Constant for logging
    private static final String TAG = CostListActivity.class.getSimpleName();

    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private DailyCostAdapter mAdapter;
    //  private DailyCostTagAdapter mTagAdapter;


    private CostDatabase mDb;

    //constants for sp currency
    String currency;
    public static String currency1;
    public static String currency2;
    List<String> diffCurrencies;


    //View Pager
    //todo number of total_cost slides (no of diff currencies on a date)
    public static int NUM_PAGES;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private ScreenSlidePagerAdapter pagerAdapter;
    List<TotalCostPojo> totalCostPojosForAdapter;
    List<Integer> minHight;
    private ViewPager2.OnPageChangeCallback onPageChangeCallback;


    // Extra for the cost ID to be received after rotation
    public static final String INSTANCE_COST_ID = "instanceCostId";
    // Constant for default cost id to be used when not in update mode
    private static final int DEFAULT_COST_ID = -1;
    private int mCostId = DEFAULT_COST_ID;
    private int costId;

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
    int categoryCostSlideHeight = 0;
  //  List<Integer> viewPager2MaxHight;
    int totalCost;
    String totalCostString;
    int category1Cost;
    int category2Cost;
    int category3Cost;
    int category4Cost;
    int category5Cost;
    int category6Cost;
    int category7Cost;
    int category8Cost;
    int category9Cost;

    SliderLayout slider;


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

        //setting DailyCostAdapter
        mRecyclerView = findViewById(R.id.recyclerViewDailyCost);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new DailyCostAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);


// Instantiate a ViewPager, tablayout and a PagerAdapter:
        viewPager2 = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tablayout);
        pagerAdapter = new ScreenSlidePagerAdapter(this, viewPager2);
        viewPager2.setAdapter(pagerAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        });
        tabLayoutMediator.attach();


        //receiving intent when onItemClickListener in CostListActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_COST_DATE)) {
            if (mCostDate == DEFAULT_COST_DATE) {
                mCostDate = intent.getStringExtra(EXTRA_COST_DATE);

                calenderDateSetting();

                DailyExpensesViewModelFactory factory = new DailyExpensesViewModelFactory(mDb, mCostDate);

                final DailyExpensesViewModel viewModel = new ViewModelProvider(this, factory).get(DailyExpensesViewModel.class);

                viewModel.getCosts().observe(this, new Observer<List<DailyExpenseTagsWithPicsPojo>>() {
                    @Override
                    public void onChanged(List<DailyExpenseTagsWithPicsPojo> dailyExpenseTagsWithPicsPojos) {
                        mAdapter.setmDailyCosts(dailyExpenseTagsWithPicsPojos);
                    }
                });

                viewModel.getTotalCategoryCosts().observe(this, totalCostPojos -> {
                    int height = 0;
                    for (TotalCostPojo tcp : totalCostPojos) {
                         if (tcp.getCategoryCosts().size() > height) {
                             height = tcp.getCategoryCosts().size();
                         }
                    }
                    pagerAdapter.setmDailyCategoryCosts(totalCostPojos, height);
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
                builder.setTitle(getString(R.string.warning));
                builder.setMessage(getString(R.string.are_you_sure));
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Get the position of the item to be deleted
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                int position = viewHolder.getAdapterPosition();
                                List<DailyExpenseTagsWithPicsPojo> dailyExpensePicsPojos = mAdapter.getDailyCosts();
                                //costEntries -> all costs from RecyclerView on certain date
                                mDb.costDao().deleteCostWithId(dailyExpensePicsPojos.get(position).getCostEntry().getId());
                                //  sumCategoriesAndTotal(); todo delete this
                            }
                        });
                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
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
        slider = findViewById(R.id.slider);
    }

    private void calenderDateSetting() {
        //getting date for setting a calender
        String week_day = Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(mCostDate).getDayOfWeek().toString());
        String date_No = valueOf(LocalDate.parse(mCostDate).getDayOfMonth());
        String month = Helper.fromUperCaseToFirstCapitalizedLetter
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
    public void onBackPressed() {
        if (viewPager2.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
        }
    }

    public void setTotalCostPojosForAdapter(List<TotalCostPojo> totalCostPojos) {
        if (totalCostPojos == null) {
            Toast.makeText(this, "There is no data", Toast.LENGTH_SHORT).show();
        } else {
            totalCostPojosForAdapter = totalCostPojos;
            Log.d(String.valueOf(totalCostPojosForAdapter), " dhdhdhd");  //ne radi
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_daily_cost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        viewPager2.unregisterOnPageChangeCallback(onPageChangeCallback);
//    }
}


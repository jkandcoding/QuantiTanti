package com.example.android.quantitanti;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.database.CostEntry;
import com.example.android.quantitanti.factories.AddCostViewModelFactory;
import com.example.android.quantitanti.helpers.Helper;
import com.example.android.quantitanti.models.AddCostViewModel;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class AddCostActivity extends AppCompatActivity {

    // Extra for the cost ID to be received in the intent
    public static final String EXTRA_COST_ID = "extraCostId";
    // Extra for the cost ID to be received after rotation
    public static final String INSTANCE_COST_ID = "instanceCostId";
    // Constant for default cost id to be used when not in update mode
    private static final int DEFAULT_COST_ID = -1;
    // Constant for logging
    private static final String TAG = AddCostActivity.class.getSimpleName();
    // Fields for views
    String updateCategory;
    String category;
    EditText mCostDescription;
    EditText mCostValue;

    //Category buttons
    ImageButton iBtn_car;
    ImageButton iBtn_clothes;
    ImageButton iBtn_food;
    ImageButton iBtn_utilities;
    ImageButton iBtn_groceries;
    ImageButton iBtn_education;
    ImageButton iBtn_sport;
    ImageButton iBtn_cosmetics;
    ImageButton iBtn_other;

    String setCategory;

    //Datepicker
    View mCalender;
    TextView mTv_weekDay;
    TextView mTv_dateNo;
    TextView tv_toolbar_month_year;
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;

    // OffsetDateTime ThreeTenABP
    String chosenDateString;
    String defaultTodayDateString;
    String updateDateString;
    String date;
    String dayOfMonthString;
    String monthString;
    String yearString;
    String dayOfWeekString;

    TextView tv_addCost_label;

    private int mCostId = DEFAULT_COST_ID;
    private static final String DEFAULT_COST_DATE = "2020-01-01";
    private String mCostDate = DEFAULT_COST_DATE;

    // Member variable for the Database
    private CostDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_add_cost);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_ac);
        setSupportActionBar(myToolbar);
        setTitle("Select:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set calender on Toolbar
            LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View calender = inflator.inflate(R.layout.calender_view, null);
            myToolbar.addView(calender);
        // set TextView on Toolbar
            LayoutInflater inflater2 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View monthYear = inflater2.inflate(R.layout.tv_toolbar_montyear, null);
            myToolbar.addView(monthYear);

        initViews();

        mDb = CostDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_COST_ID)) {
            mCostId = savedInstanceState.getInt(INSTANCE_COST_ID, DEFAULT_COST_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(DailyExpensesActivity.EXTRA_COST_DATE)) {
            if (mCostDate == DEFAULT_COST_DATE) {
                mCostDate = intent.getStringExtra(DailyExpensesActivity.EXTRA_COST_DATE);

                calenderDateSetting();

                iBtn_car.setAlpha((float) 1);
                setCategory = CATEGORY_1 ;
            }
        }

        else if (intent != null && intent.hasExtra(EXTRA_COST_ID)) {
            setTitle("Update:");
            tv_addCost_label.setText("Cost category:");
            if (mCostId == DEFAULT_COST_ID) {
                // populate the UI
                mCostId = intent.getIntExtra(EXTRA_COST_ID, DEFAULT_COST_ID);


                AddCostViewModelFactory factory = new AddCostViewModelFactory(mDb, mCostId);
                final AddCostViewModel viewModel =
                        ViewModelProviders.of(this, factory).get(AddCostViewModel.class);

                viewModel.getCost().observe(this, new Observer<CostEntry>() {
                    @Override
                    public void onChanged(CostEntry costEntry) {
                        viewModel.getCost().removeObserver(this);
                        populateUI(costEntry);
                    }
                });
            }
        }

        else {
            iBtn_car.setAlpha((float) 1);
            setCategory = CATEGORY_1;
        }

        onCategoryButtonsClicked();
    }

    private void calenderDateSetting() {
        //getting date for setting a calender
        String week_day = Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(mCostDate).getDayOfWeek().toString());
        String date_No = valueOf(LocalDate.parse(mCostDate).getDayOfMonth());
        String month = Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(mCostDate).getMonth().toString());
        String year = valueOf(LocalDate.parse(mCostDate).getYear());

        mTv_weekDay.setText(week_day);
        mTv_dateNo.setText(date_No);
        tv_toolbar_month_year.setText(month + ", " + year);
        tv_toolbar_month_year.setTypeface(null, Typeface.BOLD);
    }

    private void populateUI(CostEntry costEntry) {
        if (costEntry == null) {
            return;
        }

        updateDateString = costEntry.getDate();
        updateCategory = costEntry.getCategory();
        updateCategoryImgBtn();
        mTv_weekDay.setText(Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(updateDateString).getDayOfWeek().toString()));
        mTv_dateNo.setText(valueOf(LocalDate.parse(updateDateString).getDayOfMonth()));

        String month = Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(updateDateString).getMonth().toString());
        String year = valueOf(LocalDate.parse(updateDateString).getYear());
        tv_toolbar_month_year.setText(month + ", " + year);
        tv_toolbar_month_year.setTypeface(null, Typeface.BOLD);
        mCostDescription.setText(costEntry.getName());
        mCostValue.setText(Helper.fromIntToDecimalString(costEntry.getCost()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_COST_ID, mCostId);
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        //category buttons
        iBtn_car = findViewById(R.id.iBtn_car);
        iBtn_clothes = findViewById(R.id.iBtn_clothes);
        iBtn_food = findViewById(R.id.iBtn_food);
        iBtn_utilities = findViewById(R.id.iBtn_utilities);
        iBtn_groceries = findViewById(R.id.iBtn_groceries);
        iBtn_education = findViewById(R.id.iBtn_education);
        iBtn_sport = findViewById(R.id.iBtn_sport);
        iBtn_cosmetics = findViewById(R.id.iBtn_cosmetics);
        iBtn_other = findViewById(R.id.iBtn_other);

        tv_addCost_label =findViewById(R.id.tv_addCost_label);

        // one cost item
        mCostDescription = findViewById(R.id.et_costDescription);
        mCostValue = findViewById(R.id.et_costValue);
        mCostValue.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});

        //calender
        mCalender = findViewById(R.id.view_calender);
        mTv_weekDay = findViewById(R.id.tv_weekDay);
        mTv_dateNo = findViewById(R.id.tv_dateNo);
        tv_toolbar_month_year = findViewById(R.id.tv_toolbar_month_year);

        //getting current date with threeTenABP
        OffsetDateTime defaultTodayDate = OffsetDateTime.now();
        defaultTodayDateString = defaultTodayDate.toLocalDate().toString();  //for db - localDate, no Time


        //Strings for tv_s -> default date
        dayOfMonthString = valueOf(defaultTodayDate.getDayOfMonth());
        monthString = Helper.fromUperCaseToFirstCapitalizedLetter(defaultTodayDate.getMonth().toString());
        yearString = valueOf(defaultTodayDate.getYear());
        dayOfWeekString = Helper.fromUperCaseToFirstCapitalizedLetter(defaultTodayDate.getDayOfWeek().toString());

        //int for DatePicker
        dayOfMonth = defaultTodayDate.getDayOfMonth();
        month = defaultTodayDate.getMonthValue();
        year = defaultTodayDate.getYear();

        //set calender with default date
        mTv_weekDay.setText(dayOfWeekString);
        mTv_dateNo.setText(dayOfMonthString);

        tv_toolbar_month_year.setText(monthString + ", " + yearString);
        tv_toolbar_month_year.setTypeface(null, Typeface.BOLD);

        mCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCalenderClicked();
            }
        });
    }

    public void onCategoryButtonsClicked() {
        iBtn_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateCategory != null || setCategory != null) {
                    setAlphaLow();
                }
                iBtn_car.setAlpha((float)1);
                 setCategory = null;
                setCategory = CATEGORY_1;
            }
        });

        iBtn_clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateCategory != null || setCategory != null) {
                    setAlphaLow();
                }
                iBtn_clothes.setAlpha((float)1);
                setCategory = null;
                setCategory = CATEGORY_2;
            }
        });

        iBtn_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateCategory != null || setCategory != null) {
                    setAlphaLow();
                }
                iBtn_food.setAlpha((float)1);
                setCategory = null;
                setCategory = CATEGORY_3;
            }
        });

        iBtn_utilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateCategory != null || setCategory != null) {
                    setAlphaLow();
                }
                iBtn_utilities.setAlpha((float)1);
                setCategory = null;
                setCategory = CATEGORY_4;
            }
        });

        iBtn_groceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateCategory != null || setCategory != null) {
                    setAlphaLow();
                }
                iBtn_groceries.setAlpha((float)1);
                setCategory = null;
                setCategory = CATEGORY_5;
            }
        });

        iBtn_education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateCategory != null || setCategory != null) {
                    setAlphaLow();
                }
                iBtn_education.setAlpha((float)1);
                setCategory = null;
                setCategory = CATEGORY_6;
            }
        });

        iBtn_sport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateCategory != null || setCategory != null) {
                    setAlphaLow();
                }
                iBtn_sport.setAlpha((float)1);
                setCategory = null;
                setCategory = CATEGORY_7;
            }
        });

        iBtn_cosmetics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateCategory != null || setCategory != null) {
                    setAlphaLow();
                }
                iBtn_cosmetics.setAlpha((float)1);
                setCategory = null;
                setCategory = CATEGORY_8;
            }
        });

        iBtn_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateCategory != null || setCategory != null) {
                    setAlphaLow();
                }
                iBtn_other.setAlpha((float) 1);
                setCategory = null;
                setCategory = CATEGORY_9;
            }
        });

    }

    public void setAlphaLow() {
        iBtn_car.setAlpha((float) 0.3);
        iBtn_clothes.setAlpha((float) 0.3);
        iBtn_food.setAlpha((float) 0.3);
        iBtn_utilities.setAlpha((float) 0.3);
        iBtn_groceries.setAlpha((float) 0.3);
        iBtn_education.setAlpha((float) 0.3);
        iBtn_sport.setAlpha((float) 0.3);
        iBtn_cosmetics.setAlpha((float) 0.3);
        iBtn_other.setAlpha((float) 0.3);
    }

    public void updateCategoryImgBtn() {
        switch (updateCategory) {
            case CATEGORY_1:
                iBtn_car.setAlpha((float) 1);
                break;
            case CATEGORY_2:
                iBtn_clothes.setAlpha((float) 1);
                break;
            case CATEGORY_3:
                iBtn_food.setAlpha((float) 1);
                break;
            case CATEGORY_4:
                iBtn_utilities.setAlpha((float) 1);
                break;
            case CATEGORY_5:
                iBtn_groceries.setAlpha((float) 1);
                break;
            case CATEGORY_6:
                iBtn_education.setAlpha((float) 1);
                break;
            case CATEGORY_7:
                iBtn_sport.setAlpha((float) 1);
                break;
            case CATEGORY_8:
                iBtn_cosmetics.setAlpha((float) 1);
                break;
            case CATEGORY_9:
                iBtn_other.setAlpha((float) 1);
                break;
        }
    }

   public void onCalenderClicked() {
        datePickerDialog = new DatePickerDialog(AddCostActivity.this, R.style.MyDatePickerDialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        chosenDateString = null;

                        // Get chosen date from DatePicker for saving into db
                        OffsetDateTime chosenDate = OffsetDateTime.of(year, month+1, dayOfMonth,
                                0, 0, 0, 0, ZoneOffset.UTC);
                        chosenDateString = chosenDate.toLocalDate().toString();

                        //setting textviews with chosen date from DatePicker
                        dayOfMonthString = valueOf(chosenDate.getDayOfMonth());
                        monthString = Helper.fromUperCaseToFirstCapitalizedLetter(chosenDate.getMonth().toString());
                        yearString = valueOf(chosenDate.getYear());
                        dayOfWeekString = Helper.fromUperCaseToFirstCapitalizedLetter(chosenDate.getDayOfWeek().toString());
                        mTv_dateNo.setText(dayOfMonthString);
                        mTv_weekDay.setText(dayOfWeekString);
                        tv_toolbar_month_year.setText(monthString + ", " + yearString);
                    }
                }, year, month-1, dayOfMonth);  //indexes of DatePicker for month (0-11), indexes of threetenABP (1-12)
        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_cost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        } else if (id == R.id.item_save) {
                    onSaveItemClicked();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * onSaveItemClicked is called when the "save" item is clicked.
     * It retrieves user input and inserts that new cost data into the underlying database.
     */
    public void onSaveItemClicked() {
        String name = mCostDescription.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter cost description, please", Toast.LENGTH_SHORT).show();
            return;
        }

        String costString = mCostValue.getText().toString().trim();
        costString = costString.replace(" ", "");
        int cost = 0;
        if (!TextUtils.isEmpty(costString)) {
           double cost1 = (Double.parseDouble(costString));
           cost = Helper.fromDoubleToInt(cost1);
        }

        //save date
        if (chosenDateString != null) {
             date = chosenDateString;
             //click on costItem
        } else if (chosenDateString == null && mCostId != DEFAULT_COST_ID) {
           date = updateDateString;
           //click on new cost under DailyCostActivity
        }  else if (chosenDateString == null && mCostDate != DEFAULT_COST_DATE) {
            date = mCostDate;
        }  else {
            date = defaultTodayDateString;
        }

        //save category
        if (setCategory == null) {
            category = updateCategory;
        } else {
            category = setCategory;
        }

        // unos u bazu
        final CostEntry costEntry = new CostEntry(category, name, cost, date);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mCostId == DEFAULT_COST_ID) {
                    //insert new cost
                    mDb.costDao().insertCost(costEntry);
                } else {
                    //update cost
                    costEntry.setId(mCostId);
                    mDb.costDao().updateCost(costEntry);
                }
                finish();
            }
        });
    }
}

// limiting decimal places in input costValue to 2 digits
class DecimalDigitsInputFilter implements InputFilter {
    private Pattern mPattern;
    DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = mPattern.matcher(dest);
        if (!matcher.matches())
            return "";
        return null;
    }
}
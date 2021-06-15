package com.example.android.quantitanti;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.database.CostEntry;
import com.example.android.quantitanti.database.Expenses_tags_join;
import com.example.android.quantitanti.database.PicsEntry;
import com.example.android.quantitanti.factories.AddCostViewModelFactory;
import com.example.android.quantitanti.fragments.CurrencyFragment;
import com.example.android.quantitanti.fragments.MultiselectTagDialogFragment;
import com.example.android.quantitanti.helpers.Helper;
import com.example.android.quantitanti.models.DailyExpenseTagsWithPicsPojo;
import com.example.android.quantitanti.viewmodels.AddCostViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.TextStyle;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

public class AddCostActivity extends AppCompatActivity implements MultiselectTagDialogFragment.OnDataPass, CurrencyFragment.OnDataPassCurr, SharedPreferences.OnSharedPreferenceChangeListener {

    // String key for MultiSelectTagDialogFragment
    public static final String BUNDLE_TAGS = "bundleTags";
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
    Chip mShowCurrency;
    String currencySave;
    View horScrollV;

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

    //tags
    LinearLayout ll_pickATag;
    TextView tv_addCost_label;
    TextView tv_pickATag;
    ChipGroup cg_tags;

    List<String> choosenTags = new ArrayList<>();
    List<Integer> choosenTagsIds = new ArrayList<>();
    int costIdWithTagOrPic;

    //pics
    LinearLayout ll_takeAPic;
    TextView tv_takeAPic;
    ChipGroup cg_picUriResult;
    private String pictureFilePath;
    String picName;
    Map<String, String> picDataForDB = new HashMap<>();
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    static final int REQUEST_GALLERY_PHOTO = 200;
    String photoUriFromCameraString = "";
    String photoUriFromGalleryString = "";
    File mPhotoFile;

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
                setCategory = CATEGORY_1;
            }
        } else if (intent != null && intent.hasExtra(EXTRA_COST_ID)) {
            setTitle("Update:");
            tv_addCost_label.setText("Cost category:");
            if (mCostId == DEFAULT_COST_ID) {
                // populate the UI
                mCostId = intent.getIntExtra(EXTRA_COST_ID, DEFAULT_COST_ID);

                AddCostViewModelFactory factory = new AddCostViewModelFactory(mDb, mCostId);
                final AddCostViewModel viewModel =
                        new ViewModelProvider(this, factory).get(AddCostViewModel.class);

                //getCost from ViewModel
                viewModel.getCost().observe(this, new Observer<DailyExpenseTagsWithPicsPojo>() {
                    @Override
                    public void onChanged(DailyExpenseTagsWithPicsPojo dailyExpTagsWithPicsPojo) {
                        viewModel.getCost().removeObserver(this);
                        populateUI(dailyExpTagsWithPicsPojo);
                    }
                });
            }
        } else {
            iBtn_car.setAlpha((float) 1);
            setCategory = CATEGORY_1;
        }

        onCategoryButtonsClicked();
        pickATag();
        takeAPic();

        setupSharedPreferences();
        pickCurrency();
    }

    private void setCurrencyFromPreferences(SharedPreferences sharedPreferences) {
        String currency = sharedPreferences.getString(getString(R.string.pref_currency_key), getString(R.string.pref_currency_value_kuna));
        mShowCurrency.setText(currency);
    }

    private void pickCurrency() {
        mShowCurrency.setOnClickListener(v -> {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
//                if (prev != null) {
//                    ft.remove(prev);
//                }
//                ft.addToBackStack(null);
            DialogFragment dialogFragment = new CurrencyFragment();
            dialogFragment.show(ft, "currencyDialog");


        });
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setCurrencyFromPreferences(sharedPreferences);
        //Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_currency_key))) {
            setCurrencyFromPreferences(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void calenderDateSetting() {
        //getting date for setting a calender
        String week_day = Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(mCostDate).getDayOfWeek().toString());
        String date_No = valueOf(LocalDate.parse(mCostDate).getDayOfMonth());
        String month = Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(mCostDate).getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        String year = valueOf(LocalDate.parse(mCostDate).getYear());

        mTv_weekDay.setText(week_day);
        mTv_dateNo.setText(date_No);
        tv_toolbar_month_year.setText(month + ", " + year);
        tv_toolbar_month_year.setTypeface(null, Typeface.BOLD);
    }

    private void populateUI(DailyExpenseTagsWithPicsPojo dailyExpTagsWithPicsPojo) {
        if (dailyExpTagsWithPicsPojo == null) {
            return;
        }
        updateCategory = dailyExpTagsWithPicsPojo.getCostEntry().getCategory();
        updateCategoryImgBtn();

        updateDateString = dailyExpTagsWithPicsPojo.getCostEntry().getDate();
        mTv_weekDay.setText(Helper.fromUperCaseToFirstCapitalizedLetter
                (LocalDate.parse(updateDateString).getDayOfWeek().toString()));
        mTv_dateNo.setText(valueOf(LocalDate.parse(updateDateString).getDayOfMonth()));
        String month = Helper.fromUperCaseToFirstCapitalizedLetter
                //  (LocalDate.parse(updateDateString).getMonth().toString());
                        (LocalDate.parse(updateDateString).getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        String year = valueOf(LocalDate.parse(updateDateString).getYear());
        tv_toolbar_month_year.setText(month + ", " + year);
        tv_toolbar_month_year.setTypeface(null, Typeface.BOLD);

        mCostDescription.setText(dailyExpTagsWithPicsPojo.getCostEntry().getName());

        mCostValue.setText(Helper.fromIntToDecimalString(dailyExpTagsWithPicsPojo.getCostEntry().getCost()));

        mShowCurrency.setText(dailyExpTagsWithPicsPojo.getCostEntry().getCurrency());

        fillCgTags(dailyExpTagsWithPicsPojo.getTagNames());

        //fill the hashmap with picNames and Uris for Db entry:
        List<PicsEntry> picsData = dailyExpTagsWithPicsPojo.getPicsEntries();
        for (PicsEntry p : picsData) {
            picDataForDB.put(p.getPic_name(), p.getPic_uri());
        }

        //fill the chipgroup:
        cg_picUriResult.setVisibility(View.VISIBLE);

        for (Map.Entry<String, String> entry : picDataForDB.entrySet()) {
            Chip chip_pic = (Chip) getLayoutInflater().inflate(R.layout.single_pic_chip_layout, null);
            chip_pic.setText(entry.getKey());
            chip_pic.setCloseIconVisible(true);
            chip_pic.setOnCloseIconClickListener(v -> {
                cg_picUriResult.removeView(chip_pic);
                removeFromHashmap(entry.getKey());
            });
            cg_picUriResult.addView(chip_pic);
        }
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
        horScrollV = findViewById(R.id.horizontalScrollView);

        //tags
        tv_addCost_label = findViewById(R.id.tv_addCost_label);
        ll_pickATag = findViewById(R.id.ll_pickATag);
        tv_pickATag = findViewById(R.id.tv_pickATag);
        cg_tags = findViewById(R.id.cg_tags);

        //pics
        ll_takeAPic = findViewById(R.id.ll_takeAPic);
        tv_takeAPic = findViewById(R.id.tv_takeAPic);
        cg_picUriResult = findViewById(R.id.cg_picUriResult);
        //slider = findViewById(R.id.slider);

        // one cost item
        mCostDescription = findViewById(R.id.et_costDescription);
        mCostValue = findViewById(R.id.et_costValue);
        mCostValue.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(7, 2)});
        mCostValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    onSaveItemClicked();
                }
                return false;
            }
        });
        mShowCurrency = findViewById(R.id.cp_showCurrency);

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
        // monthString = Helper.fromUperCaseToFirstCapitalizedLetter(defaultTodayDate.getMonth().toString());
        monthString = Helper.fromUperCaseToFirstCapitalizedLetter(defaultTodayDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
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
        iBtn_car.setOnClickListener(v -> {
            if (updateCategory != null || setCategory != null) {
                setAlphaLow();
            }
            iBtn_car.setAlpha((float) 1);
            setCategory = null;
            setCategory = CATEGORY_1;
        });

        iBtn_clothes.setOnClickListener(v -> {
            if (updateCategory != null || setCategory != null) {
                setAlphaLow();
            }
            iBtn_clothes.setAlpha((float) 1);
            setCategory = null;
            setCategory = CATEGORY_2;
        });

        iBtn_food.setOnClickListener(v -> {
            if (updateCategory != null || setCategory != null) {
                setAlphaLow();
            }
            iBtn_food.setAlpha((float) 1);
            setCategory = null;
            setCategory = CATEGORY_3;
        });

        iBtn_utilities.setOnClickListener(v -> {
            if (updateCategory != null || setCategory != null) {
                setAlphaLow();
            }
            iBtn_utilities.setAlpha((float) 1);
            setCategory = null;
            setCategory = CATEGORY_4;
        });

        iBtn_groceries.setOnClickListener(v -> {
            if (updateCategory != null || setCategory != null) {
                setAlphaLow();
            }
            iBtn_groceries.setAlpha((float) 1);
            setCategory = null;
            setCategory = CATEGORY_5;
        });

        iBtn_education.setOnClickListener(v -> {
            if (updateCategory != null || setCategory != null) {
                setAlphaLow();
            }
            iBtn_education.setAlpha((float) 1);
            setCategory = null;
            setCategory = CATEGORY_6;
        });

        iBtn_sport.setOnClickListener(v -> {
            if (updateCategory != null || setCategory != null) {
                setAlphaLow();
            }
            iBtn_sport.setAlpha((float) 1);
            setCategory = null;
            setCategory = CATEGORY_7;
        });

        iBtn_cosmetics.setOnClickListener(v -> {
            if (updateCategory != null || setCategory != null) {
                setAlphaLow();
            }
            iBtn_cosmetics.setAlpha((float) 1);
            setCategory = null;
            setCategory = CATEGORY_8;
        });

        iBtn_other.setOnClickListener(v -> {
            if (updateCategory != null || setCategory != null) {
                setAlphaLow();
            }
            iBtn_other.setAlpha((float) 1);
            setCategory = null;
            setCategory = CATEGORY_9;
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
                (datePicker, year, month, dayOfMonth) -> {
                    chosenDateString = null;

                    // Get chosen date from DatePicker for saving into db
                    OffsetDateTime chosenDate = OffsetDateTime.of(year, month + 1, dayOfMonth,
                            0, 0, 0, 0, ZoneOffset.UTC);
                    chosenDateString = chosenDate.toLocalDate().toString();

                    //setting textviews with chosen date from DatePicker
                    dayOfMonthString = valueOf(chosenDate.getDayOfMonth());
                    monthString = Helper.fromUperCaseToFirstCapitalizedLetter(chosenDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
                    yearString = valueOf(chosenDate.getYear());
                    dayOfWeekString = Helper.fromUperCaseToFirstCapitalizedLetter(chosenDate.getDayOfWeek().toString());
                    mTv_dateNo.setText(dayOfMonthString);
                    mTv_weekDay.setText(dayOfWeekString);
                    tv_toolbar_month_year.setText(monthString + ", " + yearString);
                }, year, month - 1, dayOfMonth);  //indexes of DatePicker for month (0-11), indexes of threetenABP (1-12)
        datePickerDialog.show();
    }

    @Override
    public void onDataPassCurr(String currencyCode) {
        mShowCurrency.setText(currencyCode);
    }

    public void pickATag() {
        ll_pickATag.setOnClickListener(v -> {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
//                if (prev != null) {
//                    ft.remove(prev);
//                }
//                ft.addToBackStack(null);
            DialogFragment dialogFragment = new MultiselectTagDialogFragment();
            //send tags that should be checked to dialogFragment
            if (choosenTags.size() != 0) {
                Bundle bundle = new Bundle();
                ArrayList<String> choosenTagsArray = new ArrayList<>(choosenTags.size());
                choosenTagsArray.addAll(choosenTags);
                bundle.putStringArrayList(BUNDLE_TAGS, choosenTagsArray);
                dialogFragment.setArguments(bundle);
            }
            dialogFragment.show(ft, "dialog");
        });
    }

    @Override
    public void onDataPass(List<String> data) {
        fillCgTags(data);
    }

    private void fillCgTags(List<String> data) {
        choosenTags.clear();
        choosenTags.addAll(data);
        Log.d(String.valueOf(choosenTags), "sadadada");
        cg_tags.setVisibility(View.VISIBLE);
        cg_tags.removeAllViews();
        for (String s : data) {
//          final Chip chip_tag = new Chip(this);
            Chip chip_tag = (Chip) getLayoutInflater().inflate(R.layout.single_tag_chip_layout, null);
            chip_tag.setText(s);
            chip_tag.setCloseIconVisible(true);
            chip_tag.setOnCloseIconClickListener(v -> {
                cg_tags.removeView(chip_tag);
                String removedTag = chip_tag.getText().toString();
                if (choosenTags.contains(removedTag)) {
                    choosenTags.remove(removedTag);
                    Log.d(String.valueOf(choosenTags), "sadadada");
                }
            });
            cg_tags.addView(chip_tag);
        }
    }

    public void takeAPic() {
        ll_takeAPic.setOnClickListener(v -> {

            // Alert dialog for capture or select from galley
            final CharSequence[] items = {
                    "Take Photo", "Choose from Library",
                    "Cancel"
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(AddCostActivity.this, R.style.AlertDialogStyle);
            builder.setIcon(R.drawable.okvir_za_datum_warning);
            builder.setTitle("Source:");
            builder.setItems(items, (dialog, item) -> {
                if (items[item].equals("Take Photo")) {
                    requestStoragePermission(true);
                } else if (items[item].equals("Choose from Library")) {
                    requestStoragePermission(false);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });
    }

    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);

                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    /**
     * Select image from gallery
     */
    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CAPTURE_IMAGE) {
                File imgFile = new File(pictureFilePath);
                if (imgFile.exists()) {
                    photoUriFromCameraString = (Uri.fromFile(imgFile)).toString();
                    nameYourPic();
                }

            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();

                File imgFile = new File(getRealPathFromUri(selectedImage));
                if (imgFile.exists()) {
                    photoUriFromGalleryString = (Uri.fromFile(imgFile)).toString();
                }
                nameYourPic();
            }
        }
    }

    public void nameYourPic() {
        // get alert_dialog_name_your_pic.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View nameYourPic = li.inflate(R.layout.alert_dialog_name_your_pic, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(nameYourPic);
        //  final EditText edittext = new EditText(getApplicationContext());
        final EditText edittext = nameYourPic.findViewById(R.id.et_nameYourPic);
        // alert.setTitle("Name your picture");
        // alert.setView(edittext);
        alert.setCancelable(false);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                if (edittext.getText().toString().isEmpty()) {
                    picName = timeStamp;
                } else {
                    picName = edittext.getText().toString().trim() + "_" + timeStamp;
                }

                if (!photoUriFromCameraString.isEmpty()) {
                    picDataForDB.put(picName, photoUriFromCameraString);
                }
                if (!photoUriFromGalleryString.isEmpty()) {
                    picDataForDB.put(picName, photoUriFromGalleryString);
                }

                photoUriFromCameraString = "";
                photoUriFromGalleryString = "";

                setPicTags();
            }
        });
        alert.show();
    }

    public void setPicTags() {
        cg_picUriResult.setVisibility(View.VISIBLE);

//      final Chip chip_pic = new Chip(this);
        Chip chip_pic = (Chip) getLayoutInflater().inflate(R.layout.single_pic_chip_layout, null);
        chip_pic.setText(picName);
        chip_pic.setCloseIconVisible(true);
        chip_pic.setOnCloseIconClickListener(v -> {
            cg_picUriResult.removeView(chip_pic);
            removeFromHashmap(picName);
        });
        cg_picUriResult.addView(chip_pic);
    }

    public void removeFromHashmap(String name) {
        Iterator<String> iterator = picDataForDB.keySet().iterator();
        while (iterator.hasNext()) {
            String mapKey = iterator.next();
            if (mapKey.contains(name)) {
                iterator.remove();
            }
        }
    }

    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(final boolean isCamera) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else {
                                dispatchGalleryIntent();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(
                        error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT)
                                .show())
                .onSameThread()
                .check();
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage(
                "This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GO TO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);  //todo: registerForActivityResult
    }

    /**
     * Create file with current timestamp name
     *
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        pictureFilePath = mFile.getAbsolutePath();
        return mFile;
    }

    /**
     * Get real file path from URI
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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
        } else if (chosenDateString == null && mCostDate != DEFAULT_COST_DATE) {
            date = mCostDate;
        } else {
            date = defaultTodayDateString;
        }

        //save category
        if (setCategory == null) {
            category = updateCategory;
        } else {
            category = setCategory;
        }

        //save currency
        currencySave = mShowCurrency.getText().toString().trim();

        //ENTRY INTO DATABASE:
        final CostEntry costEntry = new CostEntry(category, name, cost, date, currencySave);
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
                costIdWithTagOrPic = mDb.costDao().loadLastCostId();
                Log.d(String.valueOf(costIdWithTagOrPic), "costIdWithTagOrPic"); //ok

                //if tags exist:
                for (final String tag : choosenTags) {
                    choosenTagsIds.add(mDb.tagsDao().loadTagId(tag));
                }
                //insert new cost-tag
                if (mCostId == DEFAULT_COST_ID) {
                    if (!choosenTags.isEmpty()) {
                        for (final Integer chosenTagId : choosenTagsIds) {
                            mDb.expenses_tags_join_dao().insert(new Expenses_tags_join(costIdWithTagOrPic, chosenTagId));
                        }
                    }
                } else {
                    //update cost-tag
                    List<Integer> tagIdsFromDB = mDb.expenses_tags_join_dao().getTagIdsForCost(mCostId);
                    List<Integer> helperList = new ArrayList<>();

                    if (!tagIdsFromDB.isEmpty() && !choosenTagsIds.isEmpty()) {
                        for (Integer i : tagIdsFromDB) {
                            if (choosenTagsIds.contains(i)) {
                                helperList.add(i);
                            }
                        }
                        tagIdsFromDB.removeAll(helperList);
                        choosenTagsIds.removeAll(helperList);
                    }
                    if (tagIdsFromDB.size() != 0) {
                        for (Integer i : tagIdsFromDB) {
                            mDb.expenses_tags_join_dao().delete(new Expenses_tags_join(mCostId, i));
                        }
                    }
                    if (choosenTagsIds.size() != 0) {
                        for (Integer i : choosenTagsIds) {
                            mDb.expenses_tags_join_dao().insert(new Expenses_tags_join(mCostId, i));
                        }
                    }
                }

                //if pics exist:
                if (cg_picUriResult != null) {
                    if (mCostId == DEFAULT_COST_ID) {
                        //insert new pic
                        for (Map.Entry<String, String> entry : picDataForDB.entrySet()) {
                            mDb.picsDao().insertPic(new PicsEntry(entry.getValue(), entry.getKey(), costIdWithTagOrPic));
                        }
                    } else {
                        //update pic:
                        List<PicsEntry> foundForDelete = new ArrayList<>();     //helper list
                        List<String> foundForDelMap = new ArrayList<>();        //helper list

                        List<PicsEntry> picsFromDB = mDb.picsDao().loadPicsByCostId(mCostId);
                        if (picsFromDB.size() != 0 && picDataForDB.size() != 0) {
                            for (Map.Entry<String, String> entry : picDataForDB.entrySet()) {
                                for (PicsEntry picsEntry : picsFromDB) {
                                    if (picsEntry.getPic_name().equals(entry.getKey())) {
                                        foundForDelMap.add(entry.getKey());
                                        foundForDelete.add(picsEntry);
                                    }
                                }
                            }
                            for (String s : foundForDelMap) {
                                removeFromHashmap(s);
                            }
                            picsFromDB.removeAll(foundForDelete);
                        }
                        if (picsFromDB.size() != 0) {
                            for (PicsEntry picsEntry : picsFromDB) {
                                mDb.picsDao().deletePic(picsEntry);
                            }
                        }
                        if (picDataForDB.size() != 0) {
                            for (Map.Entry<String, String> entry : picDataForDB.entrySet()) {
                                mDb.picsDao().insertPic(new PicsEntry(entry.getValue(), entry.getKey(), mCostId));
                            }
                        }
                    }
                }

                runOnUiThread(() -> Toast.makeText(AddCostActivity.this, "Item saved", Toast.LENGTH_SHORT).show());
                finish();
            }
        });
    }


}
//------------------------------------------------------------------------------------------

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
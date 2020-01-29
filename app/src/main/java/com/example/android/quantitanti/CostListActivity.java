package com.example.android.quantitanti;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.adapters.CostAdapter;
import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.database.DailyExpensesView;
import com.example.android.quantitanti.models.CostListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CostListActivity extends AppCompatActivity implements CostAdapter.ItemClickListener {

    // Constant for logging
    private static final String TAG = CostListActivity.class.getSimpleName();

    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private CostAdapter mAdapter;

    private CostDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cost_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_cl);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextAppearance(this, R.style.AppTitle);

        CLRecyclerView mRecyclerView = (CLRecyclerView) findViewById(R.id.recyclerViewCost);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set emptyView
        mEmptyView = findViewById(R.id.emptyView);
        mRecyclerView.setEmptyView(mEmptyView);

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new CostAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

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
                // swipe to delete + alertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(CostListActivity.this, R.style.AlertDialogStyle);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.okvir_za_datum_warning);
                builder.setTitle("Warning");
                builder.setMessage("Are you sure you want to delete entire day?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Get the position of the item to be deleted
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        int position = viewHolder.getAdapterPosition();
                                        List<DailyExpensesView> expenses = mAdapter.getDailyExpenses();
                                        //expenses -> total daily expenses from RecyclerView, all of them
                                        //iz odabranog troska (kojeg zelimo obrisati) uzimamo position (od adaptera)
                                        //od toga uzimamo date i kao parametar ide u deleteDailyCosts() metodu
                                        String deleteDate = expenses.get(position).getOneDate();
                                        mDb.costDao().deleteDailyCosts(deleteDate);
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
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CostListActivity.this, AddCostActivity.class);
                startActivity(intent);
            }
        });

        mDb = CostDatabase.getInstance(getApplicationContext());
        setupViewModel();
    }

    private void setupViewModel() {
        CostListViewModel viewModel = ViewModelProviders.of(this).get(CostListViewModel.class);
        viewModel.getDailyExpenses().observe(this, new Observer<List<DailyExpensesView>>() {
            @Override
            public void onChanged(List<DailyExpensesView> dailyExpensesViews) {
                Log.d(TAG, "Updating list of costs from LiveData in ViewModel");
                mAdapter.setmDailyExpenses(dailyExpensesViews);
            }
        });
    }

    @Override
    public void onItemClickListener(String itemDate) {
        // Launch AddCostActivity adding the itemDate as an extra in the intent
        Intent intent = new Intent(CostListActivity.this, DailyExpensesActivity.class);
        intent.putExtra(DailyExpensesActivity.EXTRA_COST_DATE, itemDate);
        startActivity(intent);
    }
}

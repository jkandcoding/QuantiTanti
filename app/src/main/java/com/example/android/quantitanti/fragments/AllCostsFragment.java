package com.example.android.quantitanti.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.AddCostActivity;
import com.example.android.quantitanti.AppExecutors;
import com.example.android.quantitanti.R;
import com.example.android.quantitanti.adapters.AllCostsAdapter;
import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.factories.CostListViewModelFactory;
import com.example.android.quantitanti.models.DailyExpenseTagsWithPicsPojo;
import com.example.android.quantitanti.viewmodels.CostListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

public class AllCostsFragment extends Fragment implements AllCostsAdapter.ItemClickListener, FilterDialogFragment.OnDataPass {

    private static final int TARGET_FRAGMENT_REQUEST_CODE = 1;

    public static final String EXTRA_COST_ID = "extraCostId";

    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private AllCostsAdapter mAdapter;


    private CostDatabase mDb;

    public static AllCostsFragment getInstance() {
        AllCostsFragment fragment = new AllCostsFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_costs, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerview_all_costs);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new AllCostsAdapter(this, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         * An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         * and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                // swipe to delete + alertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle);
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
                                int position = viewHolder.getAbsoluteAdapterPosition();
                                List<DailyExpenseTagsWithPicsPojo> expenses = mAdapter.getmAllCosts();
                                int deleteId = expenses.get(position).getCostEntry().getId();
                                mDb.costDao().deleteCostWithId(deleteId);
                                //todo refresh adapter from TotalCostFragment
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog,
                        // so we will refresh the adapter to prevent hiding the item from UI
                        mAdapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
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
        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddCostActivity.class);
                startActivity(intent);
            }
        });

        mDb = CostDatabase.getInstance(requireActivity().getApplicationContext());

        setupViewModel();


    }

    private void setupViewModel() {
        CostListViewModelFactory factory = new CostListViewModelFactory(mDb);

        //CostListViewModel viewModel = new ViewModelProvider(this, factory).get(CostListViewModel.class);
        CostListViewModel viewModel = new ViewModelProvider(requireActivity(), factory).get(CostListViewModel.class);
        viewModel.getAllCosts().observe(getViewLifecycleOwner(), new Observer<List<DailyExpenseTagsWithPicsPojo>>() {

            @Override
            public void onChanged(List<DailyExpenseTagsWithPicsPojo> dailyExpenseTagsWithPicsPojos) {
                mAdapter.setAllCost(dailyExpenseTagsWithPicsPojos);
            }
        });
    }


    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddCostActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(getActivity(), AddCostActivity.class);
        intent.putExtra(EXTRA_COST_ID, itemId);
        startActivity(intent);
    }

    @Override
    public void onItemLongClickListener(int itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.okvir_za_datum_warning);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure you want to delete this cost?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.costDao().deleteCostWithId(itemId);
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //nothing happens
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuInflater inflater1 = requireActivity().getMenuInflater();
        inflater1.inflate(R.menu.menu_all_costs_fragment, menu);

        //animate filter/search icons to slowly appear
        MenuItem filterItem = menu.findItem(R.id.action_filter);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        // Post delayed so the view can be built,
        //         otherwise findViewById(R.id.menu_filter) would be null
        new Handler().postDelayed(() -> {
            AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(250);

            // Make item visible and start the animation
            filterItem.setVisible(true);
            searchItem.setVisible(true);

            requireActivity().findViewById(R.id.action_filter).startAnimation(animation);
            requireActivity().findViewById(R.id.action_search).startAnimation(animation);
        }, 1);


       //searching costs:
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
//            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
//            DialogFragment df = new FilterDialogFragment();
//            df.show(ft, "dialogFilter");

            FilterDialogFragment fragment = new FilterDialogFragment();
            fragment.setTargetFragment(this, 1);
            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            fragment.show(ft, "filterDialog");

            //todo replace target deprecated API (iznad) with childFragmentManager.setFragmentResultListener(...)

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataPass(List<String> dataCategories, List<String> dataTags) {
        //todo send data to AllCostAdapter
        mAdapter.setCategoriesAndTagsForFilter(dataCategories, dataTags);
    }

    @Override
    public void onPause() {
        super.onPause();


        //animate filter/search icons to slowly disappear
//        MenuItem filterItem = menu.findItem(R.id.action_filter);
//        MenuItem searchItem = menu.findItem(R.id.action_search);


 //       new Handler().postDelayed(() -> {
//            AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
//            animation.setDuration(250);

             //Make item visible and start the animation
//            filterItem.setVisible(true);
//            searchItem.setVisible(true);

//            requireActivity().findViewById(R.id.action_filter).startAnimation(animation);
//            requireActivity().findViewById(R.id.action_search).startAnimation(animation);
//        }, 1);
    }
}

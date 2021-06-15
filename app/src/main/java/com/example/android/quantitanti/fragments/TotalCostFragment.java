package com.example.android.quantitanti.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.AddCostActivity;
import com.example.android.quantitanti.AppExecutors;
import com.example.android.quantitanti.CLRecyclerView;
import com.example.android.quantitanti.DailyExpensesActivity;
import com.example.android.quantitanti.R;
import com.example.android.quantitanti.adapters.CostAdapter;
import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.factories.CostListViewModelFactory;
import com.example.android.quantitanti.models.TotalFrontCostPojo;
import com.example.android.quantitanti.viewmodels.CostListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TotalCostFragment extends Fragment implements CostAdapter.ItemClickListener {

    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private CostAdapter mAdapter;

    private CostDatabase mDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_total_cost, container, false);
        CLRecyclerView mRecyclerView = (CLRecyclerView) view.findViewById(R.id.recyclerViewCost);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //set emptyView
        mEmptyView = view.findViewById(R.id.emptyView);
        mRecyclerView.setEmptyView(mEmptyView);

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new CostAdapter(this, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Set the Floating Action Button (FAB) to its corresponding View.
         * Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         * to launch the AddCostActivity.
         */
        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddCostActivity.class);
            startActivity(intent);
        });

        mDb = CostDatabase.getInstance(requireActivity().getApplicationContext());

        setupViewModel();
    }

    private void setupViewModel() {
        CostListViewModelFactory factory = new CostListViewModelFactory(mDb);

        CostListViewModel viewModel = new ViewModelProvider(requireActivity(), factory).get(CostListViewModel.class);
        viewModel.getDailyExpenses().observe(getViewLifecycleOwner(), new Observer<List<TotalFrontCostPojo>>() {
            @Override
            public void onChanged(List<TotalFrontCostPojo> totalFrontCostPojos) {
                mAdapter.setmDailyExpenses(totalFrontCostPojos);
            }
        });
    }

    @Override
    public void onItemClickListener(String itemDate) {
        // Launch DailyExpenseActivity adding the itemDate as an extra in the intent
        Intent intent = new Intent(getActivity(), DailyExpensesActivity.class);
        intent.putExtra(DailyExpensesActivity.EXTRA_COST_DATE, itemDate);
        startActivity(intent);
    }

    @Override
    public void onItemLongClickListener(String itemDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle);
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
                        mDb.costDao().deleteDailyCosts(itemDate);
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
}

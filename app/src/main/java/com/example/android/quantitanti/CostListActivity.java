package com.example.android.quantitanti;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android.quantitanti.adapters.CostAdapter;
import com.example.android.quantitanti.adapters.SwitchFrontFragmentAdapter;
import com.example.android.quantitanti.database.CostDatabase;
import com.example.android.quantitanti.factories.CostListViewModelFactory;
import com.example.android.quantitanti.viewmodels.CostListViewModel;
import com.example.android.quantitanti.models.TotalFrontCostPojo;
import com.example.android.quantitanti.sharedpreferences.SettingsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CostListActivity extends AppCompatActivity {

    // Constant for logging
    private static final String TAG = CostListActivity.class.getSimpleName();

 //   constants for sp currency


    //todo NOVO
    private ViewPager2 viewPagerFragment;
    private SwitchFrontFragmentAdapter switchAdapter;
    TabLayout tabLayoutFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cost_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_cl);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextAppearance(this, R.style.AppTitle);

        //todo ovo ostaje u CostListActivityu -> setup fragmentPager
        viewPagerFragment = findViewById(R.id.fragment_pager);
        switchAdapter = new SwitchFrontFragmentAdapter(this);
        viewPagerFragment.setAdapter(switchAdapter);

        tabLayoutFragments = findViewById(R.id.tablayout_fragments);
        new TabLayoutMediator(tabLayoutFragments, viewPagerFragment, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("DAILY TOTAL");
                } else if (position == 1) {
                    tab.setText("ALL COSTS");
                }
            }
        }).attach();
   }


    @Override
    public void onBackPressed() {
        if (viewPagerFragment.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPagerFragment.setCurrentItem(viewPagerFragment.getCurrentItem() - 1);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cost_list, menu);
        return super.onCreateOptionsMenu(menu);
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



}

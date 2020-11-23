package com.example.android.quantitanti;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android.quantitanti.adapters.SwitchFrontFragmentAdapter;
import com.example.android.quantitanti.sharedpreferences.SettingsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CostListActivity extends AppCompatActivity {

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

        //this stays in CostListActivity -> setup fragmentPager
        viewPagerFragment = findViewById(R.id.fragment_pager);
        switchAdapter = new SwitchFrontFragmentAdapter(this);
        viewPagerFragment.setAdapter(switchAdapter);

        tabLayoutFragments = findViewById(R.id.tablayout_fragments);
        new TabLayoutMediator(tabLayoutFragments, viewPagerFragment, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("DAILY TOTALS");
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
        if (id == R.id.action_legal) {
            String privacyPolicyUrl = "https://sites.google.com/view/quantitanti-legal";
            openWebPage(privacyPolicyUrl);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openWebPage(String privacyPolicyUrl) {
        Uri uri = Uri.parse(privacyPolicyUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}

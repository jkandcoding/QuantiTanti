package com.example.android.quantitanti.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.android.quantitanti.fragments.AllCostsFragment;
import com.example.android.quantitanti.fragments.TotalCostFragment;

public class SwitchFrontFragmentAdapter extends FragmentStateAdapter {
    public SwitchFrontFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
//    public SwitchFrontFragmentAdapter(@NonNull Fragment fragment) {
//        super(fragment);
//    }
//    public SwitchFrontFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
//        super(fragmentManager, lifecycle);
//    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TotalCostFragment();
            case 1:
                return new AllCostsFragment();
        }
        return new TotalCostFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}

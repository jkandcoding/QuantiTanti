package com.example.android.quantitanti.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.R;
import com.example.android.quantitanti.adapters.CurrencyAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CurrencyFragment extends DialogFragment implements CurrencyAdapter.ItemClickListener {

    public Set<Currency> allAvailableCurrenciesSet = new TreeSet<>(new SbufferComparator());
    List<String> allCurrencies = new ArrayList<>();
    List<String> allCurrenciesCode = new ArrayList<>();
    public TextView tv_spinnerTittle;
    private RecyclerView rv_currenyRecyclerView;
    private Button btn_currencyCancel;

    private CurrencyAdapter currencyAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.currency_popup, container, false);

        allAvailableCurrenciesSet.addAll(Currency.getAvailableCurrencies());
        for (Currency currency : allAvailableCurrenciesSet) {
            allCurrencies.add(currency.getCurrencyCode() + " - " + currency.getDisplayName());
            allCurrenciesCode.add(currency.getCurrencyCode());
        }
        tv_spinnerTittle = v.findViewById(R.id.tv_spinnerTittle);
        rv_currenyRecyclerView = v.findViewById(R.id.rv_currenyRecyclerView);
        btn_currencyCancel = v.findViewById(R.id.btn_currencyCancel);

        layoutManager = new LinearLayoutManager(getContext());
        rv_currenyRecyclerView.setLayoutManager(layoutManager);
        currencyAdapter = new CurrencyAdapter(allCurrencies, allCurrenciesCode, this);
        rv_currenyRecyclerView.setAdapter(currencyAdapter);

        dismissDialog();

        return v;
    }

    public interface OnDataPassCurr {
        void onDataPassCurr(String currencyCode);
    }

    private OnDataPassCurr dataPassCurr;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dataPassCurr = (OnDataPassCurr) context;
    }

    @Override
    public void onItemClickListener(String currencyCode) {
        dataPassCurr.onDataPassCurr(currencyCode);
        dismiss();
    }

    static class SbufferComparator implements Comparator<Currency> {

        @Override
        public int compare(Currency s1, Currency s2) {
            return s1.getCurrencyCode().compareTo(s2.getCurrencyCode());
        }
    }

    public void dismissDialog() {
        btn_currencyCancel.setOnClickListener(view -> dismiss());
    }

}

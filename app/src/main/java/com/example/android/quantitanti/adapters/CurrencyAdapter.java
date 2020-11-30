package com.example.android.quantitanti.adapters;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quantitanti.R;

import java.util.List;

import static android.graphics.Typeface.BOLD;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrenyViewHolder> {

    final private List<String> allCurrencies;
    final private List<String> allCurrenciesCode;
    final private ItemClickListener mItemClickListener;

    public CurrencyAdapter(List<String> allCurrencies, List<String> allCurrenciesCode, ItemClickListener mItemClickListener) {
        this.allCurrencies = allCurrencies;
        this.allCurrenciesCode = allCurrenciesCode;
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public CurrenyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.one_currency_item_view, parent, false);
        return new CurrenyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrenyViewHolder holder, int position) {
        String currency = allCurrencies.get(position);
        String currencyCode = allCurrenciesCode.get(position);
        SpannableStringBuilder sb = new SpannableStringBuilder(currency);
        StyleSpan b = new StyleSpan(BOLD);
        sb.setSpan(b, 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        holder.tv_currencyItem.setText(sb);
    }

    @Override
    public int getItemCount() {
        if (allCurrencies == null) {
            return 0;
        }
        return allCurrencies.size();
    }


    public interface ItemClickListener {
        void onItemClickListener(String currencyCode);
    }

    public class CurrenyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tv_currencyItem;

        public CurrenyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_currencyItem = itemView.findViewById(R.id.tv_currencyItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String currencyCode = allCurrenciesCode.get(getAbsoluteAdapterPosition());
            mItemClickListener.onItemClickListener(currencyCode);
        }
    }


}

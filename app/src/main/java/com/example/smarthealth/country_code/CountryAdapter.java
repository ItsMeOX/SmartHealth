package com.example.smarthealth.country_code;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smarthealth.R;

import java.util.List;

public class CountryAdapter extends BaseAdapter {
    private Context context;
    private List<CountryItem> countryList;

    public CountryAdapter(Context context, List<CountryItem> countryList) {
        this.context = context;
        this.countryList = countryList;
    }

    @Override
    public int getCount() {
        return countryList.size();
    }

    @Override
    public Object getItem(int position) {
        return countryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.country_spinner, parent, false);
        }

        ImageView flagIcon = convertView.findViewById(R.id.flagIcon);
        TextView countryCode = convertView.findViewById(R.id.countryCode);

        CountryItem countryItem = countryList.get(position);
        flagIcon.setImageResource(countryItem.getFlagImage());
        countryCode.setText(countryItem.getCountryCode());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}

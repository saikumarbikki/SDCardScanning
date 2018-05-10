package com.scanning.sdcard.sdcardscanning.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.scanning.sdcard.sdcardscanning.R;
import com.scanning.sdcard.sdcardscanning.common.FileReport;
import com.scanning.sdcard.sdcardscanning.model.AdapterModel;

import java.util.List;

public class ReportListAdapter extends ArrayAdapter {
    //to reference the Activity
    private final Activity context;
    //to store the list of countries
    private final List<AdapterModel> listItems;

    public ReportListAdapter(Activity context, List<AdapterModel> listItems) {
        super(context, R.layout.listview_row, listItems);
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.listview_row, null);

        //this code gets references to objects in the listview_row.xml file
        TextView infoField = (TextView) rowView.findViewById(R.id.info_col);
        TextView descField = (TextView) rowView.findViewById(R.id.desc_col);
        TextView titleField = (TextView) rowView.findViewById(R.id.main_title);

        //this code sets the values of the objects to values from the arrays
        AdapterModel amd = listItems.get(position);
        infoField.setText(amd.info);
        descField.setText(amd.desc);
        titleField.setText(amd.title);

        if (amd.title.equals("")) {
            titleField.setVisibility(View.GONE);
        }
        if (amd.desc.equals("")) {
            descField.setVisibility(View.GONE);
        }
        if (amd.info.equals("")) {
            infoField.setVisibility(View.GONE);
        }

        FileReport.csvReport = FileReport.csvReport + amd.title + ", " + amd.info + ", " + amd.desc + "\r\n";
        return rowView;

    }

}

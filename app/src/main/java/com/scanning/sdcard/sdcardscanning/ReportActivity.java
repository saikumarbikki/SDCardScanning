package com.scanning.sdcard.sdcardscanning;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.scanning.sdcard.sdcardscanning.adapter.ReportListAdapter;
import com.scanning.sdcard.sdcardscanning.common.FileReport;
import com.scanning.sdcard.sdcardscanning.model.AdapterModel;
import com.scanning.sdcard.sdcardscanning.model.FileDescription;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    BottomNavigationView mBottomNavigationView;
    Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);
        context = this;
        List<AdapterModel> listItems = new ArrayList<AdapterModel>();
        //Average File Size Info
        listItems.add(new AdapterModel(String.valueOf(new DecimalFormat("#.##").format(FileReport.averageFileSize)) + " MB", "Average file size", ""));
        //Max File Size Info
        listItems.add(new AdapterModel("", "", ""));
        listItems.add(new AdapterModel("", "Max File Size Lists", ""));
        listItems.add(new AdapterModel("", "", ""));
        for (int mxf = 0; mxf < FileReport.fileMaxSize.size(); mxf++) {
            FileDescription fdm = FileReport.fileMaxSize.get(mxf);
            //System.out.println("File Name "+fdm.getFileName());
            //System.out.println("File Size in MB "+fdm.getFileSizeInMb());
            listItems.add(new AdapterModel(String.valueOf(new DecimalFormat("#.##").format(fdm.getFileSizeInMb())) + " MB", "", fdm.getFileName()));
        }
        //Frequent used File
        listItems.add(new AdapterModel("", "", ""));
        listItems.add(new AdapterModel("", "Frequent used File extensions..", ""));
        listItems.add(new AdapterModel("", "", ""));
        for (Map.Entry<String, Integer> entry : FileReport.frequentUsedFile) {
            listItems.add(new AdapterModel(String.valueOf(entry.getKey()), "", String.valueOf(entry.getValue()) + " Count"));
        }

        ReportListAdapter whatever = new ReportListAdapter(this, listItems);
        ListView listView = (ListView) findViewById(R.id.report_view);
        listView.setAdapter(whatever);
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_share:
                        shareReport();
                        return true;
                }
                return false;
            }
        });
    }

    private void shareReport() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String body = FileReport.csvReport;
        String subject = "SD Card Scan Reportt";
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        Intent.createChooser(shareIntent, "Share Scan Report..");
        startActivity(shareIntent);
    }
}

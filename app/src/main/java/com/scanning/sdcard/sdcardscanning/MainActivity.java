package com.scanning.sdcard.sdcardscanning;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scanning.sdcard.sdcardscanning.common.FileReport;
import com.scanning.sdcard.sdcardscanning.model.ScanStatusModel;
import com.scanning.sdcard.sdcardscanning.service.ScanService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String SCAN_REPORT = "SCAN_REPORT";
    private Intent localservice;
    TextView file_scan_view;
    TextView sd_card_info;
    Button buttonStart;
    Button buttonStop;
    ProgressBar progressBar;

    private boolean isExternalDriveReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localservice = new Intent(this, ScanService.class);
        file_scan_view = findViewById(R.id.file_scan_view);
        sd_card_info = findViewById(R.id.sd_card_info);
        progressBar = findViewById(R.id.progressBar);
        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);
        FileReport.scanningState = FileReport.NONE;
        FileReport.csvReport = "";

        if (isExternalDriveReady == false) {
            isExternalDriveReady = FileReport.isExternalStorageAvailable();
            String statusMessage = isExternalDriveReady == true ? "External Device is Ready!!" : "No External SD card found!!";
            sd_card_info.setText(statusMessage);
            if (isExternalDriveReady == false)
                FileReport.scanningState = FileReport.NOT_READY;
        }
        setElementViewState();
        Log.v("MainActivity", "OnCreate Called!!!!!");
    }

    private void setElementViewState() {
        if (FileReport.scanningState == FileReport.NONE) {
            buttonStart.setEnabled(true);
            buttonStop.setEnabled(false);
            progressBar.setVisibility(View.GONE);
        } else if (FileReport.scanningState == FileReport.START) {
            buttonStart.setEnabled(false);
            buttonStop.setEnabled(true);
            progressBar.setVisibility(View.VISIBLE);
        } else if (FileReport.scanningState == FileReport.STOP) {
            buttonStart.setEnabled(true);
            buttonStop.setEnabled(false);
        } else if (FileReport.scanningState == FileReport.NOT_READY) {
            buttonStart.setEnabled(false);
            buttonStop.setEnabled(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FileReport.scanningState = FileReport.BACK;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resume ");
        registerReceiver(scanServiceReceiver, new IntentFilter(ScanService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, " Pause");
        if (FileReport.scanningState == FileReport.BACK) {
            FileReport.scanningState = FileReport.STOP;
            setElementViewState();
            stopService(localservice);
            unregisterReceiver(scanServiceReceiver);
        }
    }

    private void scanStatusNotification(String contentTitle, String contentText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        Intent intent = new Intent(this, ReportActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private BroadcastReceiver scanServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressBarActivity(intent);
        }
    };

    public void startScan(View view) {
        FileReport.scanningState = FileReport.START;
        setElementViewState();
        startService(localservice);
    }

    public void resumeScan(View view) {

    }

    public void displayFileScanReport() {
        try {
            scanStatusNotification("SD Card Scan Completed!!", "Report is Ready for Viewing");
            FileReport.scanningState = FileReport.NONE;
            setElementViewState();
            Intent intent = new Intent(this, ReportActivity.class);
            this.startActivity(intent);
        } catch (Exception er) {

        }
    }

    private void progressBarActivity(Intent intent) {
        if (isExternalDriveReady == true) {
            try {
                ScanStatusModel scanStatus = (ScanStatusModel) intent.getParcelableExtra(SCAN_REPORT);
                if (scanStatus != null) {
                    if (scanStatus.scanning == 1) {
                        progressBar.setProgress(scanStatus.progrssPercentage);
                        file_scan_view.setText(scanStatus.scanMessage);
                    }
                    if (scanStatus.fileReportInit == 1) {
                        file_scan_view.setText(scanStatus.scanMessage);
                    }
                    if (scanStatus.fileReportReady == 1) {
                        displayFileScanReport();
                        stopService(localservice);
                    }
                }
            } catch (Exception er) {
                Log.d(TAG, er.toString());
            }
        }
    }

    public void stopScan(View view) {
        FileReport.scanningState = FileReport.STOP;
        setElementViewState();
        stopService(localservice);
    }
}

package com.scanning.sdcard.sdcardscanning.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.scanning.sdcard.sdcardscanning.model.FileDescription;
import com.scanning.sdcard.sdcardscanning.model.FileFrequentUsed;
import com.scanning.sdcard.sdcardscanning.common.FileReport;
import com.scanning.sdcard.sdcardscanning.model.ScanStatusModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScanService  extends Service {
    private static final String TAG = "ScanService";
    public static final String BROADCAST_ACTION = "com.scanning.sdcard.sdcardscanning.filescanning";
    public static final String SCAN_REPORT = "SCAN_REPORT";
    public static final String FULL_REPORT = "FULL_REPORT";
    private final Handler handler = new Handler();
    Intent intent;

    private List<FileDescription> fileDescription;
    private List<FileFrequentUsed> fileFrequentUsed;
    private boolean isExternalDriveReady = false;
    private FileReport fileReport;
    private Thread fileReadThread;

    public int fileCounter = 0;
    private int progrssPercentage = 0;
    private boolean scanComplete = false;
    public int MAX_FILE_SIZE_LOOP = 10;
    public int MAX_FREQUENT_FILE_LOOP = 5;

    private ScanStatusModel scanStatus;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
        scanStatus = new ScanStatusModel();
        scanStatus.scanning = 1;
        Runnable runnableProcess = new Runnable(){
            public void run(){
                initFileScan();
            }
        };
        fileReadThread = new Thread(runnableProcess);
        fileReadThread.start();
    }

    public void initFileScan() {
        File extStore = Environment.getExternalStorageDirectory();
        fileDescription = new ArrayList<FileDescription>();
        fileFrequentUsed = new ArrayList<FileFrequentUsed>();
        scanAllExternalFileList(extStore);
    }
    private double calculateAverageFileSize(List <FileDescription> filed) {
        double sum = 0;
        if(!filed.isEmpty()) {
            for (FileDescription fildInfo : filed) {
                sum += fildInfo.getFileSizeInMb();
            }
            return sum / filed.size();
        }
        return sum;
    }

    private HashMap<String, Integer> calculateFrequentUsedFiles() {
        double sum = 0;
        Collections.sort(fileFrequentUsed);
        HashMap<String, Integer> frquentMap = new HashMap<String, Integer>();
        if(!fileFrequentUsed.isEmpty()) {

            for (FileFrequentUsed fildInfo : fileFrequentUsed) {
                String key = fildInfo.getFileExtension();
                if(frquentMap.containsKey(key)){
                    String frequencyVal = frquentMap.get(key).toString();
                    int val = Integer.parseInt(frequencyVal) + 1;
                    frquentMap.put(key, val);
                }
                else{
                    frquentMap.put(key, 1);
                }
            }
        }
        return frquentMap;
    }

    private void startFileReportProcess() {
        Collections.sort(fileDescription);

        /*Start -- Get Avg file Size Info*/
        double avgFileSize = calculateAverageFileSize(fileDescription);
        /*End -- Get Avg file Size Info*/

        /*Start -- Get Frequent File Info*/
        HashMap<String, Integer> frequentUserFiles = calculateFrequentUsedFiles();
        Set<Map.Entry<String, Integer>> set = frequentUserFiles.entrySet();
        List<Map.Entry<String, Integer>> frequentList = new ArrayList<Map.Entry<String, Integer>>(
                set);
        Collections.sort(frequentList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        if(frequentList.size() < MAX_FREQUENT_FILE_LOOP)
            MAX_FREQUENT_FILE_LOOP = frequentList.size();
        List<Map.Entry<String, Integer>> filteredFrequentList = frequentList.subList(0, MAX_FREQUENT_FILE_LOOP);
        /*End -- Get Frequent File Info*/

        /*Start -- Get 10 max file size info*/
        if(fileDescription.size() < MAX_FILE_SIZE_LOOP)
            MAX_FILE_SIZE_LOOP = fileDescription.size();
        List<FileDescription>  maxFileSizeList = fileDescription.subList(0, MAX_FILE_SIZE_LOOP);
        /*End -- Get 10 max file size info*/

        fileReport = new FileReport(maxFileSizeList, avgFileSize, filteredFrequentList);

        scanStatus.fileReportInit = 0;
        scanStatus.fileReportReady = 1;
    }

    public void scanAllExternalFileList(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                scanAllExternalFileList(fileEntry);
            } else {
                String fileName = fileEntry.getName();
                String filePath = fileEntry.getPath();
                long lastModifiedDate = fileEntry.lastModified();
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
                double fileSizeInMb = getFileSizeMegaBytes(fileEntry);
                double fileSizeInKb = getFileSizeKiloBytes(fileEntry);
                double fileSizeInBytes = getFileSizeBytes(fileEntry);
                Calendar cal1 = Calendar.getInstance();
                fileDescription.add(new FileDescription(fileName, filePath, fileSizeInMb, fileSizeInKb, fileSizeInBytes));
                long diff = Math.abs(cal1.getTimeInMillis() - lastModifiedDate);
                long diffDays = diff / (24 * 60 * 60 * 1000);
                if(diffDays < 5){
                    fileFrequentUsed.add(new FileFrequentUsed(fileExtension, lastModifiedDate));
                }
            }
        }
    }

    private double getFileSizeMegaBytes(File file) {
        return (double) file.length() / (1024 * 1024);
    }

    private double getFileSizeKiloBytes(File file) {
        return (double) file.length() / 1024;
    }

    private double getFileSizeBytes(File file) {
        return file.length();
    }


    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            displayFileScanningProgress();
            handler.postDelayed(this, 100); // 10 seconds
        }
    };

    private void displayFileScanningProgress() {

        if(isExternalDriveReady == true) {
            if(scanStatus.scanning == 1) {
                scanStatus.progrssPercentage = ((scanStatus.fileCounter * 100) / fileDescription.size());
                scanStatus.scanMessage = fileDescription.get(scanStatus.fileCounter -1).getFilePath();
                intent.putExtra(SCAN_REPORT, scanStatus);
                sendBroadcast(intent);
                if(scanStatus.fileCounter == fileDescription.size()){
                    scanStatus.scanning = 0;
                    scanStatus.scanCompleted = 1;
                }
                scanStatus.fileCounter++;
            }
            if(scanStatus.scanCompleted == 1 && scanStatus.fileReportInit == 0){
                scanStatus.scanMessage = "Preparing Report!! Please wait..";
                scanStatus.fileReportInit = 1;
                intent.putExtra(SCAN_REPORT, scanStatus);
                sendBroadcast(intent);
                startFileReportProcess();
            }
            if(scanStatus.fileReportReady == 1) {
                intent.putExtra(SCAN_REPORT, scanStatus);
                sendBroadcast(intent);
            }

        }
        else{
            isExternalDriveReady = FileReport.isExternalStorageAvailable();
            Log.d(TAG, "entered displayFileScanningProgress "+isExternalDriveReady);
            intent.putExtra("drive_status", String.valueOf(isExternalDriveReady));
            sendBroadcast(intent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(sendUpdatesToUI);
    }
}

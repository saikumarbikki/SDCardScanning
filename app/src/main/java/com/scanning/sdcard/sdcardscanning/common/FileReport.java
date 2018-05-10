package com.scanning.sdcard.sdcardscanning.common;

import android.os.Environment;

import com.scanning.sdcard.sdcardscanning.model.FileDescription;

import java.util.List;
import java.util.Map;

public class FileReport {

    public static List<FileDescription> fileMaxSize;
    public static double averageFileSize;
    public static List<Map.Entry<String, Integer>> frequentUsedFile;
    public static int scanningState = 0;
    public static final int NONE = 0;
    public static final int START = 1;
    public static final int STOP = 2;
    public static final int PAUSE = 3;
    public static final int HOME = 4;
    public static final int BACK = 5;
    public static final int NOT_READY = 6;
    public static String csvReport;

    public FileReport(List<FileDescription> fileMaxSize, double averageFileSize, List<Map.Entry<String, Integer>> frequentUsedFile) {
        FileReport.averageFileSize = averageFileSize;
        FileReport.fileMaxSize = fileMaxSize;
        FileReport.frequentUsedFile = frequentUsedFile;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

}

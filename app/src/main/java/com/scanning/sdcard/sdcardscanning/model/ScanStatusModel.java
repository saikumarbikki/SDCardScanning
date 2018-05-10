package com.scanning.sdcard.sdcardscanning.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ScanStatusModel implements Parcelable {
    public int scanning;
    public int scanCompleted;
    public int fileReportInit;
    public int fileReportReady;
    public String scanMessage;
    public int progrssPercentage = 0;
    public int fileCounter = 1;

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public ScanStatusModel() {
    }

    private ScanStatusModel(Parcel in) {
        this.scanning = in.readInt();
        this.scanCompleted = in.readInt();
        this.fileReportInit = in.readInt();
        this.fileReportReady = in.readInt();
        this.scanMessage = in.readString();
        this.progrssPercentage = in.readInt();
        this.fileCounter = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(scanning);
        dest.writeInt(scanCompleted);
        dest.writeInt(fileReportInit);
        dest.writeInt(fileReportReady);
        dest.writeString(scanMessage);
        dest.writeInt(progrssPercentage);
        dest.writeInt(fileCounter);
    }

    public static final Parcelable.Creator<ScanStatusModel> CREATOR = new Parcelable.Creator<ScanStatusModel>() {

        @Override
        public ScanStatusModel createFromParcel(Parcel source) {
            return new ScanStatusModel(source);
        }

        @Override
        public ScanStatusModel[] newArray(int size) {
            return new ScanStatusModel[size];
        }
    };
}

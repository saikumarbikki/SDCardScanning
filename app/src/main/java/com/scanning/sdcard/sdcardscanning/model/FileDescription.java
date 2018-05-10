package com.scanning.sdcard.sdcardscanning.model;

public class FileDescription implements Comparable<FileDescription> {

    public FileDescription(String fileName, String filePath,
                           double fileSizeInMb,
                           double fileSizeInKb,
                           double fileSizeInBytes) {
        this.fileName = fileName;
        this.fileSizeInMb = fileSizeInMb;
        this.fileSizeInKb = fileSizeInKb;
        this.fileSizeInBytes = fileSizeInBytes;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private String filePath;

    public double getFileSizeInMb() {
        return fileSizeInMb;
    }

    public void setFileSizeInMb(double fileSizeInMb) {
        this.fileSizeInMb = fileSizeInMb;
    }

    private double fileSizeInMb;

    public double getFileSizeInKb() {
        return fileSizeInKb;
    }

    public void setFileSizeInKb(double fileSizeInKb) {
        this.fileSizeInKb = fileSizeInKb;
    }

    private double fileSizeInKb;

    public double getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public void setFileSizeInBytes(double fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
    }

    private double fileSizeInBytes;

    @Override
    public int compareTo(FileDescription o) {
        double compareamb = ((FileDescription) o).getFileSizeInMb();
        return Double.compare(compareamb, this.fileSizeInMb);
    }

}

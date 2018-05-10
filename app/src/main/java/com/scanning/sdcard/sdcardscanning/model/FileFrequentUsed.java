package com.scanning.sdcard.sdcardscanning.model;

public class FileFrequentUsed implements Comparable<FileFrequentUsed> {

    public FileFrequentUsed(String fileExtension, long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        this.fileExtension = fileExtension;
    }

    public long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    private long lastModifiedDate;

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    private String fileExtension;

    @Override
    public int compareTo(FileFrequentUsed o) {
        long compareadt = ((FileFrequentUsed) o).getLastModifiedDate();
        return Double.compare(compareadt, this.lastModifiedDate);
    }
}

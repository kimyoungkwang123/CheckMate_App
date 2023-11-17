package com.example.moble_project.test.util;

public class fileitem {
    String fileurl;
    String filename;

    public fileitem() {

    }

    public fileitem(String fileurl, String filename) {
        this.fileurl = fileurl;
        this.filename = filename;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}

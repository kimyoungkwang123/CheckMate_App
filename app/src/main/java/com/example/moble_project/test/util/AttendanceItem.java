package com.example.moble_project.test.util;
public class AttendanceItem {
    private String name;
    private String entryDate;
    private String entryTime;
    private String exitDate;
    private String exitTime;

    public AttendanceItem(String name, String entryDate, String entryTime) {
        this.name = name;
        this.entryDate = entryDate;
        this.entryTime = entryTime;
    }

    public AttendanceItem() {
    }
    // setters and getters...

    public void setName(String name){
        this.name = name;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate; // corrected from exitDate
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime; // corrected from exitTime
    }

    public void setExitDate(String exitDate) {
        this.exitDate = exitDate;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public String getExitDate() {
        return exitDate; // this can be null
    }

    public String getExitTime() {
        return exitTime; // this can be null
    }
}
package com.example.moble_project.test.util;

public class InoutItem {
    private String name;
    private String outTime;
    private String outDate;
    private String returnTime;
    private String returnDate;
    private String reason;

    public InoutItem(String name, String returnTime, String returnDate) {
        this.name = name;
        this.returnTime = returnTime;
        this.returnDate = returnDate;
    }

    public InoutItem() {
    }
    // setters and getters...

    public void setName(String name){
        this.name = name;
    }


    public void setOutTime(String outingTime) {
        this.outTime = outingTime;
    }


    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    // Getters
    public String getName() {
        return name;
    }


    public String getOutTime() {
        return outTime;
    }

    public String getReturnTime() {
        return returnTime; // this can be null
    }

    public String getReason() {
        return reason; // this can be null
    }

    public String getOutDate() {
        return outDate;
    }

    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}
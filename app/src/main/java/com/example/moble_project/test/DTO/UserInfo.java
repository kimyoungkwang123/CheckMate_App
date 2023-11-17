package com.example.moble_project.test.DTO;

public class UserInfo {

    private String no;
    private String name;
    private String email;
    private String phone;
    private String grade;
    private String state;
    private String url;
    private String clickBtn;
    private String reason;

    public UserInfo(String no, String name, String email, String phone, String grade, String state, String url) {
        this.no = no;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.grade = grade;
        this.state = state;
        this.url = url;
    }

    public UserInfo(String no, String name, String email, String phone, String grade, String state, String url, String clickBtn) {
        this.no = no;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.grade = grade;
        this.state = state;
        this.url = url;
        this.clickBtn = clickBtn;
    }


    public UserInfo() {
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClickBtn() {
        return clickBtn;
    }

    public void setClickBtn(String clickBtn) {
        this.clickBtn = clickBtn;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

package com.example.moble_project.test.util;


public class CustomItem {
    private String title;
    private String content;
    private String eno;
    private String idx;

    private String file_NAME;

    private String file_URL;
    private String noticeyn;
    private String employee_reply_name;
    private String reply_content;
    private String reply_update_time;
    private String reply_no;
    private String view_cnt;

    // 생성자

    public CustomItem(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public CustomItem(){}


    // 게터 메소드
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
    public String getEno() {
        return eno;
    }
    public String getNoticeyn(){
        return noticeyn;
    }
    public String getIdx(){
        return idx;
    }

    public String getFile_NAME(){ return file_NAME; }

    public String getFile_URL(){ return file_URL; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setEno(String eno){
        this.eno = eno;
    }
    public void setNoticeyn(String noticeyn){
        this.noticeyn = noticeyn;
    }
    public void setIdx(String idx){
        this.idx = idx;
    }
    public void setFile_NAME(String file_NAME){ this.file_NAME = file_NAME; }

    public void setFile_URL(String file_URL){ this.file_URL = file_URL; }

    public String getEmployee_reply_name() {
        return employee_reply_name;
    }

    public void setEmployee_reply_name(String employee_reply_name) {
        this.employee_reply_name = employee_reply_name;
    }

    public String getReply_content() {
        return reply_content;
    }

    public void setReply_content(String reply_content) {
        this.reply_content = reply_content;
    }

    public String getReply_update_time() {
        return reply_update_time;
    }

    public void setReply_update_time(String reply_update_time) {
        this.reply_update_time = reply_update_time;
    }

    public String getReply_no() {
        return reply_no;
    }

    public void setReply_no(String reply_no) {
        this.reply_no = reply_no;
    }

    public String getView_cnt() {
        return view_cnt;
    }

    public void setView_cnt(String view_cnt) {
        this.view_cnt = view_cnt;
    }
}
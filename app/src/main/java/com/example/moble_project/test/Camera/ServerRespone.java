package com.example.moble_project.test.Camera;

public class ServerRespone {
    String msg;
    String clickBtn;
    String reason;

    public ServerRespone() {
    }

    public ServerRespone(String msg, String clickBtn) {
        this.msg = msg;
        this.clickBtn = clickBtn;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

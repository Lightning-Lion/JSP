package com.hoarp.web.data;

public class CaptchaData {
    private String captcha;
    private Long timestamp;

    public CaptchaData() {}

    public CaptchaData(String captcha, Long timestamp) {
        this.captcha = captcha;
        this.timestamp = timestamp;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}

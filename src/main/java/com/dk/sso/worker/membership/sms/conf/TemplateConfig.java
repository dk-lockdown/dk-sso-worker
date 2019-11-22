package com.dk.sso.worker.membership.sms.conf;

public class TemplateConfig {
    private String templateCode;

    /**
     * 过期分钟数
     */
    private Integer expireIn;

    /**
     * 重试间隔，单位秒
     */
    private Integer retryDuration;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public Integer getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(Integer expireIn) {
        this.expireIn = expireIn;
    }

    public Integer getRetryDuration() {
        return retryDuration;
    }

    public void setRetryDuration(Integer retryDuration) {
        this.retryDuration = retryDuration;
    }
}

package com.dk.sso.worker.membership.sms.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sms")
public class SmsConfig {
    public String url;

    private String systemCode;

    private Integer totalTimesEachDay;

    /**
     * 快捷登录短信
     */
    private TemplateConfig kjdldxTemplate;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public Integer getTotalTimesEachDay() {
        return totalTimesEachDay;
    }

    public void setTotalTimesEachDay(Integer totalTimesEachDay) {
        this.totalTimesEachDay = totalTimesEachDay;
    }

    public TemplateConfig getKjdldxTemplate() {
        return kjdldxTemplate;
    }

    public void setKjdldxTemplate(TemplateConfig kjdldxTemplate) {
        this.kjdldxTemplate = kjdldxTemplate;
    }
}

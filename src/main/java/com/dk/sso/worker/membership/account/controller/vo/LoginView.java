package com.dk.sso.worker.membership.account.controller.vo;

import java.util.Map;

public class LoginView {
    private String appName;

    private Map<String,String> parameters;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}

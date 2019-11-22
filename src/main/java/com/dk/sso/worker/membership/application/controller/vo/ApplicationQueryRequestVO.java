package com.dk.sso.worker.membership.application.controller.vo;

import com.dk.foundation.engine.baseentity.Pager;
import lombok.Data;

@Data
public class ApplicationQueryRequestVO extends Pager {
    private String appId;
}

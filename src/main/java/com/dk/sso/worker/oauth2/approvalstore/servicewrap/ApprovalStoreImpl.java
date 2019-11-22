package com.dk.sso.worker.oauth2.approvalstore.servicewrap;

import com.dk.sso.worker.oauth2.approvalstore.entity.Approval;
import com.dk.sso.worker.oauth2.approvalstore.entity.ApprovalData;
import com.dk.sso.worker.oauth2.approvalstore.service.ApprovalStoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service("approvalSore")
public class ApprovalStoreImpl implements ApprovalStore {

    @Resource
    private ApprovalStoreService approvalStoreService;

    @Override
    public boolean addApprovals(Collection<Approval> approvals) {
        if (CollectionUtils.isEmpty(approvals)) {
            return false;
        }
        List<ApprovalData> approvalDatas = getFromApprovals(approvals);
        return approvalStoreService.addApprovals(approvalDatas);
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> approvals) {
        if (CollectionUtils.isEmpty(approvals)) {
            return false;
        }
        List<ApprovalData> approvalDatas = getFromApprovals(approvals);
        return approvalStoreService.revokeApprovals(approvalDatas);
    }

    @Override
    public Collection<Approval> getApprovals(String userId, String clientId) {
        return getFromApprovalData(approvalStoreService.getApprovalStores(userId, clientId));
    }

    private List<ApprovalData> getFromApprovals(Collection<Approval> approvals) {
        List<ApprovalData> approvalDatas = new ArrayList<>();
        for (Approval approval : approvals) {
            ApprovalData approvalData = new ApprovalData();
            BeanUtils.copyProperties(approval, approvalData);
            approvalData.setStatus(approval.getStatus().name());
            approvalDatas.add(approvalData);
        }
        return approvalDatas;
    }

    private List<Approval> getFromApprovalData(Collection<ApprovalData> approvalDatas) {
        if (CollectionUtils.isEmpty(approvalDatas)) {
            return Collections.emptyList();
        }
        List<Approval> approvals = new ArrayList<>();
        for (ApprovalData approvalData : approvalDatas) {
            Approval approval = new Approval(approvalData.getUserId(), approvalData.getClientId(), approvalData.getScope(), approvalData.getExpiresAt(), Approval.ApprovalStatus.valueOf(approvalData.getStatus()), approvalData.getLastUpdatedAt());
            approvals.add(approval);
        }
        return approvals;
    }
}

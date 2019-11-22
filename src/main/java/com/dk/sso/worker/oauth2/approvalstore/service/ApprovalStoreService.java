package com.dk.sso.worker.oauth2.approvalstore.service;

import com.dk.sso.worker.oauth2.approvalstore.entity.ApprovalData;
import com.dk.sso.worker.oauth2.approvalstore.mapper.ApprovalStoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ApprovalStoreService {
    @Autowired
    private ApprovalStoreMapper approvalStoreMapper;

    public boolean addApprovals(List<ApprovalData> approvals) {
        boolean success = true;
        for (ApprovalData approval : approvals) {
            if (!updateApproval(approval)) {
                if (!addApproval(approval)) {
                    success = false;
                }
            }
        }
        return success;
    }

    public boolean revokeApprovals(Collection<ApprovalData> approvals) {
        boolean success = true;
        for (final ApprovalData approval : approvals) {
            int refreshed = approvalStoreMapper.deleteFromApprovalStore(approval);
            if (refreshed != 1) {
                success = false;
            }
        }
        return success;
    }

    public List<ApprovalData> getApprovalStores(String userId, String clientId) {
        return approvalStoreMapper.selectFromApprovalStore(userId, clientId);
    }

    private boolean updateApproval(final ApprovalData approval) {
        int refreshed = approvalStoreMapper.updateApprovalStore(approval);
        if (refreshed != 1) {
            return false;
        }
        return true;
    }

    private boolean addApproval(final ApprovalData approval) {
        int refreshed = approvalStoreMapper.insertApprovalStore(approval);
        if (refreshed != 1) {
            return false;
        }
        return true;
    }
}

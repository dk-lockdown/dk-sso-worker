package com.dk.sso.worker.oauth2.approvalstore.servicewrap;

import com.dk.sso.worker.oauth2.approvalstore.entity.Approval;

import java.util.Collection;

public interface ApprovalStore {
    public boolean addApprovals(Collection<Approval> approvals);

    public boolean revokeApprovals(Collection<Approval> approvals);

    public Collection<Approval> getApprovals(String userId, String clientId);
}

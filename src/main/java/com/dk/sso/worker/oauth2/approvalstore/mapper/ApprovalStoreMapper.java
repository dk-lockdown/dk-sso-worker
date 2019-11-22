package com.dk.sso.worker.oauth2.approvalstore.mapper;

import com.dk.sso.worker.oauth2.approvalstore.entity.ApprovalData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApprovalStoreMapper {
    int insertApprovalStore(ApprovalData approvalData);

    List<ApprovalData> selectFromApprovalStore(@Param("userId") String userId,@Param("clientId") String clientId);

    int deleteFromApprovalStore(ApprovalData approvalData);

    int updateApprovalStore(ApprovalData approvalData);
}

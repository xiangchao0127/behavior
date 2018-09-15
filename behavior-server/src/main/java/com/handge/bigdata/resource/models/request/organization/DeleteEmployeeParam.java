package com.handge.bigdata.resource.models.request.organization;

import java.util.List;

/**
 * @author liuqian
 * @date 2018/6/20
 * @Description:
 */
public class DeleteEmployeeParam {
    /**
     * 员工id集合
     */
    private List<String> accountIds;

    public List<String> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<String> accountIds) {
        this.accountIds = accountIds;
    }

    @Override
    public String toString() {
        return "DeleteEmployeeParam{" +
                "accountIds=" + accountIds +
                '}';
    }
}

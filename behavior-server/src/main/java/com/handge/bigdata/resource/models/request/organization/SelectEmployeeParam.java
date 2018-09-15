package com.handge.bigdata.resource.models.request.organization;

/**
 * @author liuqian
 * @date 2018/6/20
 * @Description:
 */
public class SelectEmployeeParam {
    /**
     * 账户id
     */
    private String accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "SelectEmployeeParam{" +
                "accountId='" + accountId + '\'' +
                '}';
    }
}

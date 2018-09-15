package com.handge.bigdata.resource.models.response.organization;

import java.io.Serializable;
import java.util.List;

/**
 * @author Liujuhao
 * @date 2018/6/19.
 */
public class RoleMemberManage implements Serializable{

    private static final long serialVersionUID = 7064326850039095658L;

    List<RoleMemberManageSection> selectedMemberList;

    List<RoleMemberManageSection> optionalMemberList;

    public List<RoleMemberManageSection> getSelectedMemberList() {
        return selectedMemberList;
    }

    public void setSelectedMemberList(List<RoleMemberManageSection> selectedMemberList) {
        this.selectedMemberList = selectedMemberList;
    }

    public List<RoleMemberManageSection> getOptionalMemberList() {
        return optionalMemberList;
    }

    public void setOptionalMemberList(List<RoleMemberManageSection> optionalMemberList) {
        this.optionalMemberList = optionalMemberList;
    }

    @Override
    public String toString() {
        return "RoleMemberManage{" +
                "selectedMemberList=" + selectedMemberList +
                ", optionalMemberList=" + optionalMemberList +
                '}';
    }
}

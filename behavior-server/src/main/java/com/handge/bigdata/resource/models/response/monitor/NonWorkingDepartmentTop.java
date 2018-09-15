package com.handge.bigdata.resource.models.response.monitor;

/**
 * 非工作性上网部门排名
 *
 * @author liuqian
 * @date 2018/4/25
 */
public class NonWorkingDepartmentTop {
    /**
     * 部门
     */
    private String department;
    /**
     * 部门非工作上网人数占比
     */
    private String ratioOfNonWorkingDepartment;
    /**
     * 总人数
     */
    private String numOfPerson;
    /**
     * 非工作性上网人数
     */
    private String numOfNonWorking;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRatioOfNonWorkingDepartment() {
        return ratioOfNonWorkingDepartment;
    }

    public void setRatioOfNonWorkingDepartment(String ratioOfNonWorkingDepartment) {
        this.ratioOfNonWorkingDepartment = ratioOfNonWorkingDepartment;
    }

    public String getNumOfPerson() {
        return numOfPerson;
    }

    public void setNumOfPerson(String numOfPerson) {
        this.numOfPerson = numOfPerson;
    }

    public String getNumOfNonWorking() {
        return numOfNonWorking;
    }

    public void setNumOfNonWorking(String numOfNonWorking) {
        this.numOfNonWorking = numOfNonWorking;
    }

    @Override
    public String toString() {
        return "NonWorkingDepartmentTop{" +
                "department='" + department + '\'' +
                ", ratioOfNonWorkingDepartment=" + ratioOfNonWorkingDepartment +
                ", numOfPerson=" + numOfPerson +
                ", numOfNonWorking=" + numOfNonWorking +
                '}';
    }
}

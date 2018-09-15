package com.handge.bigdata.dao.model;


import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Liujuhao
 * @date 2018/6/4.
 */
@Entity
@Table(name = "entity_employee_information_basic")
public class TableEntityEmployee implements Serializable {

    private static final long serialVersionUID = -1344397763125701293L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "number")
    private String number;

    @Column(name = "name")
    private String name;

    @Column(name = "hire_date")
    private Date hireData;

    @Column(name = "leave_date")
    private Date leaveDate;

    @Column(name = "status")
    private String status;

    @Column(name = "seniority")
    private BigDecimal seniority;

    @Column(name = "post_age")
    private BigDecimal postAge;

    @Column(name = "post")
    private String post;

    @Column(name = "positional_titles")
    private String positionalTitles;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "home_address")
    private String homeAddress;

    @Column(name = "id_card")
    private String idCard;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private TableEntityDepartment department;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private TableAuthAccount account;

    public long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getHireData() {
        return hireData;
    }

    public void setHireData(Date hireData) {
        this.hireData = hireData;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getSeniority() {
        return seniority;
    }

    public void setSeniority(BigDecimal seniorty) {
        this.seniority = seniorty;
    }

    public BigDecimal getPostAge() {
        return postAge;
    }

    public void setPostAge(BigDecimal postAge) {
        this.postAge = postAge;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPositionalTitles() {
        return positionalTitles;
    }

    public void setPositionalTitles(String positionalTitles) {
        this.positionalTitles = positionalTitles;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public TableEntityDepartment getDepartment() {
        return department;
    }

    public void setDepartment(TableEntityDepartment department) {
        this.department = department;
    }

    public TableAuthAccount getAccount() {
        return account;
    }

    public void setAccount(TableAuthAccount account) {
        this.account = account;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    @Override
    public String toString() {
        return "TableEntityEmployee{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", hireData=" + hireData +
                ", leaveDate=" + leaveDate +
                ", status='" + status + '\'' +
                ", seniority=" + seniority +
                ", postAge=" + postAge +
                ", post='" + post + '\'' +
                ", positionalTitles='" + positionalTitles + '\'' +
                ", createAt=" + createAt +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", homeAddress='" + homeAddress + '\'' +
                ", idCard='" + idCard + '\'' +
                ", department=" + department +
                ", account=" + account +
                '}';
    }
}

package com.flower.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "volunteer")
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(name = "id_card", length = 20)
    private String idCard;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "skills", length = 500)
    private String skills;

    @Column(name = "service_hours")
    private Integer serviceHours;

    @Column(name = "total_service_hours")
    private Integer totalServiceHours;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "application_time")
    private Date applicationTime;

    @Column(name = "approval_time")
    private Date approvalTime;

    @Column(name = "last_service_time")
    private Date lastServiceTime;

    @Column(name = "quit_time")
    private Date quitTime;

    @Transient
    private Integer rank;

    @PrePersist
    protected void onCreate() {
        applicationTime = new Date();
        if (serviceHours == null) serviceHours = 0;
        if (totalServiceHours == null) totalServiceHours = 0;
        if (status == null) status = "approved";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Integer getServiceHours() {
        return serviceHours;
    }

    public void setServiceHours(Integer serviceHours) {
        this.serviceHours = serviceHours;
    }

    public Integer getTotalServiceHours() {
        return totalServiceHours;
    }

    public void setTotalServiceHours(Integer totalServiceHours) {
        this.totalServiceHours = totalServiceHours;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(Date applicationTime) {
        this.applicationTime = applicationTime;
    }

    public Date getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
    }

    public Date getLastServiceTime() {
        return lastServiceTime;
    }

    public void setLastServiceTime(Date lastServiceTime) {
        this.lastServiceTime = lastServiceTime;
    }

    public Date getQuitTime() {
        return quitTime;
    }

    public void setQuitTime(Date quitTime) {
        this.quitTime = quitTime;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
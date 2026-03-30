package com.flower.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "member_benefit_usage")
public class MemberBenefitUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "member_record_id")
    private Integer memberRecordId;

    @Column(name = "benefit_code", length = 50, nullable = false)
    private String benefitCode;

    @Column(name = "benefit_name", length = 100)
    private String benefitName;

    @Column(name = "benefit_type", length = 50)
    private String benefitType;

    @Column(name = "usage_time")
    private Date usageTime;

    @Column(name = "usage_details", length = 500)
    private String usageDetails;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "device_info", length = 200)
    private String deviceInfo;

    @PrePersist
    protected void onCreate() {
        usageTime = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMemberRecordId() {
        return memberRecordId;
    }

    public void setMemberRecordId(Integer memberRecordId) {
        this.memberRecordId = memberRecordId;
    }

    public String getBenefitCode() {
        return benefitCode;
    }

    public void setBenefitCode(String benefitCode) {
        this.benefitCode = benefitCode;
    }

    public String getBenefitName() {
        return benefitName;
    }

    public void setBenefitName(String benefitName) {
        this.benefitName = benefitName;
    }

    public String getBenefitType() {
        return benefitType;
    }

    public void setBenefitType(String benefitType) {
        this.benefitType = benefitType;
    }

    public Date getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(Date usageTime) {
        this.usageTime = usageTime;
    }

    public String getUsageDetails() {
        return usageDetails;
    }

    public void setUsageDetails(String usageDetails) {
        this.usageDetails = usageDetails;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
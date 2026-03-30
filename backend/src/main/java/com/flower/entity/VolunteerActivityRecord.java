package com.flower.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "volunteer_activity_record")
public class VolunteerActivityRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "volunteer_id", nullable = false)
    private Integer volunteerId;

    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "activity_name", length = 200)
    private String activityName;

    @Column(name = "participation_time")
    private Date participationTime;

    @Column(name = "participation_status", length = 20)
    private String participationStatus;

    @Column(name = "service_hours")
    private Integer serviceHours;

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "create_time")
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        if (participationTime == null) participationTime = new Date();
        if (participationStatus == null) participationStatus = "participated";
        createTime = new Date();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getVolunteerId() { return volunteerId; }
    public void setVolunteerId(Integer volunteerId) { this.volunteerId = volunteerId; }

    public Integer getActivityId() { return activityId; }
    public void setActivityId(Integer activityId) { this.activityId = activityId; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public Date getParticipationTime() { return participationTime; }
    public void setParticipationTime(Date participationTime) { this.participationTime = participationTime; }

    public String getParticipationStatus() { return participationStatus; }
    public void setParticipationStatus(String participationStatus) { this.participationStatus = participationStatus; }

    public Integer getServiceHours() { return serviceHours; }
    public void setServiceHours(Integer serviceHours) { this.serviceHours = serviceHours; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}

package com.flower.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sign_in_record")
public class SignInRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "registration_id", nullable = false)
    private ActivityRegistration registration;

    @Column(name = "sign_in_time")
    private Date signInTime;

    @Column(name = "sign_out_time")
    private Date signOutTime;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "hours_earned")
    private Integer hoursEarned;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = "signed_in";
        if (hoursEarned == null) hoursEarned = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ActivityRegistration getRegistration() {
        return registration;
    }

    public void setRegistration(ActivityRegistration registration) {
        this.registration = registration;
    }

    public Date getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(Date signInTime) {
        this.signInTime = signInTime;
    }

    public Date getSignOutTime() {
        return signOutTime;
    }

    public void setSignOutTime(Date signOutTime) {
        this.signOutTime = signOutTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getHoursEarned() {
        return hoursEarned;
    }

    public void setHoursEarned(Integer hoursEarned) {
        this.hoursEarned = hoursEarned;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
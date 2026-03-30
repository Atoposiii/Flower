package com.flower.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "activity_registration")
public class ActivityRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private VolunteerActivity activity;

    @ManyToOne
    @JoinColumn(name = "volunteer_id", nullable = false)
    private Volunteer volunteer;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "registration_time")
    private Date registrationTime;

    @Column(name = "sign_in_time")
    private Date signInTime;

    @Column(name = "sign_in_status", length = 20)
    private String signInStatus;

    @Column(name = "confirm_time")
    private Date confirmTime;

    @Column(name = "actual_hours")
    private Integer actualHours;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "is_member_joined")
    private Boolean isMemberJoined;

    @PrePersist
    protected void onCreate() {
        registrationTime = new Date();
        if (status == null) status = "registered";
        if (signInStatus == null) signInStatus = "unsigned";
        if (isMemberJoined == null) isMemberJoined = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public VolunteerActivity getActivity() {
        return activity;
    }

    public void setActivity(VolunteerActivity activity) {
        this.activity = activity;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }

    public Date getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(Date signInTime) {
        this.signInTime = signInTime;
    }

    public String getSignInStatus() {
        return signInStatus;
    }

    public void setSignInStatus(String signInStatus) {
        this.signInStatus = signInStatus;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public Integer getActualHours() {
        return actualHours;
    }

    public void setActualHours(Integer actualHours) {
        this.actualHours = actualHours;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Boolean getIsMemberJoined() {
        return isMemberJoined;
    }

    public void setIsMemberJoined(Boolean isMemberJoined) {
        this.isMemberJoined = isMemberJoined;
    }
}
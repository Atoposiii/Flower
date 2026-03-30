package com.flower.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reply_like", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"reply_id", "user_id"})
})
public class ReplyLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "reply_id", nullable = false)
    private CommentReply reply;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "create_time")
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CommentReply getReply() {
        return reply;
    }

    public void setReply(CommentReply reply) {
        this.reply = reply;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
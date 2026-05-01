package com.estore.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "open_id", unique = true, nullable = false)
    private String openId;
    
    @Column(name = "session_key")
    private String sessionKey;
    
    @Column(name = "union_id")
    private String unionId;
    
    private String nickname;
    
    @Column(name = "avatar_url")
    private String avatarUrl;
    
    private Integer gender;
    
    private String phone;
    
    private Integer status;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (status == null) {
            status = 1;
        }
        if (gender == null) {
            gender = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}

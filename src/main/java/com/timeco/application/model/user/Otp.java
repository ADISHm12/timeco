package com.timeco.application.model.user;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="Otp")
public class Otp {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="otpId")
    private Integer otp_id;

    @Column(name="email",nullable=false,unique=true)
    private String email;

    @Column(name="otp",nullable=true)
    private Integer otp;

    @Column(name="expirationTime",nullable=true)
    private LocalDateTime expirationTime;


    public Integer getOtp_id() {
        return otp_id;
    }

    public void setOtp_id(Integer otp_id) {
        this.otp_id = otp_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Otp(String email, Integer otp, LocalDateTime expirationTime) {
        super();
        this.email = email;
        this.otp = otp;
        this.expirationTime = expirationTime;
    }

    public Otp() {
        super();
    }

}

package com.timeco.application.model.user;

public class LoginDto {
    private String email;

    private String password;

    private String phoneNumber;

    private Integer otp;

    public LoginDto(String email, String password, String phoneNumber, Integer otp) {
        super();
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public LoginDto() {
        super();
    }

    @Override
    public String toString() {
        return "LoginDto{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", otp=" + otp +
                '}';
    }
}

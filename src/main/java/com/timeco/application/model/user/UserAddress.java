package com.timeco.application.model.user;

import javax.persistence.*;

@Entity
@Table
public class UserAddress {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;


    @Column(name = "name",nullable=false)
    private String userName;

    @ManyToOne()
    @JoinColumn(name = "user_Id")
    private User user;

    @Column(name = "address",nullable=false)
    private String address;
    @Column(name = "city",nullable=false)
    private String city;
    @Column(name = "state",nullable=false)
    private String state;
    @Column(name = "country",nullable=false)
    private String country;

    @Column(name = "pinCode",nullable=false,unique = true)
    private Integer pinCode;

    @Column(name = "mobile",nullable=false)
    private String mobile;

    public UserAddress() {
        super();
    }

    public UserAddress(Long addressId, String userName, User user, String address, String city, String state, String country, Integer pinCode, String mobile) {
        super();
        this.addressId = addressId;
        this.userName = userName;
        this.user = user;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pinCode = pinCode;
        this.mobile = mobile;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getPinCode() {
        return pinCode;
    }

    public void setPinCode(Integer pinCode) {
        this.pinCode = pinCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}

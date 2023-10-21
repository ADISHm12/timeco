package com.timeco.application.Dto;

import javax.persistence.Column;

public class AddressDto {

    private String userName;
    private String address;
    private String city;

    private String state;
    private String country;
    private Integer pinCode;
    private String mobile;


    public AddressDto() {
        super();
    }

    public AddressDto(String userName, String address, String city, String state, String country, Integer pinCode, String mobile) {
        super();
        this.userName = userName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pinCode = pinCode;
        this.mobile = mobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

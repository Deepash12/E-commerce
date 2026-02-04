package com.example.E.commerce.E_commerce.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.type.descriptor.jdbc.TinyIntAsSmallIntJdbcType;

import java.sql.Timestamp;
@Getter
@Setter
@Data
@Entity
public class userAddresses
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long address_id;

    private Long user_id;
    private String fullName;
    private String phone;
    private String addressline1;
    private String addressLine2;
    private String City;
    private String code;
    private String state;
    private String postalCode;
    private TinyIntAsSmallIntJdbcType Isdefault;
    private Timestamp createdAt;
    private Timestamp updateAt;



//    public Long getAddress_id() {
//        return address_id;
//    }
//
//    public void setAddress_id(Long address_id) {
//        this.address_id = address_id;
//    }
//
//    public Long getUser_id() {
//        return user_id;
//    }
//
//    public void setUser_id(Long user_id) {
//        this.user_id = user_id;
//    }
//
//    public String getFullName() {
//        return fullName;
//    }
//
//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public String getAddressline1() {
//        return addressline1;
//    }
//
//    public void setAddressline1(String addressline1) {
//        this.addressline1 = addressline1;
//    }
//
//    public String getAddressLine2() {
//        return addressLine2;
//    }
//
//    public void setAddressLine2(String addressLine2) {
//        this.addressLine2 = addressLine2;
//    }
//
//    public String getCity() {
//        return City;
//    }
//
//    public void setCity(String city) {
//        City = city;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
//
//    public String getPostalCode() {
//        return postalCode;
//    }
//
//    public void setPostalCode(String postalCode) {
//        this.postalCode = postalCode;
//    }
//
//    public TinyIntAsSmallIntJdbcType getIsdefault() {
//        return Isdefault;
//    }
//
//    public void setIsdefault(TinyIntAsSmallIntJdbcType isdefault) {
//        Isdefault = isdefault;
//    }
//
//    public Timestamp getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(Timestamp createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public Timestamp getUpdateAt() {
//        return updateAt;
//    }
//
//    public void setUpdateAt(Timestamp updateAt) {
//        this.updateAt = updateAt;
//    }




}

package edu.ewubd.eboibazar;

import java.io.Serializable;

public class UserAddress implements Serializable {
    private String address;
    private String phone;
    private String receivingFrom;

    public UserAddress() {
    }

    public UserAddress(String address, String phone, String receivingFrom) {
        this.address = address;
        this.phone = phone;
        this.receivingFrom = receivingFrom;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getReceivingFrom() {
        return receivingFrom;
    }
}
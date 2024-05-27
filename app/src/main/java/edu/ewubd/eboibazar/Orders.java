package edu.ewubd.eboibazar;

import java.util.List;

public class Orders {
    private String curDate, curTime, status;
    private List<Cart> cartItems;
    private List<UserAddress> userAddress;
    private int totalAmount;

    public Orders(String curDate, String curTime, List<Cart> cartItems, List<UserAddress> userAddress, int totalAmount, String status) {
        this.curDate = curDate;
        this.curTime = curTime;
        this.cartItems = cartItems;
        this.userAddress = userAddress;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Orders() {
    }

    public String getCurDate() {
        return curDate;
    }

    public void setCurDate(String curDate) {
        this.curDate = curDate;
    }

    public String getCurTime() {
        return curTime;
    }

    public void setCurTime(String curTime) {
        this.curTime = curTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Cart> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<Cart> cartItems) {
        this.cartItems = cartItems;
    }

    public List<UserAddress> getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(List<UserAddress> userAddress) {
        this.userAddress = userAddress;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
}
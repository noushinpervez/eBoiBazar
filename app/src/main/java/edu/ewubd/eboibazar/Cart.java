package edu.ewubd.eboibazar;

import java.io.Serializable;

public class Cart implements Serializable {
    String bookName, author, image;
    int quantity, totalPrice;

    public Cart(String bookName, String author, String image, int quantity, int totalPrice) {
        this.bookName = bookName;
        this.author = author;
        this.image = image;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public Cart() {
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthor() {
        return author;
    }

    public String getImage() {
        return image;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }
}
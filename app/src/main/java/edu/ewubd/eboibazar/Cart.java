package edu.ewubd.eboibazar;

import java.util.ArrayList;

public class Cart {
    private final ArrayList<Book> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public void addItem(Book book) {
        items.add(book);
    }

    public ArrayList<Book> getItems() {
        return items;
    }
}
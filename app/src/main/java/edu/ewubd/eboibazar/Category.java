package edu.ewubd.eboibazar;

public class Category {
    private String category, key;

    public Category(String key, String category) {
        this.key = key;
        this.category = category;
    }

    public Category() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }
}
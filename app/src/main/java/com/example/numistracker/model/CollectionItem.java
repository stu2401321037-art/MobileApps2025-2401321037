package com.example.numistracker.model;

public class CollectionItem {
    public final long id;
    public final String name;
    public final String country;
    public final int year;
    public final int quantity;
    public final String condition;
    public final String photoPath;

    public CollectionItem(long id, String name, String country,
                          int year, int quantity, String condition, String photoPath) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.year = year;
        this.quantity = quantity;
        this.condition = condition;
        this.photoPath = photoPath;
    }
}
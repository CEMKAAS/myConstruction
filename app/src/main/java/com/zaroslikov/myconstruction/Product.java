package com.zaroslikov.myconstruction;

public class Product {


    private String name;
    private double count;
    private String suffix;
    private double price;

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Product(String name, double count, String suffix) {
        this.name = name;
        this.count = count;
        this.suffix = suffix;
        this.price = price;
    }

    public Product(int id, String name, String suffix) {
        this.id = id;
        this.name = name;
        this.suffix = suffix;

    }
}

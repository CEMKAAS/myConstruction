package com.zaroslikov.myconstruction;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Product implements Parcelable {


    private String name;
    private String category;
    private double count;
    private double price;
    private String suffix;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Product(String name, String category, double count, double price, String date) {
        this.name = name;
        this.category = category;
        this.count = count;
        this.price = price;
        this.date = date;
    }

    private String date;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }
}

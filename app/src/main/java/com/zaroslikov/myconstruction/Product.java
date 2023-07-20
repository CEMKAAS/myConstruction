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

    protected Product(Parcel in) {
        name = in.readString();
        category = in.readString();
        count = in.readDouble();
        price = in.readDouble();
        suffix = in.readString();
        date = in.readString();
        id = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

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

    public Product(int id, String name, String category, double count, double price, String date, String suffix) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.count = count;
        this.price = price;
        this.date = date;
        this.suffix = suffix;
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

    public Product(String name, String suffix,  double price, String date) {
       this.name = name;
       this.price = price;
       this.suffix = suffix;
       this.date = date;
    }

    public Product(String name, String suffix,  double price, String date, double count) {
        this.name = name;
        this.price = price;
        this.suffix = suffix;
        this.date = date;
        this.count = count;
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
        dest.writeString(name);
        dest.writeString(category);
        dest.writeDouble(count);
        dest.writeDouble(price);
        dest.writeString(suffix);
        dest.writeString(date);
        dest.writeInt(id);
    }
}

package com.example.carecall.entity;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class DoctorData implements Serializable {
    public String Address;
    public String Biography;
    public int Expriense;
    public int Id;
    public String Location;
    public String Mobile;
    public String Name;
    public String Patiens;
    public String Picture;
    public double Rating;
    public String Site;
    public String Special;
    public String Price;


    public Double getRating() {
        return this.Rating;
    }

    public Integer getExperience() {
        return this.Expriense;
    }

    public String getPrice() {
        return Price.isEmpty() ? "O" : Price;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof String) {
            return obj.equals(this.Name);
        }
        return super.equals(obj);
    }
}

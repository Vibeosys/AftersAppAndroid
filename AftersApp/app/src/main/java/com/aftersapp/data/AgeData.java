package com.aftersapp.data;

/**
 * Created by akshay on 28-09-2016.
 */
public class AgeData {

    private String name;
    private int value;

    public AgeData(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

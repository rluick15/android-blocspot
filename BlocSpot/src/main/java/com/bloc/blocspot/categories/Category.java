package com.bloc.blocspot.categories;

/**
 * Created by Rich on 12/11/2014.
 */
public class Category {

    private String mName;
    private String mColor;

    public Category(String name, String color){
        this.mName = name;
        this.mColor = color;
    }

    public String getName() {
        return mName;
    }

    public String getColor() {
        return mColor;
    }
}

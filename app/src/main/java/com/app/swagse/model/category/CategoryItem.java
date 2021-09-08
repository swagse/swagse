package com.app.swagse.model.category;

import com.google.gson.annotations.SerializedName;

public class CategoryItem {

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("icon")
    private String icon;

    public CategoryItem(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }



    @Override
    public String toString() {
        return categoryName;
    }
}
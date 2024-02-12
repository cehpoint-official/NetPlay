package com.dbug.netplay.model;

import java.util.List;

public class CategoryMain {
    public String status;
    public int count;
    public List<Category> categories;

    public class Category{
        public int cid;
        public String category_name;
        public String category_image;
        public int video_count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}



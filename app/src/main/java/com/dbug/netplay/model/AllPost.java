package com.dbug.netplay.model;

import java.util.List;

public class AllPost {

    public String status;
    public int count;
    public int count_total;
    public int pages;
    public List<Post> posts;

    public static class Post{
        public int vid;
        public int cat_id;
        public String video_title;
        public String video_url;
        public String video_id;
        public String video_thumbnail;
        public String video_duration;
        public String video_description;
        public String video_type;
        public String size;
        public int total_views;
        public String date_time;
        public String category_name;
        public String fromIntent;

        public Post(int vid, String video_title, String video_url, String video_id, String video_thumbnail, String video_duration, String video_description, String video_type, String size, int total_views, String date_time, String category_name,String fromIntent) {
            this.vid = vid;
            this.video_title = video_title;
            this.video_url = video_url;
            this.video_id = video_id;
            this.video_thumbnail = video_thumbnail;
            this.video_duration = video_duration;
            this.video_description = video_description;
            this.video_type = video_type;
            this.size = size;
            this.total_views = total_views;
            this.date_time = date_time;
            this.category_name = category_name;
            this.fromIntent = fromIntent;
        }

        public int getVid() {
            return vid;
        }

        public void setVid(int vid) {
            this.vid = vid;
        }

        public int getCat_id() {
            return cat_id;
        }

        public void setCat_id(int cat_id) {
            this.cat_id = cat_id;
        }

        public String getVideo_title() {
            return video_title;
        }

        public void setVideo_title(String video_title) {
            this.video_title = video_title;
        }

        public String getVideo_url() {
            return video_url;
        }

        public void setVideo_url(String video_url) {
            this.video_url = video_url;
        }

        public String getVideo_id() {
            return video_id;
        }

        public void setVideo_id(String video_id) {
            this.video_id = video_id;
        }

        public String getVideo_thumbnail() {
            return video_thumbnail;
        }

        public void setVideo_thumbnail(String video_thumbnail) {
            this.video_thumbnail = video_thumbnail;
        }

        public String getVideo_duration() {
            return video_duration;
        }

        public void setVideo_duration(String video_duration) {
            this.video_duration = video_duration;
        }

        public String getVideo_description() {
            return video_description;
        }

        public void setVideo_description(String video_description) {
            this.video_description = video_description;
        }

        public String getVideo_type() {
            return video_type;
        }

        public void setVideo_type(String video_type) {
            this.video_type = video_type;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public int getTotal_views() {
            return total_views;
        }

        public void setTotal_views(int total_views) {
            this.total_views = total_views;
        }

        public String getDate_time() {
            return date_time;
        }

        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }

        public String getCategory_name() {
            return category_name;
        }

        public void setCategory_name(String category_name) {
            this.category_name = category_name;
        }

        public String getFromIntent() {
            return fromIntent;
        }

        public void setFromIntent(String fromIntent) {
            this.fromIntent = fromIntent;
        }
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

    public int getCount_total() {
        return count_total;
    }

    public void setCount_total(int count_total) {
        this.count_total = count_total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }


}

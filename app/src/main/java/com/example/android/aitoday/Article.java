package com.example.android.aitoday;

public class Article {

    private String mTitle;
    private String mSection;
    private String mDate;
    private String mAuthor;
    private String mUrl;

    // Create a constructor for an Article object
    public Article(String title, String section, String date, String author, String url) {
        mTitle = title;
        mSection = section;
        mDate = date;
        mAuthor = author;
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public String getDate() {
        return mDate;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUrl() {
        return mUrl;
    }
}
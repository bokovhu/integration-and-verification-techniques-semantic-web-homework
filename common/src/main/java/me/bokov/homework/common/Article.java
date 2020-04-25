package me.bokov.homework.common;

public final class Article {

    private String id;
    private int localId;
    private String category;
    private int year;
    private int month;
    private String title;
    private String content;

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public int getLocalId () {
        return localId;
    }

    public void setLocalId (int localId) {
        this.localId = localId;
    }

    public String getCategory () {
        return category;
    }

    public void setCategory (String category) {
        this.category = category;
    }

    public int getYear () {
        return year;
    }

    public void setYear (int year) {
        this.year = year;
    }

    public int getMonth () {
        return month;
    }

    public void setMonth (int month) {
        this.month = month;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getContent () {
        return content;
    }

    public void setContent (String content) {
        this.content = content;
    }

}

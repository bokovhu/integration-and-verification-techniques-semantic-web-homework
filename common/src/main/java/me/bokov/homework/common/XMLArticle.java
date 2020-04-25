package me.bokov.homework.common;

public class XMLArticle {

    private String fullId;
    private int localId;
    private int year;
    private int month;
    private String category;
    private String title;
    private String content;

    public String getFullId () {
        return fullId;
    }

    public void setFullId (String fullId) {
        this.fullId = fullId;
    }

    public int getLocalId () {
        return localId;
    }

    public void setLocalId (int localId) {
        this.localId = localId;
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

    public String getCategory () {
        return category;
    }

    public void setCategory (String category) {
        this.category = category;
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

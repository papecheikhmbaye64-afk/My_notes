package com.example.mynotes;

public class Note {
    private int id;
    private String title;
    private String content;
    private String color;
    private long dateCreated;
    private long dateModified;
    private int isFavorite;

    public Note(String title, String content, String color, long dateCreated, long dateModified, int isFavorite) {
        this.title = title;
        this.content = content;
        this.color = color;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.isFavorite = isFavorite;
    }

    public Note(int id, String title, String content, String color, long dateCreated, long dateModified, int isFavorite) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.color = color;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.isFavorite = isFavorite;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public long getDateCreated() { return dateCreated; }
    public long getDateModified() { return dateModified; }
    public void setDateModified(long dateModified) { this.dateModified = dateModified; }
    public int isFavorite() { return isFavorite; }
    public void setFavorite(int favorite) { this.isFavorite = favorite; }
}
package com.udacity.journalapp.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by Elsi on 30/06/2018.
 */

@Entity(tableName = "diary")
public class DiaryEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String description;
    private String title;
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @Ignore
    public DiaryEntry(String description, String title, Date updatedAt) {
        this.description = description;
        this.title = title;
        this.updatedAt = updatedAt;
    }

    public DiaryEntry(int id, String description, String title, Date updatedAt) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

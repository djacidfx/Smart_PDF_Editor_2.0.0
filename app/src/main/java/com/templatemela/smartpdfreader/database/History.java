package com.templatemela.smartpdfreader.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class History {
    @ColumnInfo(name = "date")
    private String mDate;

    @ColumnInfo(name = "file_path")
    private String mFilePath;

    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "operation_type")
    private String mOperationType;

    public History(String mFilePath, String mDate, String mOperationType) {
        this.mFilePath = mFilePath;
        this.mDate = mDate;
        this.mOperationType = mOperationType;
    }

    public int getId() {
        return this.mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getFilePath() {
        return this.mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    public String getDate() {
        return this.mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getOperationType() {
        return this.mOperationType;
    }

    public void setOperationType(String operationType) {
        this.mOperationType = operationType;
    }
}

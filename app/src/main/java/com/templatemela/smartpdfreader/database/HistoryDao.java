package com.templatemela.smartpdfreader.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
    @Delete
    void deleteHistory(History history);

    @Query("SELECT * FROM history")
    List<History> getAllHistory();

    @Query("SELECT * FROM history WHERE operation_type IN (:opTypes)")
    List<History> getHistoryByOperationType(String[] opTypes);

    @Insert
    void insertAll(History... historyArr);
}

package com.resttab.testproject.dbHelper.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.resttab.testproject.dbHelper.model.Address;

import java.util.List;

@Dao
public interface AddressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Address address);

    @Delete
    void delete(Address address);

    @Query("SELECT * FROM address")
    List<Address> getAllAddresses();
}

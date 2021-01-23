package com.resttab.testproject.dbHelper.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.resttab.testproject.dbHelper.dao.AddressDao;
import com.resttab.testproject.dbHelper.model.Address;

@Database(entities = {Address.class}, version = 1, exportSchema = false)
public abstract class AddressDB extends RoomDatabase {

    public abstract AddressDao addressDao();

}

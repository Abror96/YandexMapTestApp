package com.resttab.testproject.dbHelper.repository;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.resttab.testproject.dbHelper.db.AddressDB;
import com.resttab.testproject.dbHelper.model.Address;
import com.resttab.testproject.mvp.contracts.MapContract;
import com.resttab.testproject.utils.App;

@SuppressLint("StaticFieldLeak")
public class MapRepo implements MapContract.Interactor {

    private AddressDB database;

    public MapRepo() {
        database = App.getDatabase();
    }

    @Override
    public void addAddress(OnFinishedListener onFinishedListener, Address address) {
        new AsyncTask<Void, Void, Void>() {

            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                database.addressDao().insert(address);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                onFinishedListener.addAddressFinished();
            }
        }.execute();
    }
}

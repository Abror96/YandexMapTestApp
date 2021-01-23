package com.resttab.testproject.dbHelper.repository;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.resttab.testproject.dbHelper.db.AddressDB;
import com.resttab.testproject.dbHelper.model.Address;
import com.resttab.testproject.mvp.contracts.MyAddressesContract;
import com.resttab.testproject.utils.App;

import java.util.List;

@SuppressLint("StaticFieldLeak")
public class MyAddressesRepo implements MyAddressesContract.Interactor {

    private AddressDB database;

    public MyAddressesRepo() {
        database = App.getDatabase();
    }

    @Override
    public void getAddresses(OnFinishedListener onFinishedListener) {
        new AsyncTask<Void, Void, List<Address>>() {

            @Override
            protected List<Address> doInBackground(Void... voids) {
                return database.addressDao().getAllAddresses();
            }

            @Override
            protected void onPostExecute(List<Address> list) {
                super.onPostExecute(list);
                onFinishedListener.getAddressesFinished(list);
            }
        }.execute();
    }

    @Override
    public void deleteAddress(OnFinishedListener onFinishedListener, Address address) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                database.addressDao().delete(address);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                onFinishedListener.deleteAddressFinished();
            }
        }.execute();
    }
}

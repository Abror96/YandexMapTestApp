package com.resttab.testproject.mvp.presenters;

import androidx.lifecycle.LiveData;

import com.resttab.testproject.dbHelper.model.Address;
import com.resttab.testproject.mvp.contracts.MyAddressesContract;

import java.util.List;

public class MyAddressesPresenterImpl implements MyAddressesContract.Presenter, MyAddressesContract.Interactor.OnFinishedListener {

    private MyAddressesContract.View view;
    private MyAddressesContract.Interactor interactor;

    public MyAddressesPresenterImpl(MyAddressesContract.View view, MyAddressesContract.Interactor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void destroy() {
        view = null;
    }

    @Override
    public void getAddressesCalled() {
        interactor.getAddresses(this);
    }

    @Override
    public void deleteAddressCalled(Address address) {
        interactor.deleteAddress(this, address);
    }

    @Override
    public void getAddressesFinished(List<Address> addresses) {
        if (view != null) {
            view.getAddressesSuccess(addresses);
        }
    }

    @Override
    public void deleteAddressFinished() {
        if (view != null) {
            view.deleteAddressSuccess();
        }
    }

    @Override
    public void failure(String message) {
        if (view != null) {
            view.showSnackbar(message);
        }
    }
}

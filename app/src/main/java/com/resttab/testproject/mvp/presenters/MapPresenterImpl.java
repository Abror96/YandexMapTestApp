package com.resttab.testproject.mvp.presenters;

import com.resttab.testproject.dbHelper.model.Address;
import com.resttab.testproject.mvp.contracts.MapContract;

public class MapPresenterImpl implements MapContract.Presenter, MapContract.Interactor.OnFinishedListener {

    private MapContract.View view;
    private MapContract.Interactor interactor;

    public MapPresenterImpl(MapContract.View view, MapContract.Interactor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void destroy() {
        view = null;
    }

    @Override
    public void addAddressCalled(Address address) {
        interactor.addAddress(this, address);
    }

    @Override
    public void addAddressFinished() {
        if (view != null)
            view.addAddressSuccess();
    }

    @Override
    public void failure(String message) {
        if (view != null) {
            view.showSnackbar(message);
        }
    }
}

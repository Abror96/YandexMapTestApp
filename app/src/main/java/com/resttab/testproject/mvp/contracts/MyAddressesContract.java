package com.resttab.testproject.mvp.contracts;

import androidx.lifecycle.LiveData;

import com.resttab.testproject.dbHelper.model.Address;

import java.util.List;

public interface MyAddressesContract {

    interface View {

        void getAddressesSuccess(List<Address> addresses);

        void deleteAddress(Address address);

        void deleteAddressSuccess();

        void showSnackbar(String message);
    }

    interface Presenter {

        void destroy();

        void getAddressesCalled();

        void deleteAddressCalled(Address address);

    }

    interface Interactor {

        interface OnFinishedListener {

            void getAddressesFinished(List<Address> addresses);

            void deleteAddressFinished();

            void failure(String message);

        }

        void getAddresses(OnFinishedListener onFinishedListener);

        void deleteAddress(OnFinishedListener onFinishedListener, Address address);

    }
}

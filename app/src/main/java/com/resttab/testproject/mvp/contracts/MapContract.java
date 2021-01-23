package com.resttab.testproject.mvp.contracts;

import com.resttab.testproject.dbHelper.model.Address;

public interface MapContract {

    interface View {

        void addAddressSuccess();

        void showSnackbar(String message);

    }

    interface Presenter {

        void destroy();

        void addAddressCalled(Address address);

    }

    interface Interactor {

        interface OnFinishedListener {

            void addAddressFinished();

            void failure(String message);

        }

        void addAddress(OnFinishedListener onFinishedListener, Address address);

    }

}

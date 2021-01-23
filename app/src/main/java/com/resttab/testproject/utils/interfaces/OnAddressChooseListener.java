package com.resttab.testproject.utils.interfaces;

import com.yandex.mapkit.GeoObjectCollection;

public interface OnAddressChooseListener {

    void onAddressChosen(GeoObjectCollection.Item place);

    void onDialogDismiss();

}

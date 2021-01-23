package com.resttab.testproject.ui.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.resttab.testproject.R;
import com.resttab.testproject.databinding.FragmentMapBinding;
import com.resttab.testproject.dbHelper.model.Address;
import com.resttab.testproject.dbHelper.repository.MapRepo;
import com.resttab.testproject.mvp.contracts.MapContract;
import com.resttab.testproject.mvp.presenters.MapPresenterImpl;
import com.resttab.testproject.utils.interfaces.OnAddressAddListener;
import com.resttab.testproject.utils.interfaces.OnAddressChooseListener;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.GeoObject;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.search.BusinessObjectMetadata;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.SearchType;
import com.yandex.mapkit.search.Session;
import com.yandex.runtime.Error;
import com.yandex.runtime.any.Collection;

public class MapFragment extends Fragment implements CameraListener, Session.SearchListener, OnAddressChooseListener, MapContract.View {

    private FragmentMapBinding binding;

    // coordinates of tashkent
    private double latitude = 41.311151;
    private double longitude = 69.279737;

    private SearchManager searchManager;
    private Session searchSession;

    private BottomSheetBehavior<View> bottomSheetBehavior;
    private BottomSearchFragment bottomSearchFragment;
    private MapContract.Presenter presenter;

    private String selectedTitle, selectedAddress;
    private static OnAddressAddListener onAddressAddListener;
    private static OnAddressAddListener controlBadgetsListener;
    public static void setOnAddressAddListener(OnAddressAddListener onAddressAddListener) {
        MapFragment.onAddressAddListener = onAddressAddListener;
    }
    public static void setControlBadgetsListener(OnAddressAddListener onAddressAddListener) {
        MapFragment.controlBadgetsListener =  onAddressAddListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MapKitFactory.setApiKey("522fb9ba-acc3-4c2a-ad64-371448cace44");
        MapKitFactory.initialize(getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

        presenter = new MapPresenterImpl(this, new MapRepo());

        Point point = new Point(latitude, longitude);
        binding.mapView.getMap().move(
                new CameraPosition(point, 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0), null);

        binding.mapView.getMap().addCameraListener(this);

        SearchFactory.initialize(getContext());
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        BottomSearchFragment.setOnAddressChooseListener(this);

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSearchResult.bottomSheetView);
        binding.bottomSearchResult.close.setOnClickListener(view -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
        binding.bottomSearchResult.addAddress.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            CardView linearLayout = (CardView) getLayoutInflater().inflate(R.layout.add_address_layout, null);

            EditText et_title = linearLayout.findViewById(R.id.et_title);
            TextView save_btn = linearLayout.findViewById(R.id.yes);
            TextView cancel_btn = linearLayout.findViewById(R.id.cancel);
            builder.setView(linearLayout);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            et_title.setText(selectedTitle);
            save_btn.setOnClickListener(view12 -> {
                String title = et_title.getText().toString().trim();
                if (!title.isEmpty()) {
                    presenter.addAddressCalled(new Address(title, selectedAddress));
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), R.string.title_field_must_be_filled, Toast.LENGTH_LONG).show();
                }
            });

            cancel_btn.setOnClickListener(view1 -> alertDialog.dismiss());
        });

        binding.searchCard.setOnClickListener(view -> {
            binding.searchCard.setVisibility(View.INVISIBLE);
            bottomSearchFragment = BottomSearchFragment.getInstance();
            bottomSearchFragment.show(getChildFragmentManager(), "search");
        });

        return binding.getRoot();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.destroy();
    }

    @Override
    public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateReason cameraUpdateReason, boolean b) {
        if (b && cameraUpdateReason == CameraUpdateReason.GESTURES) {
            latitude = cameraPosition.getTarget().getLatitude();
            longitude = cameraPosition.getTarget().getLongitude();

            searchSession = searchManager.submit(
                    new Point(latitude, longitude),
                    20,
                    new SearchOptions().setSearchTypes(SearchType.BIZ.value),
                    this);

        }
    }

    @Override
    public void onSearchResponse(@NonNull Response response) {
        GeoObject toponym = response.getMetadata().getToponym();
        if(toponym != null) {
            selectedTitle = toponym.getName();
            selectedAddress = toponym.getDescriptionText();

            binding.bottomSearchResult.title.setText(toponym.getName());
            binding.bottomSearchResult.category.setText(toponym.getDescriptionText());
            binding.bottomSearchResult.time.setVisibility(View.GONE);
        }
        if(response.getCollection().getChildren().size() > 0) {
            showOrgData(response.getCollection().getChildren().get(0));
        }

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    private void showOrgData(GeoObjectCollection.Item place) {
        String nameOfOrganization = place.getObj().getName();
        String address = place.getObj().getDescriptionText();

        String full_address = "", category = "", workingState = "";
        boolean isOpenNow;

        Collection meta = place.getObj().getMetadataContainer();
        if (meta.getItem(BusinessObjectMetadata.class) != null) {
            Log.d("LOGGERR", "showOrgData: ");
            full_address = meta.getItem(BusinessObjectMetadata.class).getAddress().getFormattedAddress();
            category = meta.getItem(BusinessObjectMetadata.class).getCategories().size() > 0  ? meta.getItem(BusinessObjectMetadata.class).getCategories().get(0).getName() : "";
            if (meta.getItem(BusinessObjectMetadata.class).getWorkingHours() != null && meta.getItem(BusinessObjectMetadata.class).getWorkingHours().getState() != null) {
                workingState = meta.getItem(BusinessObjectMetadata.class).getWorkingHours().getState().getShortText();
                isOpenNow = meta.getItem(BusinessObjectMetadata.class).getWorkingHours().getState().getIsOpenNow();
                binding.bottomSearchResult.time.setText(isOpenNow ? "Открыто " + workingState.toLowerCase() : workingState);
                binding.bottomSearchResult.time.setVisibility(View.VISIBLE);
            } else {
                binding.bottomSearchResult.time.setVisibility(View.GONE);
            }
        }

        selectedTitle = nameOfOrganization;
        selectedAddress = full_address.isEmpty() ? nameOfOrganization + " " + address : full_address;

        binding.bottomSearchResult.title.setVisibility(nameOfOrganization != null && !nameOfOrganization.isEmpty() ? View.VISIBLE : View.GONE);
        binding.bottomSearchResult.time.setVisibility(workingState != null && !workingState.isEmpty() ? View.VISIBLE : View.GONE);

        binding.bottomSearchResult.title.setText(nameOfOrganization);
        binding.bottomSearchResult.category.setText(category != null && !category.isEmpty() ? category : address);
    }

    @Override
    public void onSearchError(@NonNull Error error) {
        Log.d("LOGGERR", "onSearchError: " + error.isValid());
    }

    @Override
    public void onAddressChosen(GeoObjectCollection.Item place) {
        bottomSearchFragment.dismiss();
        showOrgData(place);
        binding.mapView.getMap().move(
                new CameraPosition(place.getObj().getGeometry().get(0).getPoint(),
                        16, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0), null);

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onDialogDismiss() {
        binding.searchCard.setVisibility(View.VISIBLE);
    }

    @Override
    public void addAddressSuccess() {
        if (onAddressAddListener != null) onAddressAddListener.onAddressAdded();
        if (controlBadgetsListener != null) controlBadgetsListener.onAddressAdded();
    }

    @Override
    public void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }
}

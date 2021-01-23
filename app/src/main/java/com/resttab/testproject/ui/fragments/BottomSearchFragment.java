package com.resttab.testproject.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.resttab.testproject.R;
import com.resttab.testproject.adapters.SearchResultAdapter;
import com.resttab.testproject.databinding.FragmentBottomSearchBinding;
import com.resttab.testproject.utils.interfaces.OnAddressChooseListener;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.runtime.Error;

public class BottomSearchFragment extends BottomSheetDialogFragment implements Session.SearchListener {

    private FragmentBottomSearchBinding binding;
    private static OnAddressChooseListener onAddressChooseListener;
    private SearchManager searchManager;
    private Session searchSession;
    private SearchResultAdapter adapter;

    public static BottomSearchFragment getInstance() {
        return new BottomSearchFragment();
    }

    public static void setOnAddressChooseListener(OnAddressChooseListener onSpecialistChooseListener) {
        BottomSearchFragment.onAddressChooseListener = onSpecialistChooseListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MapKitFactory.setApiKey("522fb9ba-acc3-4c2a-ad64-371448cace44");
        MapKitFactory.initialize(getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_search, container, false);
        binding.etAddress.requestFocus();

        SearchFactory.initialize(getContext());
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        binding.resultsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchSession = searchManager.submit(
                        editable.toString(),
                        Geometry.fromBoundingBox(new BoundingBox(new Point(37.1449940049, 55.9289172707), new Point(45.5868043076, 73.055417108))),
                        new SearchOptions(),
                        BottomSearchFragment.this);
            }
        });
        binding.clear.setOnClickListener(view -> binding.etAddress.setText(""));

        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog=(BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialogc = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet =  dialogc.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return bottomSheetDialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        onAddressChooseListener.onDialogDismiss();
    }

    @Override
    public void onSearchResponse(@NonNull Response response) {
        if (adapter == null)
            adapter = new SearchResultAdapter(response.getCollection().getChildren(), (place) -> onAddressChooseListener.onAddressChosen(place));
        else adapter.updateData(response.getCollection().getChildren());
        binding.resultsRecycler.setAdapter(adapter);
    }

    @Override
    public void onSearchError(@NonNull Error error) {
        Log.d("LOGGERR", "onSearchError: ");
    }
}

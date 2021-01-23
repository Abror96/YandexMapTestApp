package com.resttab.testproject.ui.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.resttab.testproject.R;
import com.resttab.testproject.adapters.MyAddressesAdapter;
import com.resttab.testproject.databinding.FragmentMyAddressesBinding;
import com.resttab.testproject.dbHelper.model.Address;
import com.resttab.testproject.dbHelper.repository.MyAddressesRepo;
import com.resttab.testproject.mvp.contracts.MyAddressesContract;
import com.resttab.testproject.mvp.presenters.MyAddressesPresenterImpl;
import com.resttab.testproject.utils.interfaces.OnAddressAddListener;

import java.util.List;

public class MyAddressesFragment extends Fragment implements MyAddressesContract.View, OnAddressAddListener {

    private FragmentMyAddressesBinding binding;
    private MyAddressesContract.Presenter presenter;
    private MyAddressesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_addresses, container, false);

        MapFragment.setOnAddressAddListener(this);
        presenter = new MyAddressesPresenterImpl(this, new MyAddressesRepo());
        presenter.getAddressesCalled();

        binding.myAddressesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        return binding.getRoot();
    }

    @Override
    public void getAddressesSuccess(List<Address> addresses) {
        if (addresses.size() > 0) {
            binding.noData.setVisibility(View.GONE);
            binding.myAddressesRecycler.setVisibility(View.VISIBLE);
        } else {
            binding.noData.setVisibility(View.VISIBLE);
            binding.myAddressesRecycler.setVisibility(View.GONE);
        }
        if (adapter == null) adapter = new MyAddressesAdapter(addresses, this);
        else adapter.updateList(addresses);
        binding.myAddressesRecycler.setAdapter(adapter);
    }

    @Override
    public void deleteAddress(Address address) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CardView linearLayout = (CardView) getLayoutInflater().inflate(R.layout.confirmation_layout, null);

        TextView et_title = linearLayout.findViewById(R.id.et_title);
        TextView yes_btn = linearLayout.findViewById(R.id.yes);
        TextView cancel_btn = linearLayout.findViewById(R.id.cancel);
        builder.setView(linearLayout);
        yes_btn.setText(R.string.delete);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        et_title.setText(R.string.do_u_rly_want_to_delete);
        yes_btn.setOnClickListener(view12 -> {
            presenter.deleteAddressCalled(address);
            alertDialog.dismiss();
        });

        cancel_btn.setOnClickListener(view1 -> alertDialog.dismiss());
    }

    @Override
    public void deleteAddressSuccess() {
        presenter.getAddressesCalled();
    }

    @Override
    public void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onAddressAdded() {
        presenter.getAddressesCalled();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.destroy();
    }
}

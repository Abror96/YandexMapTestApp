package com.resttab.testproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.resttab.testproject.R;
import com.resttab.testproject.dbHelper.model.Address;
import com.resttab.testproject.mvp.contracts.MyAddressesContract;

import java.util.List;

public class MyAddressesAdapter extends RecyclerView.Adapter<MyAddressesAdapter.ViewHolder> {

    private List<Address> addresses;
    private MyAddressesContract.View main_view;

    public MyAddressesAdapter(List<Address> addresses, MyAddressesContract.View main_view) {
        this.addresses = addresses;
        this.main_view = main_view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_address_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address item = addresses.get(position);
        holder.title.setText(item.getTitle());
        holder.address.setText(item.getAddress());

        holder.delete.setOnClickListener(view -> main_view.deleteAddress(item));
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public void updateList(List<Address> addresses) {
        this.addresses = addresses;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView address;
        private ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            address = itemView.findViewById(R.id.address);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}

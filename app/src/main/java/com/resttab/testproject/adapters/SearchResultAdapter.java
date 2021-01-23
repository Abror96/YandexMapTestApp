package com.resttab.testproject.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.resttab.testproject.R;
import com.yandex.mapkit.GeoObjectCollection;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<GeoObjectCollection.Item> places;
    private OnItemClickListener onItemClickListener;

    public SearchResultAdapter(List<GeoObjectCollection.Item> places, OnItemClickListener onItemClickListener) {
        this.places = places;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GeoObjectCollection.Item item = places.get(position);
        holder.title.setText(item.getObj().getName());
        holder.address.setText(item.getObj().getDescriptionText());

        holder.itemView.setOnClickListener(view -> onItemClickListener.onItemClicked(item));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public void updateData(List<GeoObjectCollection.Item> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            address = itemView.findViewById(R.id.address);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(GeoObjectCollection.Item place);
    }
}

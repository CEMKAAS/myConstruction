package com.zaroslikov.myconstruction;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductArhiveAdapter extends RecyclerView.Adapter<ProductArhiveAdapter.MyViewHolder> {

    private List<Product> productsList;

    public ProductArhiveAdapter(List productsList) {
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.my_row_product_arhive, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        String nameProuct = productsList.get(position).getName();
        double count = productsList.get(position).getCount();
        String suffix = productsList.get(position).getSuffix();
        double price = productsList.get(position).getPrice();



        holder.products.setText(nameProuct+ " ");
        holder.count.setText(String.valueOf(count));
        holder.unit.setText(suffix);
        holder.price.setText(price + " ₽");

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView products, unit, count, price;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            products = itemView.findViewById(R.id.products_text);
            count = itemView.findViewById(R.id.count_text);
            unit = itemView.findViewById(R.id.unit_text);
            price = itemView.findViewById(R.id.price_txt);
        }
    }


}

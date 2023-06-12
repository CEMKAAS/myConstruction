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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
//    private Map<String, Double> produtsMap;
    private List<String> productsList;
//    private List<Double> unitList;
//    private String name, suffix;
    private DecimalFormat f, eggFormat;

//    public ProductAdapter(Map<String, Double> productsMapList, String name, String suffix) {
//        this.produtsMap = productsMapList;
//        this.name = name;
//        this.suffix = suffix;
//        productsList = new ArrayList<>();
//        unitList = new ArrayList();
//        for (Map.Entry<String, Double> entry :
//                produtsMap.entrySet()) {
//            productsList.add(entry.getKey());
//            unitList.add(entry.getValue());
//        }
//    }
    public ProductAdapter(List productsList) {
       this.productsList = productsList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.my_row_product, parent, false);
        return new ProductAdapter.MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final ProductAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        String nameProuct = productsList.get(position);
//        holder.products.setText(name);
        holder.count.setText(nameProuct);

//        holder.unit.setText(String.valueOf(unitList.get(position)));

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView products, unit, count;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            products = itemView.findViewById(R.id.products_text);
            unit = itemView.findViewById(R.id.unit_text);
            count = itemView.findViewById(R.id.count_text);
        }
    }


}

package com.zaroslikov.myconstruction;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapterMagazine extends RecyclerView.Adapter<CustomAdapterMagazine.MyViewHolder> {

    private List<Product> products;

    private int myRow;
    private Listener listener;

    public static interface Listener {
        public void onClick(int position, Product product);
    }

    public CustomAdapterMagazine(List<Product> products, int myRow) {
        this.products = products;
        this.myRow = myRow;
    }

    public void setListener(CustomAdapterMagazine.Listener listener) {
        this.listener = listener;
    }

    public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(myRow, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.nameTxt.setText(String.valueOf(products.get(position).getName()));
        holder.categoryTxt.setText(String.valueOf(products.get(position).getCategory()));
        holder.countTxt.setText(String.valueOf(products.get(position).getCount()));
        holder.dateTxt.setText(String.valueOf(products.get(position).getDate()));
        if (R.layout.my_row_add==myRow) {
            holder.priceTxt.setText(String.valueOf(products.get(position).getPrice()));
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(position, products.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameTxt, categoryTxt,countTxt,priceTxt,dateTxt;
        LinearLayout mainLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name_txt);
            categoryTxt = itemView.findViewById(R.id.category_txt);
            countTxt = itemView.findViewById(R.id.count_txt);
            dateTxt = itemView.findViewById(R.id.date_txt);

            if (R.layout.my_row_add==myRow) {
                priceTxt = itemView.findViewById(R.id.price_txt);
            }

            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }


}

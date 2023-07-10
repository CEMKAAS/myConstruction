package com.zaroslikov.myconstruction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FinanceFragment extends Fragment {
    private RecyclerView recyclerView;


     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View layout = inflater.inflate(R.layout.fragment_finance, container, false);

         // Настраиваем адаптер
         recyclerView = layout.findViewById(R.id.recyclerView);

         ProductAdapter productAdapter = new ProductAdapter(productList);
         recyclerView.setAdapter(productAdapter);
         recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

         return layout;
    }
}
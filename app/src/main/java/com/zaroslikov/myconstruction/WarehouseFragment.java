package com.zaroslikov.myconstruction;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseFragment extends Fragment {

    public String nameProject;
    public String dateProject;
    public int idProject;
    private RecyclerView recyclerView;
    private List<String> productList;
    private MyDatabaseHelper myDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Открываем БД
        myDB = new MyDatabaseHelper(getActivity());

        View layout = inflater.inflate(R.layout.fragment_warehouse, container, false);

        //убириаем фаб кнопку
        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.setVisibility(View.GONE);

        MaterialToolbar appBar = getActivity().findViewById(R.id.topAppBar);
        appBar.setTitle("Мой Склад");
        appBar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        appBar.getMenu().findItem(R.id.deleteAll).setVisible(false);
        appBar.getMenu().findItem(R.id.filler).setVisible(false);
        appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            nameProject = bundle.getString("name");
            dateProject = bundle.getString("date");
            idProject = bundle.getInt("id");
        }

//Настройка листа
        productList = new ArrayList();
        myDB.insertToDbProduct();
        add();

        // Настраиваем адаптер
        recyclerView = layout.findViewById(R.id.recyclerView);

        ProductAdapter productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    public void add() {
        Cursor cursor = myDB.readProductJoin(idProject);
if (cursor != null) {
    while (cursor.moveToNext()) {
        String product = cursor.getString(1);
        productList.add(product);
    }
}
        cursor.close();
    }

//    public Map add1() {
//        Map<String, Double> tempList = new HashMap<>();
//
//        for (String product : productList) {
//
//            Cursor cursor = myDB.idProduct1(MyConstanta.TABLE_NAME, MyConstanta.TITLE, product);
//
//            if (cursor != null && cursor.getCount() != 0) {
//
//                while (cursor.moveToNext()) {
//                    Double productUnit = cursor.getDouble(2);
//                    if (tempList.get(product) == null) {
//                        tempList.put(product, productUnit);
//                    } else {
//                        double sum = tempList.get(product) + productUnit;
//                        tempList.put(product, sum);
//                    }
//                }
//                cursor.close();
//
//                Cursor cursorSale = myDB.idProduct1(MyConstanta.TABLE_NAMESALE, MyConstanta.TITLESale, product);
//
//                while (cursorSale.moveToNext()) {
//                    Double productUnit = cursorSale.getDouble(2);
//                    double minus = tempList.get(product) - productUnit;
//                    tempList.put(product, minus);
//                }
//                cursorSale.close();
//
//                Cursor cursorWriteOff = myDB.idProduct1(MyConstanta.TABLE_NAMEWRITEOFF, MyConstanta.TITLEWRITEOFF, product);
//
//                while (cursorWriteOff.moveToNext()) {
//                    Double productUnit = cursorWriteOff.getDouble(2);
//                    double minusWriteOff = tempList.get(product) - productUnit;
//                    tempList.put(product, minusWriteOff);
//                }
//                cursorWriteOff.close();
//            } else {
//                tempList.put(product, 0.0);
//            }
//        }
//
//        return tempList;
//    }


}
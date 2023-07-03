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
import com.zaroslikov.myconstruction.db.MyConstanta;
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
    private List<String> productAllList;
    private List<Product> productList;

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

        MainActivity mainActivity = new MainActivity();
       idProject = mainActivity.getProjectNumer();

        //Настройка листа
        productAllList = new ArrayList();
        productList = new ArrayList();
        add();

        // Настраиваем адаптер
        recyclerView = layout.findViewById(R.id.recyclerView);

        ProductAdapter productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    //Добавляем продукцию в список



    //Формируем список из БД
    public void add() {

        Cursor cursor = myDB.readProduct();

        while (cursor.moveToNext()) {
            String product = cursor.getString(1);
            productAllList.add(product);
        }
        cursor.close();

        for (String product : productAllList) {

            String productName = null;
            double productUnitAdd = 0;
            double productUnitWriteOff = 0;
            String suffix = null;

            Cursor cursorAdd = myDB.selectProductJoin(idProject, product, MyConstanta.TABLE_NAME_ADD);

            if (cursorAdd != null && cursorAdd.getCount() != 0) {
                cursorAdd.moveToFirst();
                productName = cursorAdd.getString(0);

                productUnitAdd = cursorAdd.getDouble(1);

                suffix = cursorAdd.getString(2);

            }
            cursor.close();

            Cursor cursorWriteOff = myDB.selectProductJoin(idProject, product, MyConstanta.TABLE_NAME_WRITEOFF);

            if (cursorWriteOff != null && cursorWriteOff.getCount() != 0) {
                cursorWriteOff.moveToFirst();
                productUnitWriteOff = cursorWriteOff.getDouble(1);

            }

            cursorWriteOff.close();

            double nowUnitProduct = productUnitAdd - productUnitWriteOff;

            productList.add(new Product(productName, nowUnitProduct, suffix));

        }
    }


}
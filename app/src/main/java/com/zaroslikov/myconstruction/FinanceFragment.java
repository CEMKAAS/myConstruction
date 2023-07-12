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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FinanceFragment extends Fragment {
    private RecyclerView recyclerViewCategory, recyclerViewProduct;
    private int idProject;
    private MyDatabaseHelper myDB;
    private List<Product> productSumList, categorySumList;
    ;

    private List<String> productNameList, categoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myDB = new MyDatabaseHelper(getActivity());

        View layout = inflater.inflate(R.layout.fragment_finance, container, false);
        //убириаем фаб кнопку
        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.setVisibility(View.GONE);

        MaterialToolbar appBar = getActivity().findViewById(R.id.topAppBar);
        appBar.setTitle("Мой Склад");
        appBar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        appBar.getMenu().findItem(R.id.deleteAll).setVisible(false);
        appBar.getMenu().findItem(R.id.filler).setVisible(true);
        appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        MainActivity mainActivity = new MainActivity();
        idProject = mainActivity.getProjectNumer();

        //Настройка листа
        categorySumList = new ArrayList();
        productSumList = new ArrayList();

        productNameList = new ArrayList();
        categoryList = new ArrayList<>();

        add();

        // Настраиваем адаптер
        recyclerViewCategory = layout.findViewById(R.id.recyclerView);
        recyclerViewProduct = layout.findViewById(R.id.recyclerViewAll);

        ProductAdapter productAdapterCategory = new ProductAdapter(categorySumList);
        recyclerViewCategory.setAdapter(productAdapterCategory);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getActivity()));

        ProductAdapter productAdapterProduct = new ProductAdapter(productSumList);
        recyclerViewProduct.setAdapter(productAdapterProduct);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }


    //Формируем список из БД
    public void add() {

        Cursor cursorProductAdd = myDB.selectProjectAllProductAndCategoryAdd(idProject);

        //Добавляем в списки продукты и категории
        Set<String> productHashSet = new HashSet<>();
        Set<String> categoryHashSet = new HashSet<>();

        while (cursorProductAdd.moveToNext()) {
            productHashSet.add(cursorProductAdd.getString(1));
            categoryHashSet.add(cursorProductAdd.getString(3));

            for (String name : productHashSet) {
                productNameList.add(name);
            }

            for (String category : categoryHashSet) {
                categoryList.add(category);
            }
        }
        cursorProductAdd.close();

        Cursor cursorAllSum = myDB.selectProjectAllSum(idProject);
        cursorAllSum.moveToFirst();
        double allSum = cursorAllSum.getDouble(1);
        //TODO Назначем сумму
        cursorAllSum.close();

        for (String category : categoryList) {

            Cursor cursorCategory = myDB.selectProjectAllSumCategory(idProject, category);
            while (cursorCategory.moveToNext()) {
                categorySumList.add(new Product(cursorCategory.getString(0), "₽", cursorCategory.getDouble(1)));
            }
            cursorCategory.close();

        }

        for (String product : productNameList){
            Cursor cursorProduct = myDB.selectProjectAllSumProduct(idProject,product);

            while (cursorProduct.moveToNext()){
                productSumList.add(new Product(cursorProduct.getString(0), "₽", cursorProduct.getDouble(1)));
            }
            cursorProduct.close();
        }

    }
}
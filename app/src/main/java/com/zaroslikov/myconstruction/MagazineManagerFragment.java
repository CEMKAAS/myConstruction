package com.zaroslikov.myconstruction;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MagazineManagerFragment extends Fragment {
    private MyDatabaseHelper myDB;
    private String appBarManager;
    private Cursor cursorManager;
    private int idProject, myRow, visibility;
    private CustomAdapterMagazine customAdapterMagazine;
    private RecyclerView recyclerView;
    private ImageView empty_imageview;

    private Boolean magazineAddBool;
    private List<Product> products;
    private TextView no_data, sixColumn, dicsPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_magazine_manager, container, false);
        //Подключение к базе данных
        myDB = new MyDatabaseHelper(getActivity());
        MainActivity mainActivity = new MainActivity();
        idProject = mainActivity.getProjectNumer();

        products = new ArrayList<>();

        //убириаем фаб кнопку
        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.setVisibility(View.GONE);
        //Настройка АппБара
        MaterialToolbar appBar = getActivity().findViewById(R.id.topAppBar);
        appBar.getMenu().findItem(R.id.filler).setVisible(true);
        appBar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        appBar.setOnMenuItemClickListener(item -> {
            int position = item.getItemId();
            if (position == R.id.filler) {
                //bottomSheetDialog.show();
            } else if (position == R.id.moreAll) {
                replaceFragment(new InFragment());
                appBar.setTitle("Информация");
            }
            return true;
        });
        appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
                ;
            }
        });

        appBarManager = appBar.getTitle().toString();

        if (appBarManager.equals("Мои Покупки")) {
            cursorManager = myDB.readAddMagazine(idProject);
            visibility = View.VISIBLE;
            magazineAddBool = true;
            myRow = R.layout.my_row_add;
        } else if (appBarManager.equals("Мои Списания")) {
            cursorManager = myDB.readWriteOffMagazine(idProject);
            visibility = View.GONE;
            magazineAddBool = false;
            myRow = R.layout.my_row_write_off;
        }

        //Создание отображения списка
        sixColumn = layout.findViewById(R.id.six_column);
        recyclerView = layout.findViewById(R.id.recyclerView);
        empty_imageview = layout.findViewById(R.id.empty_imageview);
        no_data = layout.findViewById(R.id.no_data);

        sixColumn.setVisibility(visibility);


        //Добавдение товаров в лист
        storeDataInArraysClass(cursorManager);

        //Создание адаптера
        customAdapterMagazine = new CustomAdapterMagazine(products, myRow);

        recyclerView.setAdapter(customAdapterMagazine);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Запускаем при нажатии
        customAdapterMagazine.setListener(new CustomAdapterMagazine.Listener() {
            @Override
            public void onClick(int position, Product product) {
                addChart(product);
            }
        });

        return layout;
    }

    void storeDataInArraysClass(Cursor cursor) {
        if (cursor.getCount() == 0) {
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        } else if (magazineAddBool) {
            storeDataInArraysClassLogicAdd(cursor);
        } else {
            storeDataInArraysClassLogicWriteOff(cursor);
        }

//        productNow.addAll(product);
    }

    //TODO Как бы сократить это говно?)
    public void storeDataInArraysClassLogicAdd(Cursor cursor) {
        cursor.moveToLast();
        products.add(new Product(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getDouble(3), cursor.getDouble(4), cursor.getString(5), cursor.getString(6)));
        while (cursor.moveToPrevious()) {
            products.add(new Product(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getDouble(3), cursor.getDouble(4), cursor.getString(5), cursor.getString(6)));
        }
        cursor.close();
        empty_imageview.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);
    }


    public void storeDataInArraysClassLogicWriteOff(Cursor cursor) {
        cursor.moveToLast();
        products.add(new Product(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getDouble(3), 0, cursor.getString(4), cursor.getString(5)));
        while (cursor.moveToPrevious()) {
            products.add(new Product(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getDouble(3), 0, cursor.getString(4), cursor.getString(5)));
        }
        cursor.close();
        empty_imageview.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);
    }



    public void addChart(Product product) {
        UpdateProductFragment updateProductFragment = new UpdateProductFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("product", product);
        bundle.putString("id", appBarManager);
        updateProductFragment.setArguments(bundle);

        replaceFragment(updateProductFragment);
    }


    private void replaceFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }
}
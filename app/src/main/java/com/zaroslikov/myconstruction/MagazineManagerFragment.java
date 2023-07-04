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

public class MagazineManagerFragment extends Fragment {
    private MyDatabaseHelper myDB;
    private String appBarManager;
    private Cursor cursorManager;
    private int idProject, myRow, visibility;
    private CustomAdapterMagazine customAdapterMagazine;
    private RecyclerView recyclerView;
    private ImageView empty_imageview;
    private TextView no_data, sixColumn, dicsPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_magazine_manager, container, false);
        //Подключение к базе данных
        myDB = new MyDatabaseHelper(getActivity());
        MainActivity mainActivity = new MainActivity();
        idProject = mainActivity.getProjectNumer();

        //убириаем фаб кнопку
        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.setVisibility(View.GONE);
//Настройка АппБара
        MaterialToolbar appBar = getActivity().findViewById(R.id.topAppBar);
        appBar.getMenu().findItem(R.id.filler).setVisible(true);
        appBar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        appBar.setOnMenuItemClickListener(item -> {
            int position = item.getItemId();
            if(position == R.id.filler){
//                bottomSheetDialog.show();
            } else if (position==R.id.moreAll){
                replaceFragment(new InFragment());
                appBar.setTitle("Информация");
            }
            return true;
        });
        appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();;
            }
        });

        appBarManager = appBar.getTitle().toString();

        if (appBarManager.equals("Мои Покупки")) {
            cursorManager = myDB.readAddMagazine(idProject);
            visibility = View.VISIBLE;
            myRow =  R.layout.my_row_add;
        } else if (appBarManager.equals("Мои Списания")) {
            cursorManager = myDB.readWriteOffMagazine(idProject);
            visibility = View.GONE;
            myRow =  R.layout.my_row_write_off;
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
        customAdapterMagazine = new CustomAdapterMagazine(productNow, myRow);

        recyclerView.setAdapter(customAdapterMagazine);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Запускаем при нажатии
        customAdapterMagazine.setListener(new CustomAdapterMagazine.Listener() {
            @Override
            public void onClick(int position, ProductDB productDB) {
                addChart(productDB);
            }
        });

        return layout;
    }

    void storeDataInArraysClass(Cursor cursor) {
        if (cursor.getCount() == 0) {
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        } else if (statusPrice.equals("Нет")) {
            storeDataInArraysClassLogic(cursor, 0);
        } else {
            storeDataInArraysClassLogic(cursor, 6);
        }
        productNow.addAll(product);
    }

    public void storeDataInArraysClassLogic(Cursor cursor, int id){
        cursor.moveToLast();
        product.add(new ProductDB(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2),
                cursor.getString(3) + "." + cursor.getString(4) + "." + cursor.getString(5), cursor.getInt(id)));
        while (cursor.moveToPrevious()) {
            product.add(new ProductDB(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2),
                    cursor.getString(3) + "." + cursor.getString(4) + "." + cursor.getString(5), cursor.getInt(id)));
        }
        cursor.close();
        empty_imageview.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);
    }

    private void replaceFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }
}
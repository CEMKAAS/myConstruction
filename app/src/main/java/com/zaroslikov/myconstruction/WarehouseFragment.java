package com.zaroslikov.myconstruction;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.zaroslikov.myconstruction.db.MyConstanta;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;
import com.zaroslikov.myconstruction.project.MenuProjectFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class WarehouseFragment extends Fragment {

    public String nameProject;
    public String dateProject;
    public int idProject;
    private RecyclerView recyclerView;
    private List<Product> productList, productAllList;
    private View layout;
    private TextView til;

    private MyDatabaseHelper myDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Открываем БД
        myDB = new MyDatabaseHelper(getActivity());

        layout = inflater.inflate(R.layout.fragment_warehouse, container, false);

        //убириаем фаб кнопку
        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.setVisibility(View.GONE);

        MaterialToolbar appBar = getActivity().findViewById(R.id.topAppBar);
        appBar.setTitle("Мой Склад");
        appBar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        appBar.getMenu().findItem(R.id.deleteAll).setVisible(false);
        appBar.getMenu().findItem(R.id.magazine).setVisible(false);
        appBar.getMenu().findItem(R.id.filler).setVisible(false);
        appBar.getMenu().findItem(R.id.moreAll).setVisible(true);
        appBar.setOnMenuItemClickListener(item -> {
            int position = item.getItemId();
            if (position == R.id.moreAll) {
                replaceFragment(new InFragment());
                appBar.setTitle("Информация");
            }
            return true;
        });
        appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new MenuProjectFragment());
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
        onBackPressed();
        // Настраиваем адаптер
//        recyclerView = layout.findViewById(R.id.recyclerView);
//
//        ProductAdapter productAdapter = new ProductAdapter(productList);
//        recyclerView.setAdapter(productAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Button add = layout.findViewById(R.id.end_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                builder.setTitle("Завершить проект?");
                builder.setMessage("Ваш проект попадет в архив со всеми данными, в случаи необходимости его можно будет востановить");

                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        String date = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
                        myDB.updateToDbProject(idProject, 1, date);
                        replaceFragment(new MenuProjectFragment());
                    }
                });
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();

            }
        });
        return layout;
    }

    //Добавляем продукцию в список


    //Формируем список из БД
    public void add() {

        Cursor cursor = myDB.selectProjectAllProductAndCategoryAdd(idProject);

        while (cursor.moveToNext()) {
            productAllList.add(new Product(0, cursor.getString(0), cursor.getString(1)));
        }
        cursor.close();

        for (Product product : productAllList) {

            String productName = null;
            double productUnitAdd = 0;
            double productUnitWriteOff = 0;
            String suffix = null;

            Cursor cursorAdd = myDB.selectProductJoin(idProject, product.getName(), MyConstanta.TABLE_NAME_ADD, product.getSuffix());

            if (cursorAdd != null && cursorAdd.getCount() != 0) {
                cursorAdd.moveToFirst();
                productName = cursorAdd.getString(0);

                productUnitAdd = cursorAdd.getDouble(1);

                suffix = cursorAdd.getString(2);

            }
            cursor.close();

            Cursor cursorWriteOff = myDB.selectProductJoin(idProject, product.getName(), MyConstanta.TABLE_NAME_WRITEOFF, product.getSuffix());

            if (cursorWriteOff != null && cursorWriteOff.getCount() != 0) {
                cursorWriteOff.moveToFirst();
                productUnitWriteOff = cursorWriteOff.getDouble(1);

            }

            cursorWriteOff.close();

            double nowUnitProduct = productUnitAdd - productUnitWriteOff;

            productList.add(new Product(productName, nowUnitProduct, suffix));

        }
    }


    // Настраиваем программно EditText
    public void onBackPressed() {
        TableLayout tableLayout = (TableLayout) layout.findViewById(R.id.tableLayout);
        int rowI = 0;
        for (Product product : productList) {
//            LinearLayout container = (LinearLayout) layout.findViewById(R.id.mlayout);
            TableRow tableRow = new TableRow(getActivity());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            for (int i = 0; i < 2; i++) {
                til = new TextView(getActivity());
                switch (i) {
                    case 0:
                        til.setText(product.getName());
                        tableRow.addView(til, i);
                        break;
                    case 1:
                        til.setText(String.valueOf(product.getCount()));
                        tableRow.addView(til, i);
                        break;
                    case 2:
                        til.setText(product.getSuffix());
                        tableRow.addView(til, i);
                        break;
                }
            }
            tableLayout.addView(tableRow, rowI);
            rowI++;
        }
    }


    private void replaceFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }

}
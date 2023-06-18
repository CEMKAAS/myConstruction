package com.zaroslikov.myconstruction;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AddFragment extends Fragment {
    private MyDatabaseHelper myDB;
    private TextInputLayout add_edit, date;
    private AutoCompleteTextView productName, suffixSpiner, category;
    private ArrayAdapter<String> arrayAdapterProduct;
    private List<String> productList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_add, container, false);

        myDB = new MyDatabaseHelper(getActivity());
        productList = new ArrayList<>();

        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.show();
        fab.setText("Журнал");
        fab.setIconResource(R.drawable.baseline_book_24);
        fab.getIcon();

        productName = layout.findViewById(R.id.productName_editText);
        add_edit = layout.findViewById(R.id.add_edit);
        suffixSpiner = layout.findViewById(R.id.suffixSpiner);
        category = layout.findViewById(R.id.category_edit);
        date = layout.findViewById(R.id.date);
        
        return layout;
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            //Товар
            arrayAdapterProduct = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, productList);
            productName.setAdapter(arrayAdapterProduct);

            //Категории
            arrayAdapterProduct = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, productList);
            productName.setAdapter(arrayAdapterProduct);


        }
    }

    //Добавляем продукцию в список
    public void add() {
        Cursor cursor = myDB.readProduct();

        while (cursor.moveToNext()) {
            String product = cursor.getString(1);
            productList.add(product);
        }
        cursor.close();
    }


    //Формируем список из БД
    public void add1() {
        tempList = new HashMap<>();

        for (String product : productList) {

            Cursor cursor = myDB.idProduct1(MyConstanta.TABLE_NAME, MyConstanta.TITLE, product);

            if (cursor != null && cursor.getCount() != 0) {

                while (cursor.moveToNext()) {
                    Double productUnit = cursor.getDouble(2);
                    if (tempList.get(product) == null) {
                        tempList.put(product, productUnit);
                    } else {
                        double sum = tempList.get(product) + productUnit;
                        tempList.put(product, sum);
                    }
                }
                cursor.close();

                Cursor cursorSale = myDB.idProduct1(MyConstanta.TABLE_NAMESALE, MyConstanta.TITLESale, product);

                while (cursorSale.moveToNext()) {
                    Double productUnit = cursorSale.getDouble(2);
                    double minus = tempList.get(product) - productUnit;
                    tempList.put(product, minus);
                }
                cursorSale.close();

                Cursor cursorWriteOff = myDB.idProduct1(MyConstanta.TABLE_NAMEWRITEOFF, MyConstanta.TITLEWRITEOFF, product);

                while (cursorWriteOff.moveToNext()) {
                    Double productUnit = cursorWriteOff.getDouble(2);
                    double minusWriteOff = tempList.get(product) - productUnit;
                    tempList.put(product, minusWriteOff);
                }
                cursorWriteOff.close();
            } else {
                tempList.put(product, 0.0);
            }
        }
    }

}
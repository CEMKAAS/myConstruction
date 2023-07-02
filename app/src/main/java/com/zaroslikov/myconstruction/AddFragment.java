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
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.zaroslikov.myconstruction.db.MyConstanta;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;


public class AddFragment extends Fragment {
    private MyDatabaseHelper myDB;
    private TextInputLayout add_edit, date, price_edit;
    private AutoCompleteTextView productName, suffixSpiner, category;
    private ArrayAdapter<String> arrayAdapterProduct, arrayAdapterCategory;
    private List<String> productNameList, categoryList;

    private List<Product> productList;

    private TextView nowUnit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_add, container, false);

        myDB = new MyDatabaseHelper(getActivity());
        productList = new ArrayList<>();
        categoryList = new ArrayList<>();
        productNameList = new ArrayList<>();

        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.show();
        fab.setText("Журнал");
        fab.setIconResource(R.drawable.baseline_book_24);
        fab.getIcon();

        productName = layout.findViewById(R.id.productName_editText);
        add_edit = layout.findViewById(R.id.add_edit);
        price_edit = layout.findViewById(R.id.price_edit);
        suffixSpiner = layout.findViewById(R.id.suffixSpiner);
        category = layout.findViewById(R.id.category_edit);
        date = layout.findViewById(R.id.date);
        nowUnit = layout.findViewById(R.id.now_warehouse);

        addProduct();

        Button add = layout.findViewById(R.id.add_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInDB();
            }
        });

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            //Товар
            arrayAdapterProduct = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, productNameList);
            productName.setAdapter(arrayAdapterProduct);

            //Категории
            arrayAdapterCategory = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categoryList);
            category.setAdapter(arrayAdapterProduct);


        }
    }

    //Добавляем продукцию в список
    public void addProduct() {
        Cursor cursor = myDB.readProduct();

        while (cursor.moveToNext()) {
            productList.add(new Product(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
            ));
        }

        for (Product product: productList) {
           productNameList.add(product.getName());
        }

        cursor.close();
    }


    //Формируем список из БД
    public void addDB(String product) {

            Cursor cursor = myDB.selectProductJoin(1, product, MyConstanta.TABLE_NAME_ADD);
            String productName = null;
            double productUnitAdd = 0;
            double productUnitWriteOff = 0;
            String suffix = null;
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                productName = cursor.getString(0);

                productUnitAdd = cursor.getDouble(1);

                suffix = cursor.getString(2);

            }
            cursor.close();

            Cursor cursorWriteOff = myDB.selectProductJoin(1, product, MyConstanta.TABLE_NAME_WRITEOFF);

            if (cursorWriteOff != null && cursorWriteOff.getCount() != 0) {
                cursorWriteOff.moveToFirst();
                productUnitWriteOff = cursorWriteOff.getDouble(1);

            }
            cursorWriteOff.close();

            double nowUnitProduct = productUnitAdd - productUnitWriteOff;

            nowUnit.setText(productName + " " + String.valueOf(nowUnitProduct) + " " + suffix);

    }

    public void addInDB() {
        //Достаем из андройда имяПродукта и суффикс
        String name = productName.getText().toString();
        String suffix = suffixSpiner.getText().toString();

        // проверяем продукт в БД
        Cursor cursorProduct = myDB.seachProduct(name);
        if (cursorProduct.getCount()==0) {
            myDB.insertToDbProduct(name, suffix);
        }
        cursorProduct.close();

        //присваиваем ид продукта
        Cursor cursorProduct1 = myDB.seachProduct(name);
        cursorProduct1.moveToFirst();
        int idProduct = cursorProduct1.getInt(0);
        cursorProduct1.close();

        //проверяем связку продукт архив
        Cursor cursorPP = myDB.seachPP(1, idProduct);

        if(cursorPP.getCount()==0){
            myDB.insertToDbProjectProduct(1, idProduct);
        }
        cursorPP.close();

        Cursor cursorPP1 = myDB.seachPP(1, idProduct);

        //присваиваем ид продукта и проекта
        cursorPP1.moveToFirst();
        int idPP = cursorPP1.getInt(0);
        cursorPP1.close();

        //присвыаеиваем ид продукта и проекта в адд таблицу
        double price = Double.parseDouble(price_edit.getEditText().getText().toString());
        double count = Double.parseDouble(add_edit.getEditText().getText().toString());

        String categoryProduct = category.getText().toString();
        String dateProduct = date.getEditText().toString().toString();

        myDB.insertToDbProductAdd(count,categoryProduct, price,dateProduct, idPP);

        addDB(name);
    }

}
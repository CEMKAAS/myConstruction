package com.zaroslikov.myconstruction;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.zaroslikov.myconstruction.db.MyConstanta;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;


public class AddFragment extends Fragment {
    private MyDatabaseHelper myDB;
    private TextInputLayout add_edit, date, price_edit;
    private AutoCompleteTextView productName, suffixSpiner, category;
    private ArrayAdapter<String> arrayAdapterProduct, arrayAdapterCategory;
    private List<String> productNameList, categoryList;
    private MaterialDatePicker datePicker;
    private List<Product> productList;

    private int idProject;
    private TextView nowUnit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_add, container, false);

        myDB = new MyDatabaseHelper(getActivity());
        productList = new ArrayList<>();
        categoryList = new ArrayList<>();
        productNameList = new ArrayList<>();

        MainActivity mainActivity = new MainActivity();
        idProject = mainActivity.getProjectNumer();

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

        // Настройка календаря
        CalendarConstraints constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build();

        datePicker = MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraintsBuilder)
                .setTitleText("Выберите дату").setSelection(MaterialDatePicker.todayInUtcMilliseconds()) //Todo выбирать дату из EditText
                .build();

        date.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getActivity().getSupportFragmentManager(), "wer");
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        calendar.setTimeInMillis((Long) selection);
                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                        String formattedDate = format.format(calendar.getTime());
                        date.getEditText().setText(formattedDate);

                    }
                });
            }
        });

        productName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String productClick = productNameList.get(position);
                addDB(productClick);
            }
        });

        Button add = layout.findViewById(R.id.add_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInDB();
                setArrayAdapter();
            }
        });

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            setArrayAdapter();
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

        Set<String> tempList = new HashSet<>();
        //TODO сделать в хелпере
        Cursor cursor1 = myDB.seachCategory();

        while (cursor1.moveToNext()){
            tempList.add(cursor1.getString(0));
        }
        cursor1.close();

        for (String nameExpenses : tempList) {
            categoryList.add(nameExpenses);
        }

    }

    public void addInDB() {
        //Достаем из андройда имяПродукта и суффикс
        String name = productName.getText().toString();
        String suffix = suffixSpiner.getText().toString();

        int idProduct = 0;
        int idPP = 0;
        // проверяем продукт в БД
        Cursor cursorProduct = myDB.seachProduct(name);
        if (cursorProduct.getCount()==0) {
            idProduct = Math.toIntExact(myDB.insertToDbProduct(name, suffix));
            productNameList.add(name);
        }else {
            cursorProduct.moveToFirst();
            idProduct = cursorProduct.getInt(0);
        }
        cursorProduct.close();

        //проверяем связку продукт архив
        Cursor cursorPP = myDB.seachPP(idProject, idProduct);

        if(cursorPP.getCount()==0){
            idPP = Math.toIntExact(myDB.insertToDbProjectProduct(idProject, idProduct));
        }else {
            cursorPP.moveToFirst();
            idPP = cursorPP.getInt(0);
        }
        cursorPP.close();

        //присвыаеиваем ид продукта и проекта в адд таблицу
        double price = Double.parseDouble(price_edit.getEditText().getText().toString());
        double count = Double.parseDouble(add_edit.getEditText().getText().toString());

        String categoryProduct = category.getText().toString();
        String dateProduct = date.getEditText().toString();

        myDB.insertToDbProductAdd(count,categoryProduct, price,dateProduct, idPP);

        Toast.makeText(getActivity(),"Добавили " + name + " " + count + " " + suffix + " за " + price + " ₽", Toast.LENGTH_LONG).show();

        if (!categoryList.contains(categoryProduct)) {
            categoryList.add(categoryProduct);
        }

        addDB(name);
    }
    //Формируем список из БД
    public void addDB(String product) {

        Cursor cursor = myDB.selectProductJoin(idProject, product, MyConstanta.TABLE_NAME_ADD);
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

        Cursor cursorWriteOff = myDB.selectProductJoin(idProject, product, MyConstanta.TABLE_NAME_WRITEOFF);

        if (cursorWriteOff != null && cursorWriteOff.getCount() != 0) {
            cursorWriteOff.moveToFirst();
            productUnitWriteOff = cursorWriteOff.getDouble(1);

        }
        cursorWriteOff.close();

        double nowUnitProduct = productUnitAdd - productUnitWriteOff;

        nowUnit.setText(productName + " " + nowUnitProduct + " " + suffix);

    }

    public void setArrayAdapter(){
        //Товар
        arrayAdapterProduct = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, productNameList);
        productName.setAdapter(arrayAdapterProduct);

        //Категории
        arrayAdapterCategory = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categoryList);
        category.setAdapter(arrayAdapterCategory);
    }


}
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

import com.google.android.material.appbar.MaterialToolbar;
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

public class WriteOffFragment extends Fragment {
    private MyDatabaseHelper myDB;
    private TextInputLayout add_edit, date, productNameMenu,suffixMenu,categoryMenu ;
    private AutoCompleteTextView productName, suffixSpiner, category;
    private ArrayAdapter<String> arrayAdapterProduct, arrayAdapterCategory, arrayAdapterSuffix;
    private List<String> productNameList, categoryList, suffixList;
    private MaterialDatePicker datePicker;
    private int idProject;
    private TextView nowUnit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View layout = inflater.inflate(R.layout.fragment_write_off, container, false);

        myDB = new MyDatabaseHelper(getActivity());
        categoryList = new ArrayList<>();
        productNameList = new ArrayList<>();
        suffixList = new ArrayList<>();

        MainActivity mainActivity = new MainActivity();
        idProject = mainActivity.getProjectNumer();

        MaterialToolbar appBar = getActivity().findViewById(R.id.topAppBar);
        appBar.setTitle("Мои Списания");
        appBar.getMenu().findItem(R.id.filler).setVisible(false);
        appBar.getMenu().findItem(R.id.moreAll).setVisible(true);
        appBar.getMenu().findItem(R.id.magazine).setVisible(true);
        appBar.setOnMenuItemClickListener(item -> {
            int position = item.getItemId();
            if (position == R.id.moreAll) {
                replaceFragment(new InFragment());
                appBar.setTitle("Информация");
            } else if (position==R.id.magazine) {
            replaceFragment(new MagazineManagerFragment());
        }
            return true;
        });

        appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        productName = layout.findViewById(R.id.productName_editText);
        add_edit = layout.findViewById(R.id.add_edit);
        suffixSpiner = layout.findViewById(R.id.suffixSpiner);
        category = layout.findViewById(R.id.category_edit);
        date = layout.findViewById(R.id.date);
        nowUnit = layout.findViewById(R.id.now_warehouse);

        productNameMenu = layout.findViewById(R.id.product_name_menu);
        suffixMenu = layout.findViewById(R.id.suffix_menu);
        categoryMenu = layout.findViewById(R.id.category_menu);

        addProduct();


        // Настройка календаря
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        date.getEditText().setText(calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR));

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

                Cursor cursorProduct = myDB.seachProduct(productClick);
                suffixList.clear();
                while (cursorProduct.moveToNext()) {
                    suffixList.add(cursorProduct.getString(2));
                }
                cursorProduct.close();
                setArrayAdapter();
                addDB(productClick, 0, suffixList.get(0));
            }
        });

        suffixSpiner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String productClick = productName.getText().toString();
                addDB(productClick, 0, suffixSpiner.getText().toString());
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
        Cursor cursor = myDB.seachProductToProject(idProject);

        Set<String> categoryHashSet = new HashSet<>();
        Set<String> productHashSet = new HashSet<>();

        while (cursor.moveToNext()) {
            productHashSet.add(cursor.getString(0));
            categoryHashSet.add(cursor.getString(1));
        }

        for (String name : productHashSet) {
            productNameList.add(name);
        }

        for (String category : categoryHashSet) {
            categoryList.add(category);
        }


    }

    public void addInDB() {
        add_edit.setErrorEnabled(false);
        date.setErrorEnabled(false);
        categoryMenu.setErrorEnabled(false);
        suffixMenu.setErrorEnabled(false);
        productNameMenu.setErrorEnabled(false);

        if (productName.getText().toString().equals("") || suffixSpiner.getText().toString().equals("")
                || add_edit.getEditText().getText().toString().equals("") || category.getText().toString().equals("")
                || date.getEditText().getText().toString().equals("")) {

            if (productName.getText().toString().equals("")) {
                productNameMenu.setError("Выберите товар!");
                productNameMenu.getError();
            }

            if (suffixSpiner.getText().toString().equals("")) {
                suffixMenu.setError("Выберите единицу!");
                suffixMenu.getError();
            }

            if (add_edit.getEditText().getText().toString().equals("")) {
                add_edit.setError("Укажите кол-во товара!");
                add_edit.getError();
            }
            if (category.getText().toString().equals("")) {
                categoryMenu.setError("Укажите категорию!");
                categoryMenu.getError();
            }

            if (date.getEditText().getText().toString().equals("")) {
                date.setError("Укажите дату!");
                date.getError();
            }

        } else {
            //Достаем из андройда
            String name = productName.getText().toString();
            String suffix = suffixSpiner.getText().toString();
            double count = Double.parseDouble(add_edit.getEditText().getText().toString().replaceAll(",", ".").replaceAll("[^\\d.]", ""));
            String categoryProduct = category.getText().toString();
            String dateProduct = date.getEditText().getText().toString();

            if (addDB(name, count, suffix)) {
                // проверяем продукт в БД
                Cursor cursorProduct = myDB.seachProductAndSuffix(name, suffix);
                cursorProduct.moveToFirst();
                int idProduct = cursorProduct.getInt(0);
                cursorProduct.close();

                //проверяем связку продукт архив
                Cursor cursorPP = myDB.seachPP(idProject, idProduct);
                cursorPP.moveToFirst();
               int idPP = cursorPP.getInt(0);
                cursorPP.close();

                //присвыаеиваем ид продукта и проекта в адд таблицу

                myDB.insertToDbProductWriteOff(count, categoryProduct, dateProduct, idPP);

                Toast.makeText(getActivity(), "Списали " + name + " " + count + " " + suffix, Toast.LENGTH_LONG).show();

                if (!categoryList.contains(categoryProduct)) {
                    categoryList.add(categoryProduct);
                }
            }
        }
    }

    //Формируем список из БД
    public boolean addDB(String product, double count, String suffixName) {

        Cursor cursor = myDB.selectProductJoin(idProject, product, MyConstanta.TABLE_NAME_ADD, suffixName);
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

        Cursor cursorWriteOff = myDB.selectProductJoin(idProject, product, MyConstanta.TABLE_NAME_WRITEOFF, suffixName);

        if (cursorWriteOff != null && cursorWriteOff.getCount() != 0) {
            cursorWriteOff.moveToFirst();
            productUnitWriteOff = cursorWriteOff.getDouble(1);

        }
        cursorWriteOff.close();

        double nowUnitProduct = productUnitAdd - (productUnitWriteOff + count);
        double wareHouseUnitProduct = productUnitAdd - productUnitWriteOff;

        if (nowUnitProduct < 0) {
            add_edit.setError("Столько товара нет на складе!\nВы можете списать только " + wareHouseUnitProduct  );
            add_edit.getError();
            return false;
        } else {
            if (productName == null || suffixName == null){
                nowUnit.setText(" На складе  нет такого товара ");
            }else {
                nowUnit.setText(" На складе " + productName + " " + nowUnitProduct + " " + suffixName);}

            return true;
        }


    }

    public void setArrayAdapter() {
        //Товар
        arrayAdapterProduct = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, productNameList);
        productName.setAdapter(arrayAdapterProduct);

        //Категории
        arrayAdapterCategory = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categoryList);
        category.setAdapter(arrayAdapterCategory);

        //Категории
        arrayAdapterSuffix = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, suffixList);
        suffixSpiner.setAdapter(arrayAdapterSuffix);
    }

    private void replaceFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }
}
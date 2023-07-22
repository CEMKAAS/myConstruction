package com.zaroslikov.myconstruction;

import android.content.DialogInterface;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

public class UpdateProductFragment extends Fragment {

    private TextInputLayout add_edit, date, price_edit, productNameMenu, suffixMenu, categoryMenu;
    private AutoCompleteTextView productName, suffixSpiner, category;
    private MyDatabaseHelper myDB;
    private ArrayAdapter<String> arrayAdapterProduct, arrayAdapterCategory, arrayAdapterSuffix;
    private List<String> productNameList, categoryList;
    private MaterialDatePicker datePicker;
    private List<Product> productList;
    private int idProject;
    private String nameMagazine;

    private TextView nowUnit, nowWarehouse;

    private Product productUpDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_update_product, container, false);

        // Подключаемся к базе
        myDB = new MyDatabaseHelper(getActivity());
        productList = new ArrayList<>();
        categoryList = new ArrayList<>();
        productNameList = new ArrayList<>();

        //Узнаем  ID проекта
        MainActivity mainActivity = new MainActivity();
        idProject = mainActivity.getProjectNumer();
        //Получаем данные из другого фрагмента
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            productUpDate = bundle.getParcelable("product");
            nameMagazine = bundle.getString("id");
        }
        //Подключаем фронт
        productName = layout.findViewById(R.id.productName_editText);
        add_edit = layout.findViewById(R.id.add_edit);
        price_edit = layout.findViewById(R.id.price_edit);
        suffixSpiner = layout.findViewById(R.id.suffixSpiner);
        category = layout.findViewById(R.id.category_edit);
        date = layout.findViewById(R.id.date);
        nowUnit = layout.findViewById(R.id.now_warehouse);
        nowWarehouse = layout.findViewById(R.id.now_unit);
        //Подключаем Фронт для спинеров
        productNameMenu = layout.findViewById(R.id.product_name_add_menu);
        suffixMenu = layout.findViewById(R.id.suffix_add_menu);
        categoryMenu = layout.findViewById(R.id.category_add_menu);
        //Настройка верхней строки
        MaterialToolbar appBar = getActivity().findViewById(R.id.topAppBar);
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
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


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

        //Назначае каждой строке
        productName.setText(productUpDate.getName());
        add_edit.getEditText().setText(String.valueOf(productUpDate.getCount()));
        suffixSpiner.setText(productUpDate.getSuffix(),false);
        price_edit.getEditText().setText(String.valueOf(productUpDate.getPrice()));
        category.setText(productUpDate.getCategory());
        date.getEditText().setText(productUpDate.getDate());


        //Все зависит от раздела
        if (nameMagazine.equals("Мои Покупки")) {
            price_edit.setVisibility(View.VISIBLE);
            nowUnit.setVisibility(View.GONE);
        } else if (nameMagazine.equals("Мои Списания")) {
            //суффикс, цену и ввод имени убираем
            nowUnit.setText(productUpDate.getName().toUpperCase() + " c ед. изм. " + productUpDate.getSuffix().toUpperCase());
            suffixMenu.setVisibility(View.GONE);
            productNameMenu.setVisibility(View.GONE);
            price_edit.setVisibility(View.GONE);
        }

        addDB(productUpDate.getName(), productUpDate.getCount(), productUpDate.getSuffix());


        //Берем из бд товары и добавляем в список
        addProduct();

        productName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Назначаем суффикс из продукта
                String productClick = productNameList.get(position);

                Cursor cursorProduct = myDB.seachProduct(productClick);

                while (cursorProduct.moveToNext()) {
                    suffixMenu.getEditText().setText(cursorProduct.getString(2));
                }
                cursorProduct.close();

                setArrayAdapter();

            }
        });

        //Кнопка обновления
        Button updateButton = layout.findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nameMagazine.equals("Мои Покупки")) {
                    upDateProductADD();
                } else if (nameMagazine.equals("Мои Списания")) {
                    upDateProductWriteOff();
                }
            }
        });

        //Кнопка удаления
        Button deleteButton = layout.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
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

    //Добавляем продукцию в список из БД
    public void addProduct() {
        Cursor cursor = myDB.readProduct();

        while (cursor.moveToNext()) {
            productNameList.add(cursor.getString(1));
        }
        cursor.close();

        //Через сет, чтобы не было повторов
        Set<String> tempList = new HashSet<>();
        Cursor cursor1 = myDB.seachCategory(idProject);

        while (cursor1.moveToNext()) {
            tempList.add(cursor1.getString(0));
        }
        cursor1.close();

        for (String nameExpenses : tempList) {
            categoryList.add(nameExpenses);
        }

    }

    //Обновляем базу данных
    public void upDateProductADD() {
        //Очищаем ошибки
        add_edit.setErrorEnabled(false);
        date.setErrorEnabled(false);
        categoryMenu.setErrorEnabled(false);
        suffixMenu.setErrorEnabled(false);
        price_edit.setErrorEnabled(false);
        productNameMenu.setErrorEnabled(false);

        //проверяем есть ли ошибки
        if (productName.getText().toString().equals("") || suffixSpiner.getText().toString().equals("")
                || add_edit.getEditText().getText().toString().equals("") || category.getText().toString().equals("")
                || date.getEditText().getText().toString().equals("") || price_edit.getEditText().getText().toString().equals("")) {

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

            if (price_edit.getEditText().getText().toString().equals("")) {
                price_edit.setError("Укажите цену!");
                price_edit.getError();
            }

        } else {
            //Достаем из Фронта все данные
            String name = productName.getText().toString();
            String suffix = suffixSpiner.getText().toString();
            double price = Double.parseDouble(price_edit.getEditText().getText().toString());
            double count = Double.parseDouble(add_edit.getEditText().getText().toString());
            String categoryProduct = category.getText().toString();
            String dateProduct = date.getEditText().getText().toString();
            //Константы
            int[] idProduct = {0};
            int idPP = 0;

            //Проверяем продукт в БД
            Cursor cursorProduct = myDB.seachProductAndSuffix(name, suffix);

            //Если нет товара, то тогда
            if (cursorProduct.getCount() == 0) {
                cursorProduct.close();

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                builder.setTitle("Товара " + name.toUpperCase() + " c ед.изм " + suffix.toUpperCase() + " нет!");
                builder.setMessage("Вы хотите ИЗМЕНИТЬ ВСЕ записи с " + productUpDate.getName().toUpperCase() + " c ед.изм " + productUpDate.getSuffix().toUpperCase()
                        + " на " + name.toUpperCase() + " c ед.изм " + suffix.toUpperCase()  +
                        "\nИли  ДОБАВИТЬ c ЗАМЕНОЙ на новый товар " + name.toUpperCase()  + " c ед.изм " + suffix.toUpperCase()  + " с данными значениями?");

                builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Вы добавили товар ", Toast.LENGTH_SHORT).show();
                        idProduct[0] = Math.toIntExact(myDB.insertToDbProduct(name, suffix));
                        productNameList.add(name);
                        cursorUpdate(idProduct, idPP, count, categoryProduct, price, dateProduct);
                    }
                });

                builder.setNegativeButton("Изменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        idProduct[0] = Math.toIntExact(myDB.updateToDbProduct(productUpDate.getName(), name, suffix));
                        if (addDB(productUpDate.getName(), count, productUpDate.getSuffix())) {
                            cursorUpdate(idProduct, idPP, count, categoryProduct, price, dateProduct);
                        }
                    }
                });

                builder.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });

                builder.show();

            } else {

                cursorProduct.moveToFirst();
                idProduct[0] = cursorProduct.getInt(0);
                cursorProduct.close();

                if (addDB(name, count, suffix)) {
                    cursorUpdate(idProduct, idPP, count, categoryProduct, price, dateProduct);

                }

            }
        }
    }

    //Обновляем Списание
    public void upDateProductWriteOff() {
        add_edit.setErrorEnabled(false);
        date.setErrorEnabled(false);
        categoryMenu.setErrorEnabled(false);

        if (add_edit.getEditText().getText().toString().equals("") || category.getText().toString().equals("")
                || date.getEditText().getText().toString().equals("")) {

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
            //Достаем из андройда имяПродукта и суффикс
            double count = Double.parseDouble(add_edit.getEditText().getText().toString());
            String categoryProduct = category.getText().toString();
            String dateProduct = date.getEditText().getText().toString();

            final int[] idProduct = {0};
            int idPP = 0;

            // проверяем продукт в БД
            Cursor cursorProduct = myDB.seachProductAndSuffix(productUpDate.getName(), productUpDate.getSuffix());
            cursorProduct.moveToFirst();
            idProduct[0] = cursorProduct.getInt(0);
            cursorProduct.close();
            if (addDB(productUpDate.getName(), count, productUpDate.getSuffix())) {
                cursorUpdate(idProduct, idPP, count, categoryProduct, 0, dateProduct);
            }
        }
    }

    //Проверяем уходим ли в минус или нет
    public boolean addDB(String name, double count, String suffix) {

        Cursor cursor = myDB.selectProductJoin(idProject, name, MyConstanta.TABLE_NAME_ADD, suffix);
        String productName = null;
        double productUnitAdd = 0;
        double productUnitWriteOff = 0;
        String suffixName = null;

        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            productName = cursor.getString(0);

            productUnitAdd = cursor.getDouble(1);

            suffixName = cursor.getString(2);

        }
        cursor.close();

        Cursor cursorWriteOff = myDB.selectProductJoin(idProject, name, MyConstanta.TABLE_NAME_WRITEOFF, suffix);

        if (cursorWriteOff != null && cursorWriteOff.getCount() != 0) {
            cursorWriteOff.moveToFirst();
            productUnitWriteOff = cursorWriteOff.getDouble(1);

        }
        cursorWriteOff.close();

        double diff = productUpDate.getCount() - count;
        double nowUnitProduct = 0;
        double wareHouseUnitProduct = productUnitAdd - productUnitWriteOff;

        if (nameMagazine.equals("Мои Покупки")) {
            nowUnitProduct = (productUnitAdd - diff) - productUnitWriteOff;

            if (nowUnitProduct < 0) {

                add_edit.setError("Столько товара нет на складе!\nу Вас списано " + productUnitWriteOff);
                add_edit.getError();

                return false;
            }

        } else if (nameMagazine.equals("Мои Списания")) {
            nowUnitProduct = productUnitAdd  - (productUnitWriteOff- diff);

            if (nowUnitProduct < 0) {

                add_edit.setError("Столько товара нет на складе!\nу Вас добавленно " + productUnitAdd);
                add_edit.getError();

                return false;
            }
        }





            nowWarehouse.setText("Cейчас на складе " + wareHouseUnitProduct + " "  + name);

        return true;
    }

    public void cursorUpdate(int idProduct[], int idPP, double count, String categoryProduct,
                             double price, String dateProduct) {

        //проверяем связку продукт архив
        Cursor cursorPP = myDB.seachPP(idProject, idProduct[0]);

        if (cursorPP.getCount() == 0) {
            idPP = Math.toIntExact(myDB.insertToDbProjectProduct(idProject, idProduct[0]));
        } else {
            cursorPP.moveToFirst();
            idPP = cursorPP.getInt(0);
        }
        cursorPP.close();

        if (nameMagazine.equals("Мои Покупки")) {
            myDB.updateToDbAdd(count, categoryProduct, price, dateProduct, idPP, productUpDate.getId());
        } else if (nameMagazine.equals("Мои Списания")) {
            myDB.updateToDbWriteOff(count, categoryProduct, dateProduct, idPP, productUpDate.getId());
        }

        Toast.makeText(getActivity(), "Обновленно", Toast.LENGTH_LONG).show();
        replaceFragment(new MagazineManagerFragment());
        if (!categoryList.contains(categoryProduct)) {
            categoryList.add(categoryProduct);
        }
    }

    public void deleteProduct() {
        if (nameMagazine.equals("Мои Покупки")) {
            myDB.deleteOneRowAdd(productUpDate.getId(), MyConstanta.TABLE_NAME_ADD);
        } else if (nameMagazine.equals("Мои Списания")) {
            myDB.deleteOneRowAdd(productUpDate.getId(), MyConstanta.TABLE_NAME_WRITEOFF);
        }

        replaceFragment(new MagazineManagerFragment());
    }

    private void replaceFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }

    public void setArrayAdapter() {
        //Товар
        arrayAdapterProduct = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, productNameList);
        productName.setAdapter(arrayAdapterProduct);

        //Категории
        arrayAdapterCategory = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categoryList);
        category.setAdapter(arrayAdapterCategory);

    }

}
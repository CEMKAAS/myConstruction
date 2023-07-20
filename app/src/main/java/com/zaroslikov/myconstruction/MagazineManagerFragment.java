package com.zaroslikov.myconstruction;

import android.database.Cursor;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class MagazineManagerFragment extends Fragment {
    private MyDatabaseHelper myDB;
    private String appBarManager;
    private Cursor cursorManager;
    private int idProject, myRow, visibility;
    private CustomAdapterMagazine customAdapterMagazine;
    private RecyclerView recyclerView;
    private ImageView empty_imageview;
    private Date dateFirst, dateEnd;
    private Boolean magazineAddBool, clickBool;
    private List<Product> products, productNow;
    private TextView no_data, sixColumn, dicsPrice;
    private AutoCompleteTextView animalsSpinerSheet, categorySpinerSheet;
    private MaterialDatePicker<Pair<Long, Long>> datePicker;
    private List<String> productNameList, categoryList;
    private Button buttonSheet;
    private TextInputLayout dataSheet;
    private BottomSheetDialog bottomSheetDialog;
    private ArrayAdapter<String> arrayAdapterProduct, arrayAdapterCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_magazine_manager, container, false);
        //Подключение к базе данных
        myDB = new MyDatabaseHelper(getActivity());
        MainActivity mainActivity = new MainActivity();
        idProject = mainActivity.getProjectNumer();

        products = new ArrayList<>();
        productNow = new ArrayList<>();
        categoryList = new ArrayList<>();
        productNameList = new ArrayList<>();

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
                bottomSheetDialog.show();
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
            }
        });

        appBarManager = appBar.getTitle().toString();

        if (appBarManager.equals("Мои Покупки")) {
            cursorManager = myDB.readAddMagazine(idProject);
            visibility = View.VISIBLE;
            magazineAddBool = true;
            clickBool = true;
            myRow = R.layout.my_row_add;
        } else if (appBarManager.equals("Мои Списания")) {
            cursorManager = myDB.readWriteOffMagazine(idProject);
            visibility = View.GONE;
            magazineAddBool = false;
            clickBool = true;
            myRow = R.layout.my_row_write_off;
        } else {
            cursorManager = myDB.readAddMagazine(idProject);
            visibility = View.VISIBLE;
            magazineAddBool = true;
            clickBool = false;
            myRow = R.layout.my_row_add;
        }

        //Создание отображения списка
        sixColumn = layout.findViewById(R.id.six_column);
        recyclerView = layout.findViewById(R.id.recyclerView);
        empty_imageview = layout.findViewById(R.id.empty_imageview);
        no_data = layout.findViewById(R.id.no_data);

        sixColumn.setVisibility(visibility);

        //Добавдение товаров в лист
        storeDataInArraysClass(cursorManager);
        addProduct();

        //Создание модального bottomSheet
        showBottomSheetDialog();


        //Создание адаптера
        customAdapterMagazine = new CustomAdapterMagazine(productNow, myRow);

        recyclerView.setAdapter(customAdapterMagazine);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        if(clickBool) {
            //Запускаем при нажатии
            customAdapterMagazine.setListener(new CustomAdapterMagazine.Listener() {
                @Override
                public void onClick(int position, Product product) {
                    addChart(product);
                }
            });
        }
// Настройка календаря на период
        CalendarConstraints constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build();

        datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setCalendarConstraints(constraintsBuilder)
                .setTitleText("Выберите даты")
                .setSelection(
                        Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()
                        ))
                .build();

        dataSheet.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getActivity().getSupportFragmentManager(), "wer");
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

                        Long startDate = selection.first;
                        Long endDate = selection.second;

                        calendar.setTimeInMillis(startDate);
                        calendar2.setTimeInMillis(endDate);

                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                        String formattedDate1 = format.format(calendar.getTime());
                        String formattedDate2 = format.format(calendar2.getTime());

                        try {
                            dateFirst = format.parse(formattedDate1);
                            dateEnd = format.parse(formattedDate2);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        dataSheet.getEditText().setText(formattedDate1 + "-" + formattedDate2);
                    }
                });
            }
        });
        // Настройка кнопки в bottomSheet
        buttonSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    filter();
                    customAdapterMagazine = new CustomAdapterMagazine(productNow, myRow);

                    recyclerView.setAdapter(customAdapterMagazine);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                    bottomSheetDialog.dismiss();

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
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

    //Добавляем bottobSheet
    public void showBottomSheetDialog() {

        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.fragment_bottom);

        animalsSpinerSheet = bottomSheetDialog.findViewById(R.id.product_spiner_sheet);
        categorySpinerSheet = bottomSheetDialog.findViewById(R.id.categiry_spiner_sheet);

        animalsSpinerSheet.setText("Все",false);
        categorySpinerSheet.setText("Все", false);

        dataSheet = bottomSheetDialog.findViewById(R.id.data_sheet);
        buttonSheet = bottomSheetDialog.findViewById(R.id.button_sheet);
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


    public void addProduct() {

        Set<String> categoryHashSet = new HashSet<>();
        Set<String> productHashSet = new HashSet<>();

        for (Product product : products) {
            productHashSet.add(product.getName());
            categoryHashSet.add(product.getCategory());
        }

        for (String name : productHashSet) {
            productNameList.add(name);
        }

        for (String category : categoryHashSet) {
            categoryList.add(category);
        }
        productNow.addAll(products);
        productNameList.add("Все");
        categoryList.add("Все");
    }


    public void filter() throws ParseException {

        productNow.clear();

        String animalsSpinerSheetText = animalsSpinerSheet.getText().toString();
        String categorySpinerSheetText = categorySpinerSheet.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

        if (animalsSpinerSheetText.equals("Все") && categorySpinerSheetText.equals("Все") && dataSheet.getEditText().getText().toString().equals("")) {
            productNow.addAll(products);

        } else if (animalsSpinerSheetText.equals("Все") && categorySpinerSheetText.equals("Все") && !dataSheet.getEditText().getText().toString().equals("")) {

                for (Product product : products) {

                    Date dateNow = format.parse(product.getDate());

                    if ((dateFirst.before(dateNow) && dateEnd.after(dateNow)) || dateFirst.equals(dateNow) || dateEnd.equals(dateNow)) {
                        productNow.add(product);

                    }
                }

        }else if (animalsSpinerSheetText.equals("Все") && !categorySpinerSheetText.equals("Все") && dataSheet.getEditText().getText().toString().equals("")) {

                for (Product product : products) {

                    if (categorySpinerSheetText.equals(product.getCategory())) {
                        productNow.add(product);

                    }
                }
        }else if (!animalsSpinerSheetText.equals("Все") && categorySpinerSheetText.equals("Все") && dataSheet.getEditText().getText().toString().equals("")) {

            for (Product product : products) {

                if (animalsSpinerSheetText.equals(product.getName())) {
                    productNow.add(product);
                }
            }

        }else if (animalsSpinerSheetText.equals("Все") && !categorySpinerSheetText.equals("Все") && !dataSheet.getEditText().getText().toString().equals("")) {

            for (Product product : products) {

                Date dateNow = format.parse(product.getDate());

                if (categorySpinerSheetText.equals(product.getCategory()) &&
                        ((dateFirst.before(dateNow) && dateEnd.after(dateNow)) || dateFirst.equals(dateNow) || dateEnd.equals(dateNow))) {
                    productNow.add(product);
                }
            }
        }else if (!animalsSpinerSheetText.equals("Все") && categorySpinerSheetText.equals("Все") && !dataSheet.getEditText().getText().toString().equals("")) {

            for (Product product : products) {

                Date dateNow = format.parse(product.getDate());

                if (animalsSpinerSheetText.equals(product.getName()) &&
                        ((dateFirst.before(dateNow) && dateEnd.after(dateNow)) || dateFirst.equals(dateNow) || dateEnd.equals(dateNow))) {
                    productNow.add(product);
                }
            }

        }else if (!animalsSpinerSheetText.equals("Все") && !categorySpinerSheetText.equals("Все") && dataSheet.getEditText().getText().toString().equals("")) {

            for (Product product : products) {

                if (animalsSpinerSheetText.equals(product.getName()) && categorySpinerSheetText.equals(product.getCategory())) {
                    productNow.add(product);
                }
            }


        } else if (!animalsSpinerSheetText.equals("Все") && !categorySpinerSheetText.equals("Все") && !dataSheet.getEditText().getText().toString().equals("")) {

            for (Product product : products) {

                Date dateNow = format.parse(product.getDate());

                if (animalsSpinerSheetText.equals(product.getName()) && categorySpinerSheetText.equals(product.getCategory()) &&
                        ((dateFirst.before(dateNow) && dateEnd.after(dateNow)) || dateFirst.equals(dateNow) || dateEnd.equals(dateNow))) {
                    productNow.add(product);
                }
            }
        }
    }

    public void setArrayAdapter() {
        //Товар
        arrayAdapterProduct = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, productNameList);
        animalsSpinerSheet.setAdapter(arrayAdapterProduct);

        //Категории
        arrayAdapterCategory = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categoryList);
        categorySpinerSheet.setAdapter(arrayAdapterCategory);

    }

}
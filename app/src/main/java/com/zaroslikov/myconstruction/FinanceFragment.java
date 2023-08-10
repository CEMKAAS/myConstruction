package com.zaroslikov.myconstruction;

import android.database.Cursor;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.zaroslikov.myconstruction.db.MyConstanta;
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

public class FinanceFragment extends Fragment {
    private RecyclerView recyclerViewCategory, recyclerViewProduct;
    private int idProject;
    private MyDatabaseHelper myDB;
    private List<Product> productSumList, categorySumList, productSumListNow, categorySumListNow;
    private TextView allSumText, categoryText, productText;
    private List<String> productNameList, categoryList;
    private TextInputLayout animalsSpinerSheet, categorySpinerSheet;
    private Date dateFirst, dateEnd;
    private MaterialDatePicker<Pair<Long, Long>> datePicker;
    private Button buttonSheet;
    private TextInputLayout dataSheet;
    private BottomSheetDialog bottomSheetDialog;
    private View layout;
    private  TextView til;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        layout = inflater.inflate(R.layout.fragment_finance, container, false);
        //убириаем фаб кнопку
        myDB = new MyDatabaseHelper(getActivity());
        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.setVisibility(View.GONE);

        MaterialToolbar appBar = getActivity().findViewById(R.id.topAppBar);
        appBar.setTitle("Мой Финансы");
        appBar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        appBar.getMenu().findItem(R.id.magazine).setVisible(false);
        appBar.getMenu().findItem(R.id.deleteAll).setVisible(false);
        appBar.getMenu().findItem(R.id.filler).setVisible(true);
        appBar.getMenu().findItem(R.id.moreAll).setVisible(true);
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


        MainActivity mainActivity = new MainActivity();
        idProject = mainActivity.getProjectNumer();

        allSumText = layout.findViewById(R.id.all_sum);
        categoryText = layout.findViewById(R.id.category_txt);
        productText = layout.findViewById(R.id.product_txt);

        //Настройка листа
        categorySumList = new ArrayList();
        productSumList = new ArrayList();

        categorySumListNow = new ArrayList();
        productSumListNow = new ArrayList();

        productNameList = new ArrayList();
        categoryList = new ArrayList<>();

        add();

        //Создание модального bottomSheet
        showBottomSheetDialog();

        tableCategory();
        tableProduct();

//        // Настраиваем адаптер
//        recyclerViewCategory = layout.findViewById(R.id.recyclerView);
//        recyclerViewProduct = layout.findViewById(R.id.recyclerViewAll);
//
//        ProductAdapter productAdapterCategory = new ProductAdapter(categorySumListNow, true);
//        recyclerViewCategory.setAdapter(productAdapterCategory);
//        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        ProductAdapter productAdapterProduct = new ProductAdapter(productSumListNow, true);
//        recyclerViewProduct.setAdapter(productAdapterProduct);
//        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                        productText.setText("По продукции за\n" + formattedDate1 + "-" + formattedDate2);
                        categoryText.setText("По категориям за\n" + formattedDate1 + "-" + formattedDate2);
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


                    tableCategory();
                    tableProduct();
//                    ProductAdapter productAdapterCategory = new ProductAdapter(categorySumListNow, true);
//                    recyclerViewCategory.setAdapter(productAdapterCategory);
//                    recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//                    ProductAdapter productAdapterProduct = new ProductAdapter(productSumListNow, true);
//                    recyclerViewProduct.setAdapter(productAdapterProduct);
//                    recyclerViewProduct.setLayoutManager(new LinearLayoutManager(getActivity()));

                    bottomSheetDialog.dismiss();

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        return layout;
    }


    //Формируем список из БД
    public void add() {

        Cursor cursorProductAdd = myDB.selectProjectAllProductAndCategoryAdd(idProject);

        //Добавляем в списки продукты и категории
        Set<String> productHashSet = new HashSet<>();
        Set<String> categoryHashSet = new HashSet<>();

        while (cursorProductAdd.moveToNext()) {
            productHashSet.add(cursorProductAdd.getString(0));
            categoryHashSet.add(cursorProductAdd.getString(2));
        }
        cursorProductAdd.close();

        for (String name : productHashSet) {
            productNameList.add(name);
        }

        for (String category : categoryHashSet) {
            categoryList.add(category);
        }
        cursorProductAdd.close();

        Cursor cursorAllSum = myDB.selectProjectAllSum(idProject);
        cursorAllSum.moveToFirst();
        double allSum = cursorAllSum.getDouble(0);
        allSumText.setText("Общая сумма: " + allSum + " ₽");
        cursorAllSum.close();

        for (String category : categoryList) {

            Cursor cursorCategory = myDB.selectProjectAllSumCategory(idProject, category);

            while (cursorCategory.moveToNext()) {
                categorySumList.add(new Product(cursorCategory.getString(0), "₽", cursorCategory.getDouble(1), cursorCategory.getString(2)));
            }

            cursorCategory.close();

        }

        for (String product : productNameList) {
            Cursor cursorProduct = myDB.selectProjectAllSumProduct(idProject, product);

            while (cursorProduct.moveToNext()) {
                productSumList.add(new Product(cursorProduct.getString(0), "₽", cursorProduct.getDouble(2), cursorProduct.getString(3)));
            }
            cursorProduct.close();
        }
        categorySumListNow.addAll(categorySumList);
        productSumListNow.addAll(productSumList);
    }


    public void filter() throws ParseException {

        productSumListNow.clear();
        categorySumListNow.clear();

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

        double sumAll  = 0;
        if (!dataSheet.getEditText().getText().toString().equals("")) {

            for (Product productSum : productSumList) {
                Date dateNow = format.parse(productSum.getDate());
                if ((dateFirst.before(dateNow) && dateEnd.after(dateNow)) || dateFirst.equals(dateNow) || dateEnd.equals(dateNow)) {
                    productSumListNow.add(productSum);
                    sumAll += productSum.getPrice();
                }

            }

            for (Product productCategory : categorySumList) {
                Date dateNow = format.parse(productCategory.getDate());
                if ((dateFirst.before(dateNow) && dateEnd.after(dateNow)) || dateFirst.equals(dateNow) || dateEnd.equals(dateNow)) {
                    categorySumListNow.add(productCategory);

                }

            }
        }
        allSumText.setText("Общая сумма: " + sumAll + " ₽");


    }

    private void replaceFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }

    //Добавляем bottobSheet
    public void showBottomSheetDialog() {

        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.fragment_bottom);

        animalsSpinerSheet = bottomSheetDialog.findViewById(R.id.menu);
        categorySpinerSheet = bottomSheetDialog.findViewById(R.id.menu2);

        animalsSpinerSheet.setVisibility(View.GONE);
        categorySpinerSheet.setVisibility(View.GONE);

        dataSheet = bottomSheetDialog.findViewById(R.id.data_sheet);
        buttonSheet = bottomSheetDialog.findViewById(R.id.button_sheet);
    }


    public void tableCategory() {
        TableLayout tableLayout = (TableLayout) layout.findViewById(R.id.table_category);
        int rowI = 0;
        for (Product product : categorySumListNow) {
            TableRow tableRow = new TableRow(getActivity());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
            for (int i = 0; i <= 2; i++) {
                til = new TextView(getActivity());
                switch (i) {
                    case 0:
                        til.setText(product.getName()+ "  ");
                        tableRow.addView(til, i);
                        break;
                    case 1:
                        til.setText(String.valueOf(product.getPrice()));
                        tableRow.addView(til, i);
                        break;
                    case 2:
                        til.setText("  ₽");
                        tableRow.addView(til, i);
                        break;
                }
            }
            tableLayout.addView(tableRow, rowI);
            rowI++;
        }
    }

    public void tableProduct() {
        TableLayout tableLayout = (TableLayout) layout.findViewById(R.id.table_product          );
        int rowI = 0;
        for (Product product : productSumListNow) {
            TableRow tableRow = new TableRow(getActivity());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
            for (int i = 0; i <= 2; i++) {
                til = new TextView(getActivity());
                switch (i) {
                    case 0:
                        til.setText(product.getName()+ "  ");
                        tableRow.addView(til, i);
                        break;
                    case 1:
                        til.setText(String.valueOf(product.getPrice()));
                        tableRow.addView(til, i);
                        break;
                    case 2:
                        til.setText("  ₽");
                        tableRow.addView(til, i);
                        break;
                }
            }
            tableLayout.addView(tableRow, rowI);
            rowI++;
        }
    }




}
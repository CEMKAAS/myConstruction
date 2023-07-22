package com.zaroslikov.myconstruction.project;

import android.content.DialogInterface;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.zaroslikov.myconstruction.CustomAdapterMagazine;
import com.zaroslikov.myconstruction.InFragment;
import com.zaroslikov.myconstruction.MagazineManagerFragment;
import com.zaroslikov.myconstruction.MainActivity;
import com.zaroslikov.myconstruction.Product;
import com.zaroslikov.myconstruction.ProductAdapter;
import com.zaroslikov.myconstruction.ProductArhiveAdapter;
import com.zaroslikov.myconstruction.R;
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

public class ArhiveWarehouseFragment extends Fragment {

    private RecyclerView recyclerViewCategory, recyclerViewProduct;
    private int idProject;
    private MyDatabaseHelper myDB;
    private List<Product> productSumList, categorySumList, productSumListNow, categorySumListNow;
    private TextView dataTxt, allSumText;
    private List<String> productNameList, categoryList;

    private String nameProject, dateProject;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View layout = inflater.inflate(R.layout.fragment_arhive_warehouse, container, false);

      //убириаем фаб кнопку
        myDB = new MyDatabaseHelper(getActivity());
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            nameProject = bundle.getString("name");
            dateProject = bundle.getString("date");
        }

        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.show();
        fab.setText("Журнал");
        fab.setIconResource(R.drawable.baseline_book_24);
        fab.getIcon();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                replaceFragment(new MagazineManagerFragment());

            }
        });

        MaterialToolbar appBar = getActivity().findViewById(R.id.topAppBar);
        appBar.setTitle(nameProject);
        appBar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        appBar.getMenu().findItem(R.id.deleteAll).setVisible(false);
        appBar.getMenu().findItem(R.id.filler).setVisible(false);
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


        MainActivity mainActivity = new MainActivity();
        idProject = mainActivity.getProjectNumer();

        dataTxt = layout.findViewById(R.id.data_txt);
        allSumText = layout.findViewById(R.id.all_sum);

        dataTxt.setText(dateProject);


        //Настройка листа
        categorySumList = new ArrayList();
        productSumList = new ArrayList();

        categorySumListNow = new ArrayList();
        productSumListNow = new ArrayList();

        productNameList = new ArrayList();
        categoryList = new ArrayList<>();

        add();

        // Настраиваем адаптер
        recyclerViewCategory = layout.findViewById(R.id.recyclerView);
        recyclerViewProduct = layout.findViewById(R.id.recyclerViewAll);

        ProductAdapter productAdapterCategory = new ProductAdapter (categorySumListNow, true);
        recyclerViewCategory.setAdapter(productAdapterCategory);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getActivity()));

        ProductArhiveAdapter  productAdapterProduct = new ProductArhiveAdapter (productSumListNow);
        recyclerViewProduct.setAdapter(productAdapterProduct);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(getActivity()));



        Button returnButton = layout.findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                builder.setTitle("Вернуть проект?");
                builder.setMessage("Возможно вы еще не доконца закончили проект." +
                        " Его можно будет вернуть потом обратно в архив!");

                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myDB.updateToDbProject(idProject, 0, "");
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

        Button deleteButton = layout.findViewById(R.id.end_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                builder.setTitle("Удалить проект?");
                builder.setMessage("Ваш проект удалиться со всеми данными, вы уверенны? ");

                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myDB.deleteOneRowAdd(idProject, MyConstanta.TABLE_NAME);
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
            Cursor cursorProduct = myDB.selectProjectAllSumProductAndCount(idProject, product);

            while (cursorProduct.moveToNext()) {
                productSumList.add(new Product(cursorProduct.getString(0), cursorProduct.getString(1), cursorProduct.getDouble(2),
                        cursorProduct.getString(3), cursorProduct.getDouble(4)));
            }
            cursorProduct.close();
        }
        categorySumListNow.addAll(categorySumList);
        productSumListNow.addAll(productSumList);
    }

    private void replaceFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }
}
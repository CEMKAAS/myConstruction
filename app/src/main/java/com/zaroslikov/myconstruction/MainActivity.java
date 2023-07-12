package com.zaroslikov.myconstruction;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.zaroslikov.myconstruction.databinding.ActivityMainBinding;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;
import com.zaroslikov.myconstruction.project.MenuProjectFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyDatabaseHelper myDB;
    private ExtendedFloatingActionButton fab;
    private MaterialToolbar appBar;
    private int position = 0;

    public static int projectNumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myDB = new MyDatabaseHelper(this);

        fab = findViewById(R.id.extended_fab);
        fab.setVisibility(View.GONE);

        appBar = findViewById(R.id.topAppBar);
        // AppBar

        appBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
//                case R.id.menu:
//                    replaceFragment(new InFragment());
//                    appBar.setTitle("Информация");
//                    fab.hide();
//                    fab.setVisibility(View.GONE);
//                    break;
//
//                case R.id.deleteAll:
//                    beginIncubator();
//                    break;
            }
            return true;
        });

        set();
        binding.navView.setVisibility(View.GONE);
        binding.navView.setOnNavigationItemSelectedListener(item -> {
            position = item.getItemId();

            if(position == R.id.warehouse_button){
                replaceFragment(new WarehouseFragment());
                appBar.setTitle("Мой Склад");
                fab.hide();
                fab.setVisibility(View.GONE);
            } else if (position==R.id.add_button){
                replaceFragment(new AddFragment());
            } else if (position == R.id.writeOff_button){
                replaceFragment(new WriteOffFragment());
            } else if (position == R.id.finance_button) {
                replaceFragment(new FinanceFragment());
            }

//                case R.id.finance_button:
//                    replaceFragment(new FinanceFragment());
//                    appBar.setTitle("Мои Финансы");
//                    fab.show();
//                    fab.setText("Цена");
//                    fab.setIconResource(R.drawable.ic_action_price);
//                    fab.getIcon();
//
//                    fab.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            replaceFragment(new PriceFragment());
//                            appBar.setTitle("Моя Цена");
//                            fab.hide();
//                            fab.setVisibility(View.GONE);
//                        }
//                    });
//                    break;

//                case R.id.sale_button:
//                    replaceFragment(new SaleFragment());
//                    fba(new AddManagerFragment());
//                    break;
//
//                case R.id.expenses_button:
//                    replaceFragment(new ExpensesFragment());
//                    fba(new AddManagerFragment());
//                    break;

            return true;
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("visible_fragment");
                if (fragment instanceof WarehouseFragment) {
                    binding.navView.setVisibility(View.VISIBLE);
                    position = 0;
                }
                if (fragment instanceof MenuProjectFragment) {
                    binding.navView.setVisibility(View.GONE);
                }
                if (fragment instanceof FinanceFragment) {
                    binding.navView.setVisibility(View.VISIBLE);
                    position = 1;
                }
                if (fragment instanceof AddFragment) {
                    binding.navView.setVisibility(View.VISIBLE);
                    position = 2;
                }
                if (fragment instanceof WriteOffFragment) {
                    binding.navView.setVisibility(View.VISIBLE);
                    position = 3;
                }
                binding.navView.getMenu().getItem(position).setChecked(true);
            }
        }
        );


//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void set() {
        Cursor cursor = myDB.readProject();

        if (cursor.getCount() == 0) {
            replaceFragment(new AddProjectFragment());
        } else if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            setProjectNumer(cursor.getInt(0));

            replaceFragment(new WarehouseFragment());
        } else {
            replaceFragment(new MenuProjectFragment());
        }
        cursor.close();
    }

    //Переходим на фрагмент
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }

    public void beginIncubator() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Удаляем все ?");
        builder.setMessage("Вы уверены, что хотите удалить все проекты, включая архивные?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
                myDB.deleteAllData();
                replaceFragment(new WarehouseFragment());
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    public void setProjectNumer(int projectNumer){
        this.projectNumer = projectNumer;
    }
    public int getProjectNumer(){
        return projectNumer;
    }
}
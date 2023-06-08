package com.zaroslikov.myconstruction;

import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.zaroslikov.myconstruction.databinding.ActivityMainBinding;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;
import com.zaroslikov.myconstruction.project.MenuProjectFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyDatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myDB = new MyDatabaseHelper(this);

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
            replaceFragment(new WarehouseFragment());
        } else {
            replaceFragment(new MenuProjectFragment());
        }
    }

    //Переходим на фрагмент
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }
}
package com.zaroslikov.myconstruction.project;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.zaroslikov.myconstruction.AddProjectFragment;
import com.zaroslikov.myconstruction.InFragment;
import com.zaroslikov.myconstruction.R;
import com.zaroslikov.myconstruction.WarehouseFragment;
import com.zaroslikov.myconstruction.databinding.ActivityMainBinding;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;

import java.util.ArrayList;

public class MenuProjectFragment extends Fragment {
    private MyDatabaseHelper myDB;
    private RecyclerView recyclerView;
    private ArrayList<String> id, name, type, data;
    private ActivityMainBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_menu_project, container, false);
        myDB = new MyDatabaseHelper(getActivity());
        //убириаем фаб кнопку
        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.setVisibility(View.VISIBLE);

        //настройка верхнего меню
        MaterialToolbar appBar = getActivity().findViewById(R.id.topAppBar);
        appBar.setTitle("Мои Проекты");
        appBar.getMenu().findItem(R.id.deleteAll).setVisible(true);
        appBar.getMenu().findItem(R.id.filler).setVisible(false);
        appBar.getMenu().findItem(R.id.moreAll).setVisible(true);
        appBar.setNavigationIcon(null);
        appBar.setOnMenuItemClickListener(item -> {
            int position = item.getItemId();
            if (position == R.id.moreAll) {
                replaceFragment(new InFragment());
                appBar.setTitle("Информация");
            } else if (position == R.id.deleteAll) {
                deleteAllData();
            }
            return true;
        });


        TabLayout tabLayout = layout.findViewById(R.id.tab);
        ViewPager2 viewPager2 = layout.findViewById(R.id.view_pager);
        MenuAdapter menuAdapter = new MenuAdapter(getActivity());
        viewPager2.setAdapter(menuAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });


        return layout;
    }

    public void replaceFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }


    public void deleteAllData() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle("Удаляем ВСЕ ?");
        builder.setMessage("Вы уверены, что хотите удалить все проекты, включая архивные?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
                myDB.deleteAllData();
                replaceFragment(new AddProjectFragment());
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }


}
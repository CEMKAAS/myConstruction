package com.zaroslikov.myconstruction.project;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.zaroslikov.myconstruction.AddProjectFragment;
import com.zaroslikov.myconstruction.MainActivity;
import com.zaroslikov.myconstruction.R;
import com.zaroslikov.myconstruction.WarehouseFragment;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;

import java.util.ArrayList;

public class HomeProjectFragment extends Fragment {

    private MyDatabaseHelper myDB;
    private RecyclerView recyclerView;
    private ArrayList<String> name, data;
    private ArrayList<Integer> id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_home_project, container, false);

        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButton(new AddProjectFragment());
            }
        });


        fab.show();
        fab.setText("Добавить");
        fab.setIconResource(R.drawable.baseline_add_24);
        fab.getIcon();

        myDB = new MyDatabaseHelper(getActivity());

        id = new ArrayList<>();
        name = new ArrayList<>();
        data = new ArrayList<>();

        recyclerView = layout.findViewById(R.id.recyclerView);

        storeDataInArrays();

        AdapterProject adapterProject = new AdapterProject(id, name, data);
        recyclerView.setAdapter(adapterProject);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);


        adapterProject.setListener(new AdapterProject.Listener() {
            @Override
            public void onClick(int position, String name, String data, int id) {
                inProject(position, name,data,id);
                MainActivity mainActivity = new MainActivity();
                mainActivity.setProjectNumer(id);
            }
        });

        return layout;
    }


    void storeDataInArrays() {
        Cursor cursor = myDB.readProject();

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                if (cursor.getInt(4) == 0) {
                    id.add(cursor.getInt(0));
                    name.add(cursor.getString(1));
                    data.add(cursor.getString(2));
                }
            }
        }
        cursor.close();
    }

    public void onClickButton(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }

    public void inProject(int position, String name, String data, int id) {
        WarehouseFragment warehouseFragment = new WarehouseFragment();

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("date", data);
        bundle.putInt("id", id);
        warehouseFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, warehouseFragment, "visible_fragment")
                .addToBackStack(null)
                .commit();

    }
}
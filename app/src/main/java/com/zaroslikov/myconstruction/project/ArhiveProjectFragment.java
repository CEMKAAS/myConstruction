package com.zaroslikov.myconstruction.project;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.zaroslikov.myconstruction.AddProjectFragment;
import com.zaroslikov.myconstruction.MainActivity;
import com.zaroslikov.myconstruction.R;
import com.zaroslikov.myconstruction.WarehouseFragment;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;

import java.util.ArrayList;

public class ArhiveProjectFragment extends Fragment {
    private MyDatabaseHelper myDB;
    private RecyclerView recyclerView;
    private ArrayList<String> name, data;
    private ArrayList<Integer> id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_arhive_project, container, false);

        myDB = new MyDatabaseHelper(getActivity());

        id = new ArrayList<>();
        name = new ArrayList<>();
        data = new ArrayList<>();

        recyclerView = layout.findViewById(R.id.recyclerView);

        storeDataInArrays();

        AdapterProject adapterProject = new AdapterProject(id, name, data,false);
        recyclerView.setAdapter(adapterProject);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        
        adapterProject.setListener(new AdapterProject.Listener() {
            @Override
            public void onClick(int position, String name, String data, int id) {
                inProject(position, name,data);
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
                if (cursor.getInt(5) == 1) {
                    id.add(cursor.getInt(0));
                    name.add(cursor.getString(1));
                    data.add(cursor.getString(2) + " - " + cursor.getString(3));

                }
            }
        }
        cursor.close();
    }

    public void inProject(int position, String name, String data) {
        ArhiveWarehouseFragment warehouseFragment = new ArhiveWarehouseFragment();

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("date", data);
        warehouseFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, warehouseFragment, "visible_fragment")
                .addToBackStack(null)
                .commit();

    }
}
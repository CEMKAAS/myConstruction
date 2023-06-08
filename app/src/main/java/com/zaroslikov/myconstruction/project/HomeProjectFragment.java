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
import com.zaroslikov.myconstruction.R;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;

import java.util.ArrayList;

public class HomeProjectFragment extends Fragment implements View.OnClickListener {

    private MyDatabaseHelper myDB;
    private RecyclerView recyclerView;
    private ArrayList<String> id, name, data, status;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_home_project, container, false);

        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.setOnClickListener(this);
        fab.show();
        fab.setText("Добавить");
        fab.setIconResource(R.drawable.baseline_add_24);
        fab.getIcon();

        myDB = new MyDatabaseHelper(getActivity());

        id = new ArrayList<>();
        name = new ArrayList<>();
        data = new ArrayList<>();
        status = new ArrayList<>();

        recyclerView = layout.findViewById(R.id.recyclerView);

        storeDataInArrays();

        AdapterProject adapterProject = new AdapterProject(id, name, data, status);
        recyclerView.setAdapter(adapterProject );
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);


        adapterProject.setListener(new AdapterProject().Listener() {
            @Override
            public void onClick(int position, String name, String type, String data, String id) {
                addChart(position, name, type, data, id);
            }
        });

        return layout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.extended_fab:
                onClickButton(v, new AddProjectFragment());
                break;

        }
    }

    void storeDataInArrays() {
        Cursor cursor = myDB.readAllDataIncubator();
        if (cursor.getCount() == 0) {
            cursor.close();
        } else {
            while (cursor.moveToNext()) {
                if (cursor.getString(8).equals("0")) {
                    id.add(cursor.getString(0));
                    name.add(cursor.getString(1));
                    type.add(cursor.getString(2));
                    data.add(cursor.getString(3));
                }
            }
            cursor.close();
        }
    }

    public void onClickButton(View view, Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }

    public void addChart(int position, String name, String type, String data, String id) {
        NowIncubatorFragment incubatorMenuFragment = new NowIncubatorFragment();

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("type", type);
        bundle.putString("data", data);
        bundle.putString("id", id);
        incubatorMenuFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, incubatorMenuFragment, "visible_fragment")
                .addToBackStack(null)
                .commit();

    }
}
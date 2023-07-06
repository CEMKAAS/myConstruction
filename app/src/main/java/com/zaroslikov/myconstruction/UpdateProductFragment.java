package com.zaroslikov.myconstruction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UpdateProductFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_update_product, container, false);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            nameProject = bundle.getString("name");
            dateProject = bundle.getString("date");
            idProject = bundle.getInt("id");
        }



        return layout;
    }
}
package com.zaroslikov.myconstruction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;


public class AddFragment extends Fragment {
    private MyDatabaseHelper myDB;
    private TextInputLayout add_edit, date;
    private AutoCompleteTextView productName, suffixSpiner, category;
    private ArrayAdapter<String> arrayAdapterProduct;
    private List<String> productList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_add, container, false);

        myDB = new MyDatabaseHelper(getActivity());
        productList = new ArrayList<>();

        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.show();
        fab.setText("Журнал");
        fab.setIconResource(R.drawable.baseline_book_24);
        fab.getIcon();

        productName = layout.findViewById(R.id.productName_editText);
        add_edit = layout.findViewById(R.id.add_edit);
        suffixSpiner = layout.findViewById(R.id.suffixSpiner);
        category = layout.findViewById(R.id.category_edit);
        date = layout.findViewById(R.id.date);
        
        return layout;
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            arrayAdapterProduct = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, productList);
            productName.setAdapter(arrayAdapterProduct);
        }
    }

}
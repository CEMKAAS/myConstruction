package com.zaroslikov.myconstruction;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;
import com.zaroslikov.myconstruction.project.MenuProjectFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class AddProjectFragment extends Fragment {

    private TextInputLayout nameProject, dateProject;
    private MaterialDatePicker datePicker;
    static final int GALLERY_REQUEST = 1;

    private MyDatabaseHelper myDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myDB = new MyDatabaseHelper(getActivity());

        View layout = inflater.inflate(R.layout.fragment_add_project, container, false);
        //убириаем фаб кнопку
        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) getActivity().findViewById(R.id.extended_fab);
        fab.setVisibility(View.GONE);

        MaterialToolbar appBar = getActivity().findViewById(R.id.topAppBar);
        appBar.setTitle("Добавить проект");
        appBar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        appBar.getMenu().findItem(R.id.filler).setVisible(false);
        appBar.getMenu().findItem(R.id.deleteAll).setVisible(false);
        appBar.getMenu().findItem(R.id.moreAll).setVisible(true);
        appBar.getMenu().findItem(R.id.magazine).setVisible(false);
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
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });



        nameProject = layout.findViewById(R.id.name_project);
        dateProject = layout.findViewById(R.id.date);

        // Настройка календаря

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        dateProject.getEditText().setText(calendar.get(Calendar.DAY_OF_MONTH)+ "." +(calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR));

        CalendarConstraints constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build();

        datePicker = MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraintsBuilder)
                .setTitleText("Выберите дату").setSelection(MaterialDatePicker.todayInUtcMilliseconds()) //Todo выбирать дату из EditText
                .build();

        dateProject.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getActivity().getSupportFragmentManager(), "wer");
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        calendar.setTimeInMillis((Long) selection);
                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                        String formattedDate = format.format(calendar.getTime());
                        dateProject.getEditText().setText(formattedDate);

                    }
                });
            }
        });
        Button add = layout.findViewById(R.id.begin);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProject();
            }
        });
        return layout;
    }


    public void addProject() {

        String name = nameProject.getEditText().getText().toString();
        String date = dateProject.getEditText().getText().toString();

        Cursor cursor = myDB.readProject();
        cursor.getCount();

        if(name.equals("") || date.equals("")){
            if(name.equals("")){
                nameProject.setError("Укажите имя проекта!");
                nameProject.getError();
            }
            if(date.equals("")){
                dateProject.setError("Укажите дату!");
                dateProject.getError();}

        }else {
            myDB.insertToDbProject(name, date, 0);
            replaceFragment(new MenuProjectFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }

}
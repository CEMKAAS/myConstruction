package com.zaroslikov.myconstruction.project;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.zaroslikov.myconstruction.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AdapterProject  extends RecyclerView.Adapter<HomeAdapterIncubator.ViewHolder> {
    private ArrayList id, name, type, data, arhive;
    private long diff;
    private Listener listener;

    public static interface Listener {
        public void onClick(int position, String name, String type, String data, String id);
    }

    public HomeAdapterIncubator(ArrayList idIncubator, ArrayList nameIncubator, ArrayList typeIncubator, ArrayList dataIncubator, ArrayList arhiveIncubator) {
        this.id = idIncubator;
        this.name = nameIncubator;
        this.type = typeIncubator;
        this.data = dataIncubator;
        this.arhive = arhiveIncubator;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;

        public ViewHolder(MaterialCardView v) {
            super(v);
            cardView = v;
        }
    }

    @NonNull
    @Override
    public HomeAdapterIncubator.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialCardView cv = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_captioned_image, parent, false);
        return new ViewHolder(cv);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull HomeAdapterIncubator.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        MaterialCardView cardView = holder.cardView;

        ImageView imageView = (ImageView) cardView.findViewById(R.id.info_image);
        //Установка картинки в карту
        Drawable drawable = null;
        if ("Курицы".equals(type.get(position))) {
            drawable = cardView.getResources().getDrawable(R.drawable.chicken);
        } else if ("Гуси".equals(type.get(position))) {
            drawable = cardView.getResources().getDrawable(R.drawable.external_goose_birds_icongeek26_outline_icongeek26);
        } else if ("Перепела".equals(type.get(position))) {
            drawable = cardView.getResources().getDrawable(R.drawable.quail);
        } else if ("Утки".equals(type.get(position))) {
            drawable = cardView.getResources().getDrawable(R.drawable.duck);
        } else if ("Индюки".equals(type.get(position))) {
            drawable = cardView.getResources().getDrawable(R.drawable.turkeycock);
        }

        imageView.setImageDrawable(drawable);
        imageView.setContentDescription("22");

        Calendar calendar = Calendar.getInstance();
        String dateBefore22 = String.valueOf(data.get(position));
        String dateBefore222 = calendar.get(Calendar.DAY_OF_MONTH) + 1 + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);

        SimpleDateFormat myFormat = new SimpleDateFormat("dd.MM.yyyy");

        try {
            Date date1 = myFormat.parse(dateBefore22);
            Date date2 = myFormat.parse(dateBefore222);
            diff = date2.getTime() - date1.getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        TextView textView = (TextView) cardView.findViewById(R.id.name);
        textView.setText(String.valueOf(name.get(position)));// имя инкубатора
        TextView textView1 = (TextView) cardView.findViewById(R.id.dayEnd);
        textView1.setText("Идет " + String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)) + " день ");//Какой день
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position, String.valueOf(name.get(position)), String.valueOf(type.get(position)), String.valueOf(data.get(position)), String.valueOf(id.get(position)));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return id.size();
    }

}

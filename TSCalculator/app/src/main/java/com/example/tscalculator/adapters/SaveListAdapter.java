package com.example.tscalculator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tscalculator.R;
import com.example.tscalculator.SaveActivity;
import com.example.tscalculator.models.Speaker;

import java.util.List;

public class SaveListAdapter extends ArrayAdapter<Speaker> {

    private final LayoutInflater layoutInflater;

    public SaveListAdapter(@NonNull Context context, @NonNull List<Speaker> objects) {
        super(context, -1, objects);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.save_list_item, parent,
                    false);
        }

        final Speaker speaker = getItem (position);

        final TextView fieldName = convertView.findViewById(R.id.nameText);
        final EditText fieldSumFs = convertView.findViewById(R.id.fieldSumFs);
        final EditText fieldSumQms = convertView.findViewById(R.id.fieldSumQms);
        final EditText fieldSumQes = convertView.findViewById(R.id.fieldSumQes);
        final EditText fieldSumQts = convertView.findViewById(R.id.fieldSumQts);
        final EditText fieldSumVas = convertView.findViewById(R.id.fieldSumVas);
        final EditText fieldSumEbp = convertView.findViewById(R.id.fieldSumEbp);
        final Button deleteButton = convertView.findViewById(R.id.deleteButton);

        deleteButton.setTag(speaker.getName());
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SaveActivity) getContext()).deleteSave(v.getTag().toString());
            }
        });

        fieldName.setText(speaker.getName());
        fieldSumFs.setText(String.valueOf(speaker.getFs()));
        fieldSumQms.setText(String.format("%.2f", speaker.getQms()));
        fieldSumQes.setText(String.format("%.2f", speaker.getQes()));
        fieldSumQts.setText(String.format("%.2f", speaker.getQts()));
        fieldSumVas.setText(String.format("%.2f", speaker.getVas()));
        fieldSumEbp.setText(String.valueOf((int) speaker.getEbp()));

        return convertView;
    }

}

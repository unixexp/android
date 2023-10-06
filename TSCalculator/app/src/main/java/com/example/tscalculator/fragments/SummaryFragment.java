package com.example.tscalculator.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.tscalculator.MainActivity;
import com.example.tscalculator.R;
import com.example.tscalculator.SaveActivity;
import com.example.tscalculator.components.Calculator;
import com.example.tscalculator.components.Storage;
import com.example.tscalculator.widgets.VolumeType;

import org.json.JSONException;
import org.json.JSONObject;

public class SummaryFragment extends Fragment implements OnFragmentChangeListener {
    private Activity activity;
    private boolean selected = false;
    private boolean initialized = false;

    private TextView textSumFs;
    private TextView textSumQms;
    private TextView textSumQes;
    private TextView textSumQts;
    private TextView textSumVas;
    private TextView textSumEbp;

    private EditText fieldSumFs;
    private EditText fieldSumQms;
    private EditText fieldSumQes;
    private EditText fieldSumQts;
    private EditText fieldSumVas;
    private EditText fieldSumEbp;

    private VolumeType volumeType;
    private Button saveButton;
    private Storage storage;

    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance(Activity activity) {
        SummaryFragment fragment = new SummaryFragment();
        fragment.setActivity(activity);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.summary_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textSumFs = getView().findViewById(R.id.textSumFs);
        textSumQms = getView().findViewById(R.id.textSumQms);
        textSumQes = getView().findViewById(R.id.textSumQes);
        textSumQts = getView().findViewById(R.id.textSumQts);
        textSumVas = getView().findViewById(R.id.textVas);
        textSumEbp = getView().findViewById(R.id.textEbp);

        fieldSumFs = getView().findViewById(R.id.fieldSumFs);
        fieldSumQms = getView().findViewById(R.id.fieldSumQms);
        fieldSumQes = getView().findViewById(R.id.fieldSumQes);
        fieldSumQts = getView().findViewById(R.id.fieldSumQts);
        fieldSumVas = getView().findViewById(R.id.fieldSumVas);
        fieldSumEbp = getView().findViewById(R.id.fieldSumEbp);

        volumeType = getView().findViewById(R.id.volume_type_image);
        saveButton = getView().findViewById(R.id.savesButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SaveActivity.class);
                startActivity(intent);
            }
        });

        initialized = true;
        ((MainActivity) activity).onFragmentCreated(this);
    }

    private void updateSumFs() {
        if (Calculator.driver.getFs() > 0) {
            textSumFs.setTextColor(activity.getResources()
                    .getColor(R.color.colorNineV1, null));
            fieldSumFs.setText(String.valueOf(Calculator.driver.getFs()));
        } else {
            textSumFs.setTextColor(activity.getResources()
                    .getColor(R.color.colorOneV1, null));
            fieldSumFs.setText("-");
        }
    }

    private void updateSumQms() {
        if (Calculator.driver.getQms() > 0 &&
                Calculator.driver.getQms() != Double.POSITIVE_INFINITY) {
            textSumQms.setTextColor(activity.getResources()
                    .getColor(R.color.colorNineV1, null));
            fieldSumQms.setText(String.format("%.2f", Calculator.driver.getQms()));
        } else {
            textSumQms.setTextColor(activity.getResources()
                    .getColor(R.color.colorOneV1, null));
            fieldSumQms.setText("-");
        }
    }

    private void updateSumQes() {
        if (Calculator.driver.getQes() > 0
                && Calculator.driver.getQes() != Double.POSITIVE_INFINITY) {
            textSumQes.setTextColor(activity.getResources()
                    .getColor(R.color.colorNineV1, null));
            fieldSumQes.setText(String.format("%.2f", Calculator.driver.getQes()));
        } else {
            textSumQes.setTextColor(activity.getResources()
                    .getColor(R.color.colorOneV1, null));
            fieldSumQes.setText("-");
        }
    }

    private void updateSumQts() {
        if (Calculator.driver.getQts() > 0 &&
                Calculator.driver.getQts() != Double.POSITIVE_INFINITY) {
            textSumQts.setTextColor(activity.getResources()
                    .getColor(R.color.colorNineV1, null));
            fieldSumQts.setText(String.format("%.2f", Calculator.driver.getQts()));
        } else {
            textSumQts.setTextColor(activity.getResources()
                    .getColor(R.color.colorOneV1, null));
            fieldSumQts.setText("-");
        }
    }

    private void updateSumVas() {
        if (Calculator.driver.getVas() > 0 &&
                Calculator.driver.getVas() != Double.POSITIVE_INFINITY) {
            textSumVas.setTextColor(activity.getResources()
                    .getColor(R.color.colorNineV1, null));
            fieldSumVas.setText(String.format("%.2f", Calculator.driver.getVas()));
        } else {
            textSumVas.setTextColor(activity.getResources()
                    .getColor(R.color.colorOneV1, null));
            fieldSumVas.setText("-");
        }
    }

    private void updateSumEbp() {
        if (Calculator.driver.getEbp() > 0 && Calculator.driver.getEbp() < 1000 &&
                Calculator.driver.getEbp() != Double.POSITIVE_INFINITY) {
            textSumEbp.setTextColor(activity.getResources()
                    .getColor(R.color.colorNineV1, null));
            fieldSumEbp.setText(String.valueOf((int) Calculator.driver.getEbp()));
        } else {
            textSumEbp.setTextColor(activity.getResources()
                    .getColor(R.color.colorOneV1, null));
            fieldSumEbp.setText("-");
        }
    }

    private void updateSumOptimalVolumeType() {
        volumeType.setVolumeType(Calculator.driver.getOptimalVolumeType());
    }

    @Override
    public void onFragmentChange(Fragment fragment) {
        if (fragment == null || fragment != this) {
            selected = false;
        } else {
            selected = true;
            if (initialized) {
                updateSumFs();
                updateSumQms();
                updateSumQes();
                updateSumQts();
                updateSumVas();
                updateSumEbp();
                updateSumOptimalVolumeType();
            }
        }
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

}

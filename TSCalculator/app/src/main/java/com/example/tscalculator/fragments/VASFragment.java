package com.example.tscalculator.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.tscalculator.MainActivity;
import com.example.tscalculator.R;
import com.example.tscalculator.components.Calculator;
import com.example.tscalculator.widgets.Amplifier;
import com.example.tscalculator.widgets.FrequencyGenerator;
import com.example.tscalculator.widgets.Graph;
import com.example.tscalculator.widgets.Multimeter;

import static com.example.tscalculator.components.StaticData.HERZ_RANGE;
import static com.example.tscalculator.components.StaticData.VOLT_RANGE;

public class VASFragment extends Fragment implements OnFragmentChangeListener {
    private Activity activity;
    private boolean selected = false;
    private boolean initialized = false;

    private Amplifier amp;
    private FrequencyGenerator generator;
    private Multimeter multimeter;

    private TextView textDia;
    private TextView textMa;
    private TextView textFsa;
    private EditText fieldDia;
    private EditText fieldMa;
    private EditText fieldFsa;

    public VASFragment() {
        // Required empty public constructor
    }

    public static VASFragment newInstance(Activity activity) {
        VASFragment fragment = new VASFragment();
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
        return inflater.inflate(R.layout.vas_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        amp = getView().findViewById(R.id.amplifier);
        generator = getView().findViewById(R.id.generator);
        multimeter = getView().findViewById(R.id.multimeter);

        amp.setVolume(30);
        generator.setFrequency(24);
        generator.setFrequencyMAX(500);
        multimeter.setValue(0.382f);

        textDia = getView().findViewById(R.id.textDia);
        textMa = getView().findViewById(R.id.textMa);
        textFsa = getView().findViewById(R.id.textFsa);
        fieldDia = getView().findViewById(R.id.fieldDia);
        fieldMa = getView().findViewById(R.id.fieldMa);
        fieldFsa = getView().findViewById(R.id.fieldFsa);

        fieldDia.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onUpdateFieldMa();
                onUpdateFieldFsa();
                return false;
            }
        });

        fieldMa.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onUpdateFieldDia();
                onUpdateFieldFsa();
                return false;
            }
        });

        fieldFsa.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onUpdateFieldDia();
                onUpdateFieldMa();
                return false;
            }
        });

        fieldDia.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) {
                    onUpdateFieldDia();
                    onUpdateFieldMa();
                    onUpdateFieldFsa();
                }
                return false;
            }
        });

        fieldMa.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) {
                    onUpdateFieldMa();
                    onUpdateFieldDia();
                    onUpdateFieldFsa();
                }
                return false;
            }
        });

        fieldFsa.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) {
                    onUpdateFieldFsa();
                    onUpdateFieldMa();
                    onUpdateFieldDia();
                }
                return false;
            }
        });

        initialized = true;
        ((MainActivity) activity).onFragmentCreated(this);
    }

    private void onUpdateFieldDia() {
        try {
            String value = fieldDia.getText().toString();
            if (value == "" || Double.valueOf(value) == 0) {
                textDia.setTextColor(activity.getResources()
                        .getColor(R.color.colorOneV1, null));
            } else {
                textDia.setTextColor(activity.getResources()
                        .getColor(R.color.colorNineV1, null));
            }

            Calculator.driver.setDiameter(Double.valueOf(value));

        } catch (NumberFormatException e) {
            fieldDia.setText("0");
            textDia.setTextColor(activity.getResources()
                    .getColor(R.color.colorOneV1, null));
        }
    }

    private void onUpdateFieldMa() {
        try {
            String value = fieldMa.getText().toString();
            if (value == "" || Integer.valueOf(value) == 0) {
                textMa.setTextColor(activity.getResources()
                        .getColor(R.color.colorOneV1, null));
            } else {
                textMa.setTextColor(activity.getResources()
                        .getColor(R.color.colorNineV1, null));
            }

            Calculator.vasCalculator.setMa(Integer.valueOf(value));

        } catch (NumberFormatException e) {
            fieldMa.setText("0");
            textMa.setTextColor(activity.getResources()
                    .getColor(R.color.colorOneV1, null));
        }
    }

    private void onUpdateFieldFsa() {
        try {
            String value = fieldFsa.getText().toString();
            if (value == "" || Integer.valueOf(value) == 0) {
                textFsa.setTextColor(activity.getResources()
                        .getColor(R.color.colorOneV1, null));
            } else {
                textFsa.setTextColor(activity.getResources()
                        .getColor(R.color.colorNineV1, null));
            }

            Calculator.vasCalculator.setFsa(Integer.valueOf(value));

        } catch (NumberFormatException e) {
            fieldFsa.setText("0");
            textFsa.setTextColor(activity.getResources()
                    .getColor(R.color.colorOneV1, null));
        }
    }

    @Override
    public void onFragmentChange(Fragment fragment) {
        if (fragment == null || fragment != this) {
            onUpdateFieldDia();
            onUpdateFieldMa();
            onUpdateFieldFsa();
            selected = false;
        } else {
            onUpdateFieldDia();
            onUpdateFieldMa();
            onUpdateFieldFsa();
            selected = true;
        }
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

}

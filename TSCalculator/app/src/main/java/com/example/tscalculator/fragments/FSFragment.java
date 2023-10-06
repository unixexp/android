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

public class FSFragment extends Fragment implements OnFragmentChangeListener {
    private Activity activity;
    private Thread thread;
    private boolean selected = false;

    private boolean initialized = false;
    private Amplifier amp;
    private FrequencyGenerator generator;
    private Multimeter multimeter;
    private Graph graph;
    private boolean graphAnimationDone = true;

    private TextView textFs;
    private TextView textUs;
    private EditText fieldFs;
    private EditText fieldUs;

    public FSFragment() {
        // Required empty public constructor
    }

    public static FSFragment newInstance(Activity activity) {
        FSFragment fragment = new FSFragment();
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
        return inflater.inflate(R.layout.fs_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        amp = getView().findViewById(R.id.amplifier);
        generator = getView().findViewById(R.id.generator);
        multimeter = getView().findViewById(R.id.multimeter);
        graph = getView().findViewById(R.id.graph);

        amp.setVolume(30);

        generator.setFrequency(0);
        generator.setFrequencyMAX(500);

        graph.setGraphBackgroundColor(R.color.colorSevenV1);
        graph.setGraphOuterStrokeColor(R.color.colorSevenV1);
        graph.setGraphAxisColor(R.color.colorTwoV1);
        graph.setGraphGridColor(R.color.colorFiveV1);
        graph.setGraphLegendTextColor(R.color.colorTwoV1);
        graph.setGraphColor(R.color.colorTwoV1);
        graph.setGraphMarkPointColor(R.color.colorEightV1);
        graph.setGraphMarkPointLabelColor(R.color.colorTwoV1);
        graph.setGraphMarkPointLabelBackgroundColor(R.color.colorSixV1);
        graph.setGraphLegendYLabelColor(R.color.colorOneV1);
        graph.setGraphLegendXLabelColor(R.color.colorNineV1);

        graph.setLegendXValueToInt(true);
        graph.setGraphLineWidth(1.2f);
        graph.setIntermediates(10);
        graph.setGraphGridXDensityPercent(10);
        graph.setGraphGridYGraduationCount(7);
        graph.setLegendXLabel("Hz");
        graph.setLegendYLabel("V~");
        graph.setGraphMarkPointWidth(7f);
        graph.setLegendYSizePercent(12);
        graph.setDataX(HERZ_RANGE);
        graph.setDataY(VOLT_RANGE);
        graph.setUpperYLimit(0.7f);
        graph.setAnimationInterval(0.03f);
        graph.clearMarkPointsY();
        graph.addMarkPointY("Fs", 0.487f);   // Fs
        // graph.addMarkPointY("Umin", 0.092f); // Umin
        // graph.addMarkPointY("F1", 0.21f);    // F1
        // graph.addMarkPointY("F2", 0.216f);   // F2
        graph.setGraphAnimatorListener(new Graph.GraphAnimatorListener() {
            @Override
            public void onAnimationUpdate(int value) {
                generator.setFrequency(value);
                multimeter.setValue(VOLT_RANGE[value]);
            }

            @Override
            public void onAnimationStart(int value) {
            }

            @Override
            public void onAnimationEnd(int value) {
                generator.setFrequency(value);
                multimeter.setValue(VOLT_RANGE[value]);
                graphAnimationDone = true;
            }
        });

        textFs = getView().findViewById(R.id.textFs);
        textUs = getView().findViewById(R.id.textUs);
        fieldFs = getView().findViewById(R.id.fieldFs);
        fieldUs = getView().findViewById(R.id.fieldUs);

        fieldFs.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onUpdateFieldUs();
                return false;
            }
        });

        fieldUs.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onUpdateFieldFs();
                return false;
            }
        });

        fieldFs.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 67) {
                    onUpdateFieldFs();
                    onUpdateFieldUs();
                }

                return false;
            }
        });

        fieldUs.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 67) {
                    onUpdateFieldUs();
                    onUpdateFieldFs();
                }

                return false;
            }
        });

        initialized = true;
        ((MainActivity) activity).onFragmentCreated(this);
    }

    private void onUpdateFieldFs() {
        try {
            String value = fieldFs.getText().toString();
            if (value == "" || Integer.valueOf(value) == 0) {
                textFs.setTextColor(activity.getResources()
                        .getColor(R.color.colorOneV1, null));
            } else {
                textFs.setTextColor(activity.getResources()
                        .getColor(R.color.colorNineV1, null));
            }

            Calculator.driver.setFs(Integer.valueOf(value));

        } catch (NumberFormatException e) {
            fieldFs.setText("0");
            textFs.setTextColor(activity.getResources()
                    .getColor(R.color.colorOneV1, null));
        }
    }

    private void onUpdateFieldUs() {
        try {
            String value = fieldUs.getText().toString();
            if (value == "" || Float.valueOf(value) == 0) {
                textUs.setTextColor(activity.getResources()
                        .getColor(R.color.colorOneV1, null));
            } else {
                textUs.setTextColor(activity.getResources()
                        .getColor(R.color.colorNineV1, null));
            }

            Calculator.qtsCalculator.setUs(Float.valueOf(value));

        } catch (NumberFormatException e) {
            fieldUs.setText("0");
            textUs.setTextColor(activity.getResources()
                    .getColor(R.color.colorOneV1, null));
        }
    }

    @Override
    public void onFragmentChange(Fragment fragment) {
        if (fragment == null || fragment != this) {
            if (initialized) {
                onUpdateFieldUs();
                onUpdateFieldFs();
                selected = false;
                graph.stopAnimation();
            }
        } else {
            onUpdateFieldFs();
            onUpdateFieldUs();
            graph.stopAnimation();
            selected = true;
            run();
        }
    }

    private void run() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(selected) {
                        if (initialized) {
                            if (graph != null && graphAnimationDone) {
                                Thread.sleep(3000);
                                graph.showAnimation();
                                graphAnimationDone = false;
                            }
                        }
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) { }
            }
        });
        thread.start();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

}

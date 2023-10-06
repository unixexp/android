package com.example.tscalculator.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tscalculator.MainActivity;
import com.example.tscalculator.R;
import com.example.tscalculator.components.FixTimeStepAnimator;
import com.example.tscalculator.components.StaticData;
import com.example.tscalculator.widgets.Amplifier;
import com.example.tscalculator.widgets.FrequencyGenerator;
import com.example.tscalculator.widgets.Multimeter;

public class OverviewFragment extends Fragment implements OnFragmentChangeListener {
    private Activity activity;
    private Thread thread;
    private boolean selected = false;
    private boolean initialized = false;
    private boolean firstStart = false;

    private Button aboutButton;
    private Amplifier amp;
    private FrequencyGenerator generator;
    private Multimeter multimeter;

    private FixTimeStepAnimator generatorAnimator;
    private FixTimeStepAnimator amplifierAnimator;
    private boolean amplifierAnimationDone = true;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance(Activity activity) {
        OverviewFragment fragment = new OverviewFragment();
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
        return inflater.inflate(R.layout.overview_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        aboutButton = getView().findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activity).aboutScreen.setVisibility(View.VISIBLE);
            }
        });

        amp = getView().findViewById(R.id.amplifier);
        generator = getView().findViewById(R.id.generator);
        multimeter = getView().findViewById(R.id.multimeter);

        generator.setFrequency(0);
        generator.setFrequencyMAX(1000);

        generatorAnimator = new FixTimeStepAnimator();
        generatorAnimator.setInterval(0.005f);
        generatorAnimator.setRange(0, 500);
        generatorAnimator.setFixTimeStepAnimatorListener(
                new FixTimeStepAnimator.FixTimeStepAnimatorListener() {
                    @Override
                    public void onAnimationUpdate(int value) { generator.setFrequency(value); }

                    @Override
                    public void onAnimationStart(int value) {
                        generator.setFrequency(0);
                        amp.setVolume(0);
                        multimeter.setValue(0);
                        amplifierAnimationDone = false;
                    }

                    @Override
                    public void onAnimationEnd(int value) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // Wait before start amplifier animation
                                    Thread.sleep(1000);
                                    amplifierAnimator.start();
                                } catch (InterruptedException e) {}
                            }
                        }).start();
                    }
                }
        );

        amplifierAnimator = new FixTimeStepAnimator();
        amplifierAnimator.setInterval(0.05f);
        amplifierAnimator.setRange(0, 30);
        amplifierAnimator.setFixTimeStepAnimatorListener(
                new FixTimeStepAnimator.FixTimeStepAnimatorListener() {
                    @Override
                    public void onAnimationUpdate(int value) {
                        amp.setVolume(value);
                        multimeter.setValue(StaticData.VOLT_RANGE_OVERVIEW[value]);
                    }

                    @Override
                    public void onAnimationStart(int value) {}

                    @Override
                    public void onAnimationEnd(int value) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // Wait before restart animation
                                    Thread.sleep(4000);
                                    amplifierAnimationDone = true;
                                } catch (InterruptedException e) {}
                            }
                        }).start();
                    }
                });

        multimeter.setValueMAX(10);

        initialized = true;
        ((MainActivity) activity).onFragmentCreated(this);

        if (MainActivity.firstStart) {
            MainActivity.firstStart = false;
            onFragmentChange(this);
        }

    }

    @Override
    public void onFragmentChange(Fragment fragment) {
        if (fragment == null || fragment != this) {
            if (initialized) {
                selected = false;
                amplifierAnimator.stop();
                generatorAnimator.stop();
                amp.setVolume(30);
                generator.setFrequency(500);
                multimeter.setValue(
                        StaticData.VOLT_RANGE_OVERVIEW[StaticData.VOLT_RANGE_OVERVIEW.length-1]);
                amplifierAnimationDone = true;
            }
        } else {
            selected = true;
            firstStart = true;
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
                            if (firstStart) {
                                Thread.sleep(3000);
                                firstStart = false;
                            }

                            if (generatorAnimator != null && amplifierAnimationDone) {
                                generatorAnimator.start();
                            }
                            Thread.sleep(100);
                        }
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

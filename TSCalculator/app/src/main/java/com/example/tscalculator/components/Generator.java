package com.example.tscalculator.components;

import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.tscalculator.MainActivity;
import com.example.tscalculator.R;
import com.example.tscalculator.widgets.AnimatedToggleButton;

public class Generator {

    private MainActivity activity;
    private ConstraintLayout layout;
    private TextView display;
    private ProgressBar seekBar;
    private AnimatedToggleButton playButton;
    private ImageButton frequencyDownButton;
    private ImageButton frequencyUpButton;

    public Generator(MainActivity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        layout = activity.findViewById(R.id.frequency_generator);
        display = activity.findViewById(R.id.frequency_display);
        seekBar = activity.findViewById(R.id.frequency_seek);
        playButton = activity.findViewById(R.id.button_play_tone);
        frequencyDownButton = activity.findViewById(R.id.button_frequency_down);
        frequencyUpButton = activity.findViewById(R.id.button_frequency_up);

        layout.setOnTouchListener(activity);
        display.setOnTouchListener(activity);
        seekBar.setOnTouchListener(activity);
        playButton.setOnTouchListener(activity);
        frequencyDownButton.setOnTouchListener(activity);
        frequencyUpButton.setOnTouchListener(activity);
    }

}

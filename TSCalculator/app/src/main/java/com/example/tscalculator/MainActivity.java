package com.example.tscalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.tscalculator.adapters.MainPagerAdapter;
import com.example.tscalculator.components.AudioFrequencyPlayer;
import com.example.tscalculator.components.Calculator;
import com.example.tscalculator.components.Generator;
import com.example.tscalculator.components.StaticData;
import com.example.tscalculator.components.Storage;
import com.example.tscalculator.fragments.OnFragmentChangeListener;
import com.example.tscalculator.widgets.AnimatedToggleButton;
import com.google.android.material.tabs.TabLayout;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    final private int LOAD_STEPS = 6;

    private int loadStepCount = 0;

    private FrameLayout loadingScreen;
    private FrameLayout workScreen;

    private MainPagerAdapter pagerAdapter;
    private ViewPager pager;
    private PagerListener pagerListener;
    private TabLayout tabLayout;
    private Generator generator;

    private int minFrequency = StaticData.FREQ_RANGE_1[0];
    private int maxFrequency = StaticData.FREQ_RANGE_1[1];
    private int selectedFragmentPosition = 0;

    public static boolean firstStart = true; // Used for start animation on first fragment
    private Storage storage;

    public FrameLayout aboutScreen;

    public void onFragmentCreated(Fragment fragment) {
        loadStepCount++;

        if (loadStepCount == LOAD_STEPS) {
            loadingScreen.setVisibility(View.INVISIBLE);
            workScreen.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hideSystemUI();
        return false;
    }

    private class PagerListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            selectedFragmentPosition = position;
            final Fragment selectedFragment = pagerAdapter.getFragment(position);
            for (Fragment fragment : pagerAdapter.getFragments())
                if (fragment != null)
                    ((OnFragmentChangeListener) fragment).onFragmentChange(selectedFragment);
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Force full screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        aboutScreen = findViewById(R.id.aboutScreen);
        aboutScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutScreen.setVisibility(View.INVISIBLE);
            }
        });

        loadingScreen = findViewById(R.id.loadingScreen);
        workScreen = findViewById(R.id.workScreen);

        // Prepare audio frequency player
        minFrequency = StaticData.FREQ_RANGE_1[0];
        maxFrequency = StaticData.FREQ_RANGE_1[1];
        final SeekBar frequencySeek = findViewById(R.id.frequency_seek);
        final TextView frequencyDisplay = findViewById(R.id.frequency_display);
        final AnimatedToggleButton lt500hz = findViewById(R.id.lt500hz);
        final AnimatedToggleButton bt_500_2000hz = findViewById(R.id.bt_500_2000hz);
        final AnimatedToggleButton gt_2000hz = findViewById(R.id.gt_2000hz);
        final AudioFrequencyPlayer audioFrequencyPlayer = new AudioFrequencyPlayer(maxFrequency);
        lt500hz.setChecked(true);
        frequencySeek.setMax(maxFrequency - minFrequency);
        frequencySeek.setProgress(maxFrequency);
        frequencyDisplay.setText(String.valueOf(maxFrequency));

        final AnimatedToggleButton buttonPlayTone = findViewById(R.id.button_play_tone);
        buttonPlayTone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    audioFrequencyPlayer.play();
                else
                    audioFrequencyPlayer.stop();
            }
        });

        lt500hz.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (((AnimatedToggleButton) v).isChecked()) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        bt_500_2000hz.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (((AnimatedToggleButton) v).isChecked()) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        gt_2000hz.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (((AnimatedToggleButton) v).isChecked()) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        lt500hz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (bt_500_2000hz.isChecked())
                        bt_500_2000hz.toggle();
                    if (gt_2000hz.isChecked())
                        gt_2000hz.toggle();

                    if (buttonPlayTone.isChecked()) {
                        audioFrequencyPlayer.stop();
                        buttonPlayTone.toggle();
                    }

                    minFrequency = StaticData.FREQ_RANGE_1[0];
                    maxFrequency = StaticData.FREQ_RANGE_1[1];
                    frequencySeek.setMax(maxFrequency - minFrequency);
                    frequencySeek.setProgress(maxFrequency);
                    frequencyDisplay.setText(String.valueOf(maxFrequency));
                    audioFrequencyPlayer.setFrequency(maxFrequency);
                }
            }
        });

        bt_500_2000hz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (lt500hz.isChecked())
                        lt500hz.toggle();
                    if (gt_2000hz.isChecked())
                        gt_2000hz.toggle();

                    if (buttonPlayTone.isChecked()) {
                        audioFrequencyPlayer.stop();
                        buttonPlayTone.toggle();
                    }

                    minFrequency = StaticData.FREQ_RANGE_2[0];
                    maxFrequency = StaticData.FREQ_RANGE_2[1];
                    frequencySeek.setMax(maxFrequency - minFrequency);
                    frequencySeek.setProgress(0);
                    frequencyDisplay.setText(String.valueOf(minFrequency));
                    audioFrequencyPlayer.setFrequency(minFrequency);
                }
            }
        });

        gt_2000hz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (lt500hz.isChecked())
                        lt500hz.toggle();
                    if (bt_500_2000hz.isChecked())
                        bt_500_2000hz.toggle();

                    if (buttonPlayTone.isChecked()) {
                        audioFrequencyPlayer.stop();
                        buttonPlayTone.toggle();
                    }

                    minFrequency = StaticData.FREQ_RANGE_3[0];
                    maxFrequency = StaticData.FREQ_RANGE_3[1];
                    frequencySeek.setMax(maxFrequency - minFrequency);
                    frequencySeek.setProgress(0);
                    frequencyDisplay.setText(String.valueOf(minFrequency));
                    audioFrequencyPlayer.setFrequency(minFrequency);
                }
            }
        });

        frequencySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int freq;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frequencyDisplay.setText(String.valueOf(progress + minFrequency));
                freq = progress + minFrequency;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                audioFrequencyPlayer.setFrequency(freq);
            }
        });

        final ImageButton buttonFrequencyDown = findViewById(R.id.button_frequency_down);
        buttonFrequencyDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int frequency = audioFrequencyPlayer.getFrequency();

                if (frequency == minFrequency)
                    return;

                frequency--;
                audioFrequencyPlayer.setFrequency(frequency);
                frequencySeek.setProgress(frequency - minFrequency);
            }
        });

        final ImageButton buttonFrequencyUp = findViewById(R.id.button_frequency_up);
        buttonFrequencyUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int frequency = audioFrequencyPlayer.getFrequency();

                if (frequency == maxFrequency)
                    return;

                frequency++;
                audioFrequencyPlayer.setFrequency(frequency);
                frequencySeek.setProgress(frequency - minFrequency);
            }
        });

        frequencySeek.setProgress(audioFrequencyPlayer.getFrequency());

        // Prepare calculator
        Calculator.init();

        // Prepare generator
        generator = new Generator(this);

        // Prepare pager and tab to fragments handling
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        pagerListener = new PagerListener();

        pager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabs);

        pager.addOnPageChangeListener(pagerListener);
        pager.setOnTouchListener(this);
        pager.setOffscreenPageLimit(5);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pager.setAdapter(pagerAdapter);

                            tabLayout.removeAllTabs();
                            tabLayout.setupWithViewPager(pager);
                            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                                View customTabView = getLayoutInflater().inflate(R.layout.tab_button_layout, null);
                                customTabView.findViewById(R.id.tab_button).
                                        setBackground(getDrawable(pagerAdapter.getTabIcon(i)));
                                tabLayout.getTabAt(i).setCustomView(customTabView);
                            }
                        }
                    });
                } catch (InterruptedException e) {}
            }
        }).start();
    }

    private void hideSystemUI() {
        // Hide navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // When activity goes to pause state, we inform all fragments
        // about no fragment selected this time. Therefore fragments will stop
        // them animation threads.
        for (Fragment fragment : pagerAdapter.getFragments())
            if (fragment != null)
                ((OnFragmentChangeListener) fragment).onFragmentChange(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        // When activity comes back from pause we also inform all fragments about
        // current selected fragment
        pagerListener.onPageSelected(selectedFragmentPosition);
    }

}
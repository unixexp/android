package com.example.tscalculator.components;

public class FixTimeStepAnimator implements Runnable {

    private float tickTime;
    private float interval;
    private int step;
    private int startValue;
    private int endValue;
    private int animatedValue;

    boolean running;
    private Thread thread;

    FixTimeStepAnimatorListener fixTimeStepAnimatorListener;

    public interface FixTimeStepAnimatorListener {
        void onAnimationUpdate(int value);
        void onAnimationStart(int value);
        void onAnimationEnd(int value);
    }

    public FixTimeStepAnimator() {
        tickTime = 0.0f;
        interval = 0.0f;
        step = 1;
        startValue = 0;
        endValue = 0;
        animatedValue = startValue;
        running = false;
    }

    public void setRange (int startValue, int endValue) {
        this.startValue = startValue - step;
        this.endValue = endValue;
        this.animatedValue = startValue;
    }

    public void setStep (int step) {
        this.step = step;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public void update (float deltaTime) {
        tickTime += deltaTime;

        while (tickTime > interval) {
            tickTime -= interval;

            if (startValue < endValue) {
                if (animatedValue >= endValue) {
                    animatedValue = endValue;
                    if (fixTimeStepAnimatorListener != null)
                        fixTimeStepAnimatorListener.onAnimationUpdate(animatedValue);

                    if (fixTimeStepAnimatorListener != null)
                        fixTimeStepAnimatorListener.onAnimationEnd(animatedValue);

                    tickTime = 0.0f;
                    running = false;
                } else {
                    animatedValue += step;

                    if (fixTimeStepAnimatorListener != null)
                        fixTimeStepAnimatorListener.onAnimationUpdate(animatedValue);
                }
            } else {
                if (animatedValue <= endValue) {
                    animatedValue = endValue;
                    if (fixTimeStepAnimatorListener != null)
                        fixTimeStepAnimatorListener.onAnimationUpdate(animatedValue);

                    if (fixTimeStepAnimatorListener != null)
                        fixTimeStepAnimatorListener.onAnimationEnd(animatedValue);

                    tickTime = 0.0f;
                    running = false;
                } else {
                    animatedValue -= step;

                    if (fixTimeStepAnimatorListener != null)
                        fixTimeStepAnimatorListener.onAnimationUpdate(animatedValue);
                }
            }
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        while (running) {
            float deltaTime = (System.nanoTime() - lastTime) / 1000000000.0f;
            lastTime = System.nanoTime();
            update(deltaTime);
        }
    }

    public void start() {
        if (running)
            return;

        if (fixTimeStepAnimatorListener != null)
            fixTimeStepAnimatorListener.onAnimationStart(animatedValue);

        animatedValue = startValue;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        running = false;
    }

    public void setFixTimeStepAnimatorListener(FixTimeStepAnimatorListener fixTimeStepAnimatorListener) {
        this.fixTimeStepAnimatorListener = fixTimeStepAnimatorListener;
    }
}

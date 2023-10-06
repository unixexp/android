package com.example.tscalculator.ts;

public class QtsCalculatorType1 implements QtsCalcTechnique {

    // -------------------- Измеряемые параметры ------------)-------------------
    // Напряжение на частоте Fs (Вольт)
    private double us = 0;

    // Минимальное напряжение на частоте выше Fs (Вольт)
    private double um = 0;

    // Корень квадратный из произведения us и um для нахождения частот f1 и f2
    private double u12 = 0;

    // Частота ниже Fs с напряжением равным U12 (Вольт)
    private int f1 = 0;

    // Частота выше Fs с напряжением равным U12 (Вольт)
    private int f2 = 0;



    public double getUs() {
        return us;
    }

    public void setUs(double us) {
        this.us = us;
        calcU12();
    }

    public double getUm() {
        return um;
    }

    public void setUm(double um) {
        this.um = um;
        calcU12();
    }

    public double getU12() {
        return u12;
    }

    public int getF1() {
        return f1;
    }

    public void setF1(int f1) {
        this.f1 = f1;
    }

    public int getF2() {
        return f2;
    }

    public void setF2(int f2) {
        this.f2 = f2;
    }

    private void calcU12() {
        this.u12 = Math.sqrt(us * um);
    }

    @Override
    public double calcQms(Driver driver) {
        try {
            return Math.sqrt (us / um) * ((double) driver.getFs()/(double) (f2 - f1));
        } catch (ArithmeticException e) {
            return 0;
        }
    }

    @Override
    public double calcQes(Driver driver) {
        final double mQms = calcQms(driver);
        if (mQms == 0)
            return 0;
        else
            return (calcQms(driver) * um) / (us - um);
    }

    @Override
    public double calcQts(Driver driver) {
        final double mQms = calcQms(driver);
        final double mQes = calcQes(driver);
        if (mQms == 0 || mQes == 0)
            return 0;
        else
            return (mQms * mQes) / (mQms + mQes);
    }

}

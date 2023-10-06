package com.example.tscalculator.components;

import com.example.tscalculator.ts.Driver;
import com.example.tscalculator.ts.QtsCalculatorType1;
import com.example.tscalculator.ts.VasCalculatorType1;

public class Calculator {

    public static QtsCalculatorType1 qtsCalculator;
    public static VasCalculatorType1 vasCalculator;
    public static Driver driver;

    public static void init() {
        qtsCalculator = new QtsCalculatorType1();
        vasCalculator = new VasCalculatorType1();
        driver = new Driver("Unknown", "");
        driver.setQtsCalcTechnique(qtsCalculator);
        driver.setVasCalcTechnique(vasCalculator);
    }

}

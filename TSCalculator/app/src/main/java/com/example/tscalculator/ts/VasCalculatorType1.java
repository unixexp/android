package com.example.tscalculator.ts;

public class VasCalculatorType1 implements VasCalcTechnique {

    // Плотность воздуха (г/мл)
    final double d = 0.001204;

    // Температура воздуха в помещении (градусы C)
    int tc = 24;

    // Скорость звука без учета температуры (м/с)
    final double C = 331.4;

    // Коефициент для расчета скорости звука взависимости от температуры
    final double Ck = 0.6;

    // Скорость звука с учетом температуры
    double c = 0;

    /*
     * Вес груза для измерения Vas (Граммы)
     * Рекоммендация: 10 граммов на каждый дюйм диаметра диффузора
     * Необходимо получить резонансную частоту динамика до 25% ниже, существующей
     */
    private int ma = 0;

    // Резонансная частота динамической головки с грузом (Гц)
    private int fsa = 0;

    public VasCalculatorType1() {
        super();
        calcCtc ();
    }

    public VasCalculatorType1(int tc) {
        super();
        this.tc = tc;
        calcCtc ();
    }

    public int getMa() {
        return ma;
    }

    public void setMa(int ma) {
        this.ma = ma;
    }

    public int getFsa() { return fsa; }

    public void setFsa(int fsa) {
        this.fsa = fsa;
    }

    private void calcCtc() {
        this.c = C + (Ck * this.tc);
    }

    @Override
    public double calcVas(Driver driver) {
        try {
            // Масса конуса
            final double m = ma / (Math.pow(((double) driver.getFs() / (double) fsa), 2) - 1);
            // Упругость подвижной системы
            final double cms = 1 / (Math.pow((2 * Math.PI * (double) driver.getFs()), 2) * m);
            // Эквивалентный обьем
            return cms * d * Math.pow(c, 2) * Math.pow(driver.getSd(), 2) * 10;
        } catch (ArithmeticException e) {
            return 0;
        }
    }

}

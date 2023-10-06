package com.example.tscalculator.ts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Driver {

    // Типы акустических оформлений
    final static public byte VT_NONE = -1;
    final static public byte VT_CLOSED_BOX = 0;
    final static public byte VT_VENTED_BOX = 1;
    final static public byte VT_CLOSED_OR_VENTED_BOX = 2;

    // Производитель
    private String manufacturer = "";

    // Модель
    private String model = "";

    // Частота резонанса динамической головки (Гц)
    private int fs = 0;

    // Диаметр диффузора от середины ширины подвеса одной стороны
    // до ширины подвеса противоположной (См)
    private double diameter = 0;

    // Механическая добротность
    private double qms = 0;

    // Электрическая добротность
    private double qes = 0;

    // Полная добротность
    private double qts = 0;

    // Эффективная излучающая поверхность диффузора (См^2)
    private double sd = 0;

    // Упрогость подвижной системы динамика (м/Н)
    private double cms = 0;

    // Эквивалентный обьем (Л)
    private double vas = 0;

    // Параметр для определения оптимального типа акустического оформления
    private double ebp = 0;

    // Оптимальный тип акустического оформления
    private byte optimalVolumeType = -1;

    // Алгоритм расчета Qts
    private QtsCalcTechnique qtsCalcTechnique = null;

    // Алгоритм расчета Vas
    private VasCalcTechnique vasCalcTechnique = null;

    public Driver(String manufacturer, String model) {
        super();
        this.manufacturer = manufacturer;
        this.model = model;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getFs() {
        return this.fs;
    }

    public void setFs(int fs) {
        this.fs = fs;
        if (qtsCalcTechnique != null)
            this.qts = qtsCalcTechnique.calcQts(this);
        calcEBP();
        if (vasCalcTechnique != null)
            this.vas = vasCalcTechnique.calcVas(this);
    }

    public double getDiameter() {
        return this.diameter;
    }

    public void setDiameter(double d) {
        diameter = d;
        calcSd();
        calcEBP();
        if (vasCalcTechnique != null)
            this.vas = vasCalcTechnique.calcVas(this);
    }

    public double getQms() {
        if (qtsCalcTechnique != null)
            this.qms = qtsCalcTechnique.calcQms(this);

        return this.qms;
    }

    public double getQes() {
        if (qtsCalcTechnique != null)
            this.qes = qtsCalcTechnique.calcQes(this);

        return this.qes;
    }

    public double getQts() {
        if (qtsCalcTechnique != null)
            this.qts = qtsCalcTechnique.calcQts(this);

        return this.qts;
    }

    public void setQtsCalcTechnique(QtsCalcTechnique qtsCalcTechnique) {
        this.qtsCalcTechnique = qtsCalcTechnique;
    }

    public double getSd() {
        calcSd();
        return this.sd;
    }

    public double getVas() {
        if (vasCalcTechnique != null)
            this.vas = vasCalcTechnique.calcVas(this);
        return this.vas;
    }

    public double getEbp() {
        calcEBP();
        return this.ebp;
    }

    public byte getOptimalVolumeType() {
        return optimalVolumeType;
    }

    public void setVasCalcTechnique(VasCalcTechnique vasCalcTechnique) {
        this.vasCalcTechnique = vasCalcTechnique;
    }

    private void calcSd() {
        final double R = diameter / 2;
        this.sd = Math.PI * Math.pow(R, 2);
    }

    private void calcEBP() {
        if (qts > 0) {
            ebp = fs / qts;

            if (ebp > 0) {
                if (ebp > 100) {
                    optimalVolumeType = VT_VENTED_BOX;
                } else {
                    if (ebp < 50) {
                        optimalVolumeType = VT_CLOSED_BOX;
                    } else {
                        optimalVolumeType = VT_CLOSED_OR_VENTED_BOX;
                    }
                }
            } else {
                optimalVolumeType = VT_NONE;
            }
        } else {
            ebp = 0;
            optimalVolumeType = VT_NONE;
        }
    }

    public JSONObject serializeToJSON() {
        final JSONObject params = new JSONObject();

        try {
            params.put("Fs", Integer.valueOf(getFs()));

            if (!Double.isNaN(getDiameter()) && !Double.isInfinite(getDiameter()))
                params.put("Diameter", Double.valueOf(getDiameter()));
            else
                params.put("Diameter", Double.valueOf(0.0d));

            if (!Double.isNaN(getQms()) && !Double.isInfinite(getQms()))
                params.put("Qms", Double.valueOf(getQms()));
            else
                params.put("Qms", Double.valueOf(0.0d));

            if (!Double.isNaN(getQes()) && !Double.isInfinite(getQes()))
                params.put("Qes", Double.valueOf(getQes()));
            else
                params.put("Qes", Double.valueOf(0.0d));

            if (!Double.isNaN(getQts()) && !Double.isInfinite(getQts()))
                params.put("Qts", Double.valueOf(getQts()));
            else
                params.put("Qts", Double.valueOf(0.0d));

            if (!Double.isNaN(getSd()) && !Double.isInfinite(getSd()))
                params.put("Sd", Double.valueOf(getSd()));
            else
                params.put("Sd", Double.valueOf(0.0d));

            if (!Double.isNaN(getVas()) && !Double.isInfinite(getVas()))
                params.put("Vas", Double.valueOf(getVas()));
            else
                params.put("Vas", Double.valueOf(0.0d));

            if (!Double.isNaN(getEbp()) && !Double.isInfinite(getEbp()))
                params.put("EBP", Double.valueOf(getEbp()));
            else
                params.put("EBP", Double.valueOf(0.0d));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return params;
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> params = new HashMap<>();

        params.put("Manufacturer", manufacturer);
        params.put("Model", model);
        params.put("Fs", Integer.valueOf(getFs()));
        params.put("Diameter", Double.valueOf(getDiameter()));
        params.put("Qms", Double.valueOf(getQms()));
        params.put("Qes", Double.valueOf(getQes()));
        params.put("Qts", Double.valueOf(getQts()));
        params.put("Sd", Double.valueOf(getSd()));
        params.put("Vas", Double.valueOf(getVas()));
        params.put("EBP", Double.valueOf(getEbp()));

        return params;
    }

    @Override
    public String toString () {
        return serialize().toString();
    }

}

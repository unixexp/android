package com.example.tscalculator.models;

public class Speaker {

    private String name = "";
    private int fs = 0;
    private double diameter = 0;
    private double qms = 0;
    private double qes = 0;
    private double qts = 0;
    private double sd = 0;
    private double vas = 0;
    private double ebp = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFs() {
        return fs;
    }

    public void setFs(int fs) {
        this.fs = fs;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public double getQms() {
        return qms;
    }

    public void setQms(double qms) {
        this.qms = qms;
    }

    public double getQes() {
        return qes;
    }

    public void setQes(double qes) {
        this.qes = qes;
    }

    public double getQts() {
        return qts;
    }

    public void setQts(double qts) {
        this.qts = qts;
    }

    public double getSd() {
        return sd;
    }

    public void setSd(double sd) {
        this.sd = sd;
    }

    public double getVas() {
        return vas;
    }

    public void setVas(double vas) {
        this.vas = vas;
    }

    public double getEbp() {
        return ebp;
    }

    public void setEbp(double ebp) {
        this.ebp = ebp;
    }
}

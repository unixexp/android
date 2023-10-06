package com.example.tscalculator.components;

final public class NumbersGenerator {

    final static public float[] getNumList(float start, float end, int count) {
        if (count <= 1)
            return new float[]{start};

        final float numList[] = new float[count];
        final float diff = end - start;
        final float step = diff / (float) (count-1);
        float value = start;
        numList[0] = start;
        for (int i = 1; i < count; i++) {
            value += step;
            numList[i] = value;
        }
        if (numList[count-1] != end)
            numList[count-1] = end;
        return numList;
    }

    final static public int[] getNumList(int start, int end) {
        final int count = end - start + 1;
        final int numList[] = new int[count];
        int value = start;
        for (int i = 0; i < count; i++) {
            numList[i] = value;
            value++;
        }
        return numList;
    }

    final static public float[] getNumList(float start, float end) {
        final float count = end - start + 1;
        final float numList[] = new float[(int) count];
        float value = start;
        for (int i = 0; i < count; i++) {
            numList[i] = value;
            value++;
        }
        return numList;
    }

}

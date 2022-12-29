package de.citec.sc.generator.utils;

public class ProgressSingleton {
    private static ProgressSingleton INSTANCE;
    private int count = 0;
    private String propertyCsv = "";

    private ProgressSingleton() {
    }

    public static ProgressSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProgressSingleton();
        }

        return INSTANCE;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPropertyCsv() {
        return propertyCsv;
    }

    public void setPropertyCsv(String propertyCsv) {
        this.propertyCsv = propertyCsv;
    }
}

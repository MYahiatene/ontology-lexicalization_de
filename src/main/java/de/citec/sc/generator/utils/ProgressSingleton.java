package de.citec.sc.generator.utils;

public class ProgressSingleton {
    private static ProgressSingleton INSTANCE;
    private int count = 0;
    private String propertyCsv = "";
    private int progress = 0;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

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

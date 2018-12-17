package org.md2k.motionsense;


/*

    Class for threads that export data to a csv
    -  Using the exporter class, we export the string message to a specific folder

 */

public class exportRunnable implements Runnable {

    final String foldername;
    final String message;

    exporter exp;

    public exportRunnable(String foldername, String message, exporter exp) {
        this.foldername = foldername;
        this.message = message;
        this.exp = exp;
    }

    public void run() {
        exp.exportData(foldername, message);
    }
}
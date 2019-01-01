package org.md2k.motionsense;


/*

    Class for a data object
    -  Using the exporter class, we export the string message to a specific folder

 */

public class exportRunnable {

    final String foldername;
    final String message;


    public exportRunnable(String foldername, String message) {
        this.foldername = foldername;
        this.message = message;
    }

    /*public void run() {
        exp.exportData(foldername, message);
    }*/
}
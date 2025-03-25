package org.example;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Reader reader = new Reader();
        try {
            reader.lectorCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
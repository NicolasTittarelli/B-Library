package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reader {

    public void lectorCSV() throws FileNotFoundException {

        /**
         * Upload csv route before starting
         * */

        String csv = "prueba.csv";
        List<String[]> rows = csvReader(csv);

        List<String[]> cleanData = cleanAndReplace(rows);
        Map<String, List<String[]>> groupedData = grouping(cleanData);

        for (Map.Entry<String, List<String[]>> entry : groupedData.entrySet()) {
            System.out.println("\n--- " + entry.getKey() + " ---");
            for (String[] fila : entry.getValue()) {
                System.out.println(String.join(", ", fila));
            }
        }
    }

    private List<String[]> csvReader(String csv) {

        try (CSVReader reader = new CSVReader(new FileReader(csv))) {
            return reader.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Method that removes duplicate titles. If there is no title, it does not add the content
     * to the map structure, effectively deleting the row.
     */

    private List<String[]> removeDuplicates(List<String[]> rows) {
        Map<String, String[]> map = new HashMap<>();

        for (String[] row : rows) {
            //If the row does not have a title or does not have 3 columns, it is discarded
            if (row.length != 3 || row[0].isEmpty()) {
                continue;
            }
            String titulo = row[0];
            if (!map.containsKey(titulo)) {
                map.put(titulo, row);
            }
        }
        return new ArrayList<>(map.values());
    }

    /**
     * Method that replaces book years. It uses a regular expression to check if the year is null or contains letters,
     * assigning "0" in those cases. It also assigns "0" if the year is less than 0 or greater than 2025.
     */

    private List<String[]> replaceYears(List<String[]> data) {

        List<String[]> listaCorregida = new ArrayList<>();

        for (String[] fila : data) {

            String yearString = fila[2];
            if (!yearString.matches("\\d+")) {
                yearString = "0";
            } else {
                int year = Integer.parseInt(yearString);
                if (year < 0 || year > 2025) {
                    yearString = "0";
                }
            }

            fila[2] = yearString;
            listaCorregida.add(fila);
        }

        return listaCorregida;
    }

    /**
     * Method that replaces empty author fields with "Unknown Author".
     */
    private List<String[]> replaceAuthors(List<String[]> data) {
        List<String[]> listaCorregida = new ArrayList<>();

        for (String[] row : data) {
            String author = row[1];
            if (author.isEmpty()) {
                author = "Author Unknown";
            }
            row[1] = author;
            listaCorregida.add(row);
        }
        return listaCorregida;
    }

    /**
     * Method that cleans duplicate titles, corrects invalid years, and replaces empty author names.
     */
    private List<String[]> cleanAndReplace(List<String[]> rows) {
        List<String[]> datosLimpios = removeDuplicates(rows);
        List<String[]> datosConAñosCorregidos = replaceYears(datosLimpios);
        return replaceAuthors(datosConAñosCorregidos);
    }

    /**
     * Method that groups data by authors. If the author is unknown, the data is grouped by year instead.
     */
    private Map<String, List<String[]>> grouping(List<String[]> data) {
        Map<String, List<String[]>> grouped = new HashMap<>();

        for (String[] fila : data) {
            String author = fila[1];
            String year = fila[2];

            String groupKey;

            if (author.equals("Author Unknown") || author.equals("Author Desconocido")) {
                groupKey = year;
            } else {
                groupKey = author;
            }

            if (!grouped.containsKey(groupKey)) {
                grouped.put(groupKey, new ArrayList<>());
            }

            grouped.get(groupKey).add(fila);
        }

        return grouped;
    }

}


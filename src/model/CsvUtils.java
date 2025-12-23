package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {

 public static List<String[]> readCsv(String path) throws IOException {
    List<String[]> rows = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {

        String line;
        boolean headerSkipped = false;

        while ((line = br.readLine()) != null) {

            if (!headerSkipped) { headerSkipped = true; continue; }

            // Split on commas that are NOT inside quotes
            String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            // Remove surrounding quotes (optional)
            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].replaceAll("^\"|\"$", "").trim();
            }

            rows.add(values);
        }
    }
    return rows;
}

    public static void appendLine(String path, String[] values) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            bw.write(String.join(",", values));
            bw.newLine();
        }
    }
}

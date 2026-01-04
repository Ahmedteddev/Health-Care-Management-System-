package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Utility class for reading and writing CSV files
public class CsvUtils {
    
    // Reads a CSV file and returns a list of rows (skips the header line)
    public static List<String[]> readCsv(String filePath) throws IOException {
        List<String[]> rows = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean headerSkipped = false;
            
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Split on commas but ignore commas inside quotes
                String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                
                // Remove quotes and trim each value
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != null) {
                        values[i] = values[i].replaceAll("^\"|\"$", "").trim();
                    }
                }
                
                rows.add(values);
            }
        } catch (IOException ex) {
            System.err.println("Error reading CSV file: " + filePath);
            System.err.println("Error message: " + ex.getMessage());
            throw ex;
        }
        
        return rows;
    }
    
    // Adds a new row to the end of a CSV file
    public static void appendLine(String filePath, String[] data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            String line = String.join(",", data);
            bw.write(line);
            bw.newLine();
        } catch (IOException ex) {
            System.err.println("Error appending to CSV file: " + filePath);
            System.err.println("Error message: " + ex.getMessage());
            throw ex;
        }
    }
}


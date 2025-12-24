package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for CSV file operations.
 * Provides methods to read and append data to CSV files.
 */
public class CsvUtils {
    
    /**
     * Reads a CSV file and returns a list of string arrays, one per row.
     * Skips the first (header) line.
     * Handles commas inside quotes when possible.
     * 
     * @param filePath The path to the CSV file
     * @return List of string arrays, each representing a row (without header)
     * @throws IOException If the file cannot be read
     */
    public static List<String[]> readCsv(String filePath) throws IOException {
        List<String[]> rows = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean headerSkipped = false;
            
            while ((line = br.readLine()) != null) {
                // Skip the header line
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Try to handle commas inside quotes
                // Split on commas that are NOT inside quotes
                String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                
                // Remove surrounding quotes and trim whitespace
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
    
    /**
     * Appends a new line to a CSV file.
     * 
     * @param filePath The path to the CSV file
     * @param data Array of strings representing the row data
     * @throws IOException If the file cannot be written to
     */
    public static void appendLine(String filePath, String[] data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            // Join the data with commas
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


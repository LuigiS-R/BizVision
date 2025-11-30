package com.example.TechWeek25.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

public class DataLoader {

    // --- ⬇️ 1. CONFIGURE YOUR DETAILS HERE ⬇️ ---
    private static final String CSV_FILE_PATH = "/Users/luigi/Desktop/Luigi/PNU/2025학년/2학기/TechWeek25/Hackhathon/techWeek25-backend/src/main/java/com/example/TechWeek25/util/dataSet.csv"; // ⚠️ UPDATE THIS
    private static final String DB_URL = "jdbc:mysql://localhost:3306/techWeek25-backend"; // ⚠️ UPDATE THIS
    private static final String DB_USER = "root"; // ⚠️ UPDATE THIS
    private static final String DB_PASSWORD = "20Py/Recursion/Js19"; // ⚠️ UPDATE THIS
    // --- ⬆️ 1. CONFIGURE YOUR DETAILS HERE ⬆️ ---


    public static void main(String[] args) {
        // SQL statement for inserting data
        String sql = "INSERT INTO daily_foot_traffic (record_date, dong_name, visitor_count) VALUES (?, ?, ?)";
        int batchSize = 1000; // Execute batch every 1000 records
        int count = 0;

        try (
                // Step 1: Establish a connection to the database
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                // Step 2: Set up the prepared statement for batch inserts
                PreparedStatement statement = connection.prepareStatement(sql);

                // Step 3: Set up the CSV file reader
                Reader reader = new FileReader(CSV_FILE_PATH);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        ) {
            System.out.println("Database connection successful. Starting data load...");

            // Get the header map which contains the dong names as keys
            List<String> dongNames = csvParser.getHeaderNames().subList(1, csvParser.getHeaderNames().size()); // Skip the first column (date)

            // Step 4: Loop through each row in the CSV file
            for (CSVRecord record : csvParser) {
                String recordDate = record.get(0); // Get the date from the first column

                // Step 5: "Unpivot" the data - Loop through each dong column for the current row
                for (String dongName : dongNames) {
                    if (record.isSet(dongName)) {
                        String visitorCountStr = record.get(dongName);
                        // Make sure the visitor count is not empty before parsing
                        if (visitorCountStr != null && !visitorCountStr.isEmpty()) {
                            int visitorCount = Integer.parseInt(visitorCountStr);

                            // Set the parameters for the INSERT statement
                            statement.setString(1, recordDate);
                            statement.setString(2, dongName);
                            statement.setInt(3, visitorCount);

                            // Add the statement to the batch
                            statement.addBatch();
                            count++;
                        }
                    }
                }

                // Execute the batch insert every 1000 records for efficiency
                if (count % batchSize == 0) {
                    statement.executeBatch();
                    System.out.println("Executed batch of " + batchSize + " records.");
                }
            }

            // Execute any remaining statements in the batch
            statement.executeBatch();
            System.out.println("Executed the final batch of records.");
            System.out.println("Data loading complete! Total records inserted: " + count);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.example.TechWeek25.util;

import com.example.TechWeek25.api.APIClientHelper;
import com.example.TechWeek25.entity.CommercialArea;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter implements CommandLineRunner {
    private static APIClientHelper apiClientHelper;

    public CSVWriter(APIClientHelper apiClientHelper) {
        this.apiClientHelper = apiClientHelper;
    }
    public static void writeToCSV(List<CommercialArea> areas, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Writing the header
            writer.write("area_id,area_id,city,district,latitude,longitude\n");

            // Writing each object's data in CSV format
            for (CommercialArea area : areas) {
                writer.write(String.format("%d,%s,%s,%s,%f,%f\n",
                        area.getAreaId(),
                        area.getAreaName(),
                        area.getCity(),
                        area.getDistrict(),
                        area.getLatitude(),
                        area.getLongitude()));
            }

            System.out.println("CSV file has been written successfully.");
        } catch (IOException e) {
            System.err.println("Error while writing to CSV: " + e.getMessage());
        }
    }

    // Example usage
    public void run(String... args){
        System.out.println("Fetching commercial areas from API...");
        List<CommercialArea> fileContents = apiClientHelper.getCommercialAreas();
        writeToCSV(fileContents, "src/main/resources/commercial_areas.csv");
    }
}
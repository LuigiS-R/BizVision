package com.example.TechWeek25.util;

import com.example.TechWeek25.entity.CommercialArea;
import com.example.TechWeek25.entity.RealEstateTrend;
import com.example.TechWeek25.repository.AreaRepository;
import com.example.TechWeek25.repository.RealEstateTrendRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final AreaRepository areaRepository;
    private final RealEstateTrendRepository realEstateTrendRepository;

    public DatabaseSeeder(AreaRepository areaRepository, RealEstateTrendRepository realEstateTrendRepository) {
        this.areaRepository = areaRepository;
        this.realEstateTrendRepository = realEstateTrendRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // --- EXISTING CODE FOR COMMERCIAL AREAS (UNCHANGED) ---
        if (areaRepository.count() == 0) {
            logger.info("🌱 Database is empty. Seeding commercial area data from the CSV file...");
            try {
                List<CommercialArea> areasToSave = readCommercialAreasFromCSV("commercial_areas.csv");
                areaRepository.saveAll(areasToSave);
                logger.info("✅ Successfully seeded {} areas into the database.", areasToSave.size());
            } catch (Exception e) {
                logger.error("❌ Failed to seed area data during startup.", e);
            }
        } else {
            logger.info("✅ Database already contains area data. No seeding needed.");
        }

        // --- NEW CODE FOR REAL ESTATE TRENDS ---
        if (realEstateTrendRepository.count() == 0) {
            logger.info("🌱 Database is empty. Seeding real estate trend data from TSV files...");
            try {
                List<RealEstateTrend> trendsToSave = processAndAggregateTsvData();
                if (!trendsToSave.isEmpty()) {
                    realEstateTrendRepository.saveAll(trendsToSave);
                    logger.info("✅ Successfully seeded {} real estate trend records.", trendsToSave.size());
                }
            } catch (Exception e) {
                logger.error("❌ Failed to seed real estate trend data.", e);
            }
        } else {
            logger.info("✅ Database already contains real estate trend data. No seeding needed.");
        }
    }

    // --- EXISTING HELPER METHOD (UNCHANGED) ---
    private List<CommercialArea> readCommercialAreasFromCSV(String fileName) {
        List<CommercialArea> commercialAreas = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource(fileName);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                boolean isFirstLine = true;
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    String[] attributes = line.split(",");
                    if (attributes.length == 6) {
                        CommercialArea area = new CommercialArea(
                                Long.parseLong(attributes[0]),
                                attributes[1],
                                attributes[2],
                                attributes[3],
                                Double.parseDouble(attributes[4]),
                                Double.parseDouble(attributes[5])
                        );
                        commercialAreas.add(area);
                    } else {
                        logger.warn("❌ Malformed line in CSV: {}", line);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("❌ Error reading CSV file.", e);
        }
        return commercialAreas;
    }

    // --- NEW HELPER METHODS FOR TSV PROCESSING ---

    private record ProcessedTransaction(String district, String yearMonth, double pricePerSqm, String propertyType) {}

    private List<RealEstateTrend> processAndAggregateTsvData() throws IOException {
        String commercialPath = "data/commercial";
        //String officetelPath = "data/officetel";

        List<ProcessedTransaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(processDirectory(commercialPath, "COMMERCIAL"));
        //allTransactions.addAll(processDirectory(officetelPath, "OFFICETEL"));

        if (allTransactions.isEmpty()) {
            logger.error("Error: No data was processed successfully from TSV files. Check file paths and content.");
            return Collections.emptyList();
        }

        logger.info("\nCalculating median prices from {} transactions...", allTransactions.size());

        Map<String, Map<String, Map<String, Double>>> medianPrices = allTransactions.stream()
                .collect(Collectors.groupingBy(ProcessedTransaction::district,
                        Collectors.groupingBy(ProcessedTransaction::yearMonth,
                                Collectors.groupingBy(ProcessedTransaction::propertyType,
                                        Collectors.collectingAndThen(Collectors.toList(), this::calculateMedianPrice)))));

        List<RealEstateTrend> trends = new ArrayList<>();
        medianPrices.forEach((district, yearMonthMap) ->
                yearMonthMap.forEach((yearMonth, propertyTypeMap) ->
                        propertyTypeMap.forEach((propertyType, medianPrice) -> {
                            RealEstateTrend trend = new RealEstateTrend();
                            trend.setDistrict(district);
                            trend.setYearMonth(yearMonth);
                            trend.setPropertyType(propertyType);
                            trend.setMedianPricePerSqm(medianPrice.intValue());
                            trends.add(trend);
                        })
                )
        );
        return trends;
    }

    // ** THE FIX IS HERE (METHOD 1 of 2) **
    // This method now uses Spring's ResourcePatternResolver to find all TSV files within a classpath directory,
    // which works correctly inside a JAR file.
    private List<ProcessedTransaction> processDirectory(String dirPath, String propertyType) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:" + dirPath + "/*.tsv");

        if (resources.length == 0) {
            logger.warn("Warning: No TSV files found in directory: {}", dirPath);
            return Collections.emptyList();
        }

        List<ProcessedTransaction> allTransactions = new ArrayList<>();
        for (Resource resource : resources) {
            allTransactions.addAll(processFile(resource, propertyType));
        }
        return allTransactions;
    }

    // ** THE FIX IS HERE (METHOD 2 of 2) **
    // This method now accepts a Spring `Resource` object and gets an `InputStream` from it,
    // instead of trying to get a `File` object.
    private List<ProcessedTransaction> processFile(Resource resource, String propertyType) {
        logger.info("Processing {}", resource.getFilename());
        List<ProcessedTransaction> transactions = new ArrayList<>();
        CSVFormat tsvFormat = CSVFormat.DEFAULT.builder().setDelimiter('\t').build();

        try (Reader reader = new InputStreamReader(resource.getInputStream(), Charset.forName("EUC-KR"));
             CSVParser parser = new CSVParser(reader, tsvFormat)) {

            List<CSVRecord> records = parser.getRecords();
            if (records.size() < 2) {
                return transactions;
            }

            for (int i = 1; i < records.size(); i++) {
                CSVRecord record = records.get(i);
                try {
                    String sigungu, contractYearMonth;
                    double areaSqm;
                    long priceKrw10k;

                    if ("COMMERCIAL".equals(propertyType)) {
                        if (record.size() < 13) continue;
                        sigungu = record.get(1).trim();
                        areaSqm = Double.parseDouble(record.get(8).trim());
                        contractYearMonth = record.get(12).trim();
                        priceKrw10k = Long.parseLong(record.get(10).trim().replace(",", ""));
                    } else if ("OFFICETEL".equals(propertyType)) {
                        if (record.size() < 10) continue;
                        sigungu = record.get(1).trim();
                        areaSqm = Double.parseDouble(record.get(6).trim());
                        contractYearMonth = record.get(7).trim();
                        priceKrw10k = Long.parseLong(record.get(9).trim().replace(",", ""));
                    } else {
                        continue;
                    }

                    if (contractYearMonth == null || contractYearMonth.length() != 6 || !contractYearMonth.matches("\\d+")) {
                        continue;
                    }
                    if (areaSqm <= 0) continue;

                    double pricePerSqm = (double) (priceKrw10k * 10000) / areaSqm;
                    YearMonth ym = YearMonth.parse(contractYearMonth, DateTimeFormatter.ofPattern("yyyyMM"));
                    String yearMonthFormatted = ym.format(DateTimeFormatter.ofPattern("yyyy-MM"));

                    String[] addressParts = sigungu.split("\\s+");
                    if (addressParts.length > 1) {
                        String district = addressParts[1];
                        transactions.add(new ProcessedTransaction(district, yearMonthFormatted, pricePerSqm, propertyType));
                    }
                } catch (IllegalArgumentException e) {
                    logger.warn("Skipping malformed row due to error: {} - Row: {}", e.getMessage(), record.toString());
                }
            }
        } catch (IOException e) {
            logger.error("Error reading file {}: {}", resource.getFilename(), e.getMessage());
        }
        return transactions;
    }

    private Double calculateMedianPrice(List<ProcessedTransaction> transactions) {
        if (transactions.isEmpty()) return 0.0;
        List<Double> prices = transactions.stream().map(ProcessedTransaction::pricePerSqm).sorted().collect(Collectors.toList());
        int middle = prices.size() / 2;
        if (prices.size() % 2 == 1) return prices.get(middle);
        else return (prices.get(middle - 1) + prices.get(middle)) / 2.0;
    }
}
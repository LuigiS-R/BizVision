package com.example.TechWeek25.service;

import com.example.TechWeek25.api.APIClientHelper;
import com.example.TechWeek25.dto.CompetitionData;
import com.example.TechWeek25.dto.CongestionData;
import com.example.TechWeek25.dto.QueryInput;
import com.example.TechWeek25.dto.realEstateData.RealEstateTrendObject;
import com.example.TechWeek25.dto.realEstateData.RealEstateTrends;
import com.example.TechWeek25.entity.CommercialArea;
import com.example.TechWeek25.entity.RealEstateTrend;
import com.example.TechWeek25.repository.AreaRepository;
import com.example.TechWeek25.repository.RealEstateTrendRepository;
import com.example.TechWeek25.util.LocationUtils;
import com.example.TechWeek25.util.TargetAudience;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalysisService {
    private final APIClientHelper apiClientHelper;
    private final AreaRepository commercialAreaRepository;
    private final RealEstateTrendRepository realEstateTrendRepository;

    public AnalysisService(APIClientHelper apiClientHelper, AreaRepository commercialAreaRepository, RealEstateTrendRepository realEstateTrendRepository) {
        this.apiClientHelper = apiClientHelper;
        this.commercialAreaRepository = commercialAreaRepository;
        this.realEstateTrendRepository = realEstateTrendRepository;
    }

    public Object analyze(QueryInput location){

        String longitude = location.getLongitude();
        String latitude = location.getLatitude();
        String category = location.getCategory();
        Object[] tmp = (Object[]) apiClientHelper.getBusinessCount(longitude, latitude, 500);
        CompetitionData response = new CompetitionData((Integer)tmp[0],(Map<String, Integer>) tmp[1]);
        String[] coordinatesInfo = apiClientHelper.getCoordinatesInfo(longitude, latitude);


        //Starting search process
        Optional<List<CommercialArea>> areas = commercialAreaRepository.findByAreaName(coordinatesInfo[0]);
        if(areas.isPresent()){
            List<CommercialArea> commercialAreaList = areas.get();
            if (commercialAreaList.size() > 1) {
                Map<String, List<Double>> hourlyBreakdown = (Map<String, List<Double>>) apiClientHelper.getCongestionLevel(findClosestArea(commercialAreaList, location));
                Map<String, Double> weeklyRhythm = new HashMap<>();
                for(String day : hourlyBreakdown.keySet()){
                    prepareCongestionData(hourlyBreakdown.get(day));
                    weeklyRhythm.put(day, setAverageCongestionPerDay(hourlyBreakdown.get(day)));
                }

                Map<String, Double> map = apiClientHelper.getDemographics(commercialAreaList.getFirst().getAreaId().toString());
                preparingDistributionData(map);

                response.setCongestionData(new CongestionData(weeklyRhythm, hourlyBreakdown));
                response.setVisitorsDistribution(map);


            }
            else{
                areas = commercialAreaRepository.findByDistrict(coordinatesInfo[0]);
                if(areas.isPresent()){
                    commercialAreaList = areas.get();
                    if (commercialAreaList.size() > 1) {
                        CommercialArea closestArea = findClosestArea(commercialAreaList, location);
                        Map<String, List<Double>> hourlyBreakdown = (Map<String, List<Double>>) apiClientHelper.getCongestionLevel(closestArea);

                        Map<String, Double> weeklyRhythm = new HashMap<>();
                        for(String day : hourlyBreakdown.keySet()){
                            prepareCongestionData(hourlyBreakdown.get(day));
                            weeklyRhythm.put(day, setAverageCongestionPerDay(hourlyBreakdown.get(day)));
                        }

                        Map<String, Double> map = apiClientHelper.getDemographics(commercialAreaList.getFirst().getAreaId().toString());
                        preparingDistributionData(map);

                        response.setCongestionData(new CongestionData(weeklyRhythm, hourlyBreakdown));
                        response.setVisitorsDistribution(map);
                    }
                    else{
                        areas = commercialAreaRepository.findByDistrict(coordinatesInfo[0]);
                        if(areas.isPresent()){
                            commercialAreaList = areas.get();
                            if (commercialAreaList.size() > 1) {
                                CommercialArea closestArea = findClosestArea(commercialAreaList, location);
                                Map<String, List<Double>> hourlyBreakdown = (Map<String, List<Double>>) apiClientHelper.getCongestionLevel(closestArea);

                                Map<String, Double> weeklyRhythm = new HashMap<>();
                                for(String day : hourlyBreakdown.keySet()){
                                    prepareCongestionData(hourlyBreakdown.get(day));
                                    weeklyRhythm.put(day, setAverageCongestionPerDay(hourlyBreakdown.get(day)));
                                }

                                Map<String, Double> map = apiClientHelper.getDemographics(commercialAreaList.getFirst().getAreaId().toString());
                                preparingDistributionData(map);

                                response.setCongestionData(new CongestionData(weeklyRhythm, hourlyBreakdown));
                                response.setVisitorsDistribution(map);
                            }
                            else{
                                areas = commercialAreaRepository.findByDistrict(coordinatesInfo[0]);
                                if(areas.isPresent()){
                                    commercialAreaList = areas.get();
                                    if (commercialAreaList.size() > 1) {
                                        CommercialArea closestArea = findClosestArea(commercialAreaList, location);
                                        Map<String, List<Double>> hourlyBreakdown = (Map<String, List<Double>>) apiClientHelper.getCongestionLevel(closestArea);

                                        Map<String, Double> weeklyRhythm = new HashMap<>();
                                        for(String day : hourlyBreakdown.keySet()){
                                            prepareCongestionData(hourlyBreakdown.get(day));
                                            weeklyRhythm.put(day, setAverageCongestionPerDay(hourlyBreakdown.get(day)));
                                        }

                                        Map<String, Double> map = apiClientHelper.getDemographics(commercialAreaList.getFirst().getAreaId().toString());
                                        preparingDistributionData(map);

                                        response.setCongestionData(new CongestionData(weeklyRhythm, hourlyBreakdown));
                                        response.setVisitorsDistribution(map);
                                    }

                                }
                            }
                        }
                    }

                }
            }
        }

        //Real State data processing:
        String district = coordinatesInfo[0];
        List<RealEstateTrend> commercialTransactions = realEstateTrendRepository.findByDistrictAndPropertyTypeOrderByYearMonthAsc(district, "COMMERCIAL");
        List<RealEstateTrend> officeTransactions = realEstateTrendRepository.findByDistrictAndPropertyTypeOrderByYearMonthAsc(district, "OFFICE");

        List<RealEstateTrendObject> responseCommercialTransactions = new ArrayList<>();
        List<RealEstateTrendObject> responseOfficeTransactions = new ArrayList<>();
        int i = 0;
        for(RealEstateTrend realEstateTrend : commercialTransactions){
            RealEstateTrend commercialTransaction = commercialTransactions.get(i++);
            responseCommercialTransactions.add(new RealEstateTrendObject(commercialTransaction.getYearMonth(), commercialTransaction.getMedianPricePerSqm()));
        }
        i = 0;
        for(RealEstateTrend realEstateTrend : officeTransactions){
            RealEstateTrend officeTransaction = officeTransactions.get(i++);
            responseOfficeTransactions.add(new RealEstateTrendObject(officeTransaction.getYearMonth(), officeTransaction.getMedianPricePerSqm()));
        }
        RealEstateTrends realEstateTrends = new RealEstateTrends(responseCommercialTransactions, responseOfficeTransactions);
        //realEstateTrends.put("COMMERCIAL", responseCommercialTransactions);
        //realEstateTrends.put("OFFICE", responseOfficeTransactions);

        response.setRealEstateTrends(realEstateTrends);

        int feasibilityIndex = getGeneralFeasibilityIndex(response.getCongestionData(), response.getRealEstateTrends(), response.getTotalCount(), 500);
        response.setFeasibilityIndex(feasibilityIndex);

        int tailoredFeasibilityIndex = getTailoredFeasibilityIndex(category, response.getVisitorsDistribution(), response.getTotalCount(), response.getCategoryCounts(), feasibilityIndex);
        response.setTailoredFeasibilityIndex(tailoredFeasibilityIndex);

        return response;
    }

    private CommercialArea findClosestArea(List<CommercialArea> areas, QueryInput inputLocation){
        CommercialArea closestArea = null;
        int minDistance = Integer.MAX_VALUE;
        for(CommercialArea commercialArea : areas){
            double distance = LocationUtils.calculateDistance(Double.parseDouble(inputLocation.getLatitude()), Double.parseDouble(inputLocation.getLongitude()), commercialArea.getLatitude(), commercialArea.getLongitude());
            if (distance < minDistance){
                minDistance = (int) distance;
                closestArea = commercialArea;
            }
        }
        return closestArea;
    }

    private void prepareCongestionData(List<Double> congestionData){
        double max = Collections.max(congestionData);

        for(int i = 0; i < congestionData.size(); i++){
            congestionData.set(i, (congestionData.get(i) / max) * 100);
        }
    }

    private double setAverageCongestionPerDay(List<Double> congestionData){
        double sum = 0;
        for(Double congestion : congestionData){
            sum+=congestion;
        }
        return sum/congestionData.size();
    }

    private void preparingDistributionData(Map<String, Double> map){
        double sum = 0;
        for(String key : map.keySet()){
            sum += map.get(key);
        }

        for(String key : map.keySet()){
            map.put(key, (map.get(key)/sum) * 100);
        }
    }

    private int getGeneralFeasibilityIndex(CongestionData congestionData, RealEstateTrends realEstateTrends, int totalCounts, int radius) {
        // --- 1. Traffic Score Calculation (Your code was correct) ---
        Map<String, List<Double>> hourlyBreakdown = congestionData.getHourlyBreakdown();
        double congestionAverage = 0;
        for (String day : hourlyBreakdown.keySet()) {
            // Prime hours are 10 AM (index 10) to 10 PM (index 22)
            for (int i = 10; i < 23; i++) {
                congestionAverage += hourlyBreakdown.get(day).get(i);
            }
        }
        // 13 hours per day * 7 days = 91 data points
        congestionAverage = congestionAverage / 91;
        double trafficScore = (congestionAverage / 100) * 40;


        // --- 2. Economic Score Calculation ---
        List<RealEstateTrendObject> commercialRealEstateTrend = realEstateTrends.getCommercial();

        // BUG FIX 1: Find the start index dynamically instead of hardcoding '99'.
        // This makes your code robust and prevents errors if the data range changes.
        int startIndex = 0;
        for (int i = 0; i < commercialRealEstateTrend.size(); i++) {
            if ("2015-01".equals(commercialRealEstateTrend.get(i).getYearMonth())) {
                startIndex = i;
                break;
            }
        }

        if (startIndex == 0) { // Fallback if 2015-01 is not found
            return -1; // Or handle error appropriately
        }

        double startPrice = commercialRealEstateTrend.get(startIndex).getMedianPricePerSqm();
        double endPrice = commercialRealEstateTrend.get(commercialRealEstateTrend.size() - 1).getMedianPricePerSqm();

        // BUG FIX 2: Growth formula was backwards. It's (end - start), not (start - end).
        double growth = ((endPrice - startPrice) / startPrice) * 100;
        double growthScore = Math.min(growth, 100) / 100;
        if (growthScore < 0) growthScore = 0; // Growth cannot be negative for the score

        double averageCommercialPrice = 0;
        for (int i = startIndex; i < commercialRealEstateTrend.size(); i++) {
            averageCommercialPrice += commercialRealEstateTrend.get(i).getMedianPricePerSqm();
        }
        averageCommercialPrice = averageCommercialPrice / (commercialRealEstateTrend.size() - startIndex);

        // Assuming calculateStandardDeviation is correctly implemented to take a sublist
        List<RealEstateTrendObject> fiveYearData = commercialRealEstateTrend.subList(startIndex, commercialRealEstateTrend.size());
        double volatility = calculateStandardDeviation(fiveYearData) / averageCommercialPrice;
        double stabilityScore = 1 - Math.min(volatility, 1);

        double economicScore = ((growthScore + stabilityScore) / 2) * 40;


        // --- 3. Competition Score Calculation ---
        double area = Math.PI * Math.pow(((double) radius / 1000), 2);

        // BUG FIX 3: You were using totalCounts directly, not density.
        double businessDensity = (double) totalCounts / area;

        double normalizedCompetition = Math.min(1.0, businessDensity / 1500.0);
        double competitionScore = (1 - normalizedCompetition) * 20;


        // --- 4. Final Index Calculation ---
        // BUG FIX 4: You were adding the wrong components. It should be the three pillar scores.
        return (int) Math.ceil((trafficScore + economicScore + competitionScore));
    }

    private int getTailoredFeasibilityIndex(String category, Map<String, Double> visitorsDistribution, int totalCount, Map<String, Integer> categoryCounts, int generalScore){
        List<String> targetAudience = TargetAudience.PROFILES.get(category);

        int actualAudiencePercent = 0;
        for(String audience : targetAudience){
            System.out.println(audience);
            actualAudiencePercent += visitorsDistribution.get(audience);
        }
        double idealAudiencePercent = 30.0;

        // Calculate the bonus, capping it at 1.0
        double matchRatio = Math.min(1.0, actualAudiencePercent / idealAudiencePercent);
        double audienceMatchBonus = matchRatio * 15; // This will be between 0 and 15

        // From your data: 326 cafes, 2784 total businesses
        System.out.println("Printing categoryCounts:");
        for(String key : categoryCounts.keySet()){
            System.out.println(key + " : " + categoryCounts.get(key));
        }
        System.out.println("my Category: " + category);
        double categoryCount = categoryCounts.get(category);
        double saturationThreshold = 10.0; // 10%

        double currentSaturationPercent = (categoryCount / totalCount) * 100; // This is 11.7%

// Calculate the penalty, capping it at 1.0
        double penaltyRatio = Math.min(1.0, currentSaturationPercent / saturationThreshold);
        double marketSaturationPenalty = penaltyRatio * 15; // This will be between 0 and 15

        int finalSpecificScore = generalScore + (int)audienceMatchBonus - (int)marketSaturationPenalty;
        return finalSpecificScore;

    }

    public static double calculateStandardDeviation(List<RealEstateTrendObject> trends) {
        if (trends == null || trends.isEmpty()) {
            return Double.NaN;
        }

        // Calculate the mean (average) of median prices per square meter
        double mean = trends.stream()
                .mapToDouble(RealEstateTrendObject::getMedianPricePerSqm)
                .average()
                .orElse(0.0);

        // Calculate the variance
        double variance = trends.stream()
                .mapToDouble(trend -> Math.pow(trend.getMedianPricePerSqm() - mean, 2))
                .average()
                .orElse(0.0);

        // Standard deviation is the square root of variance
        return Math.sqrt(variance);
    }
}

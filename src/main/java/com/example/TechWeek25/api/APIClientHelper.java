package com.example.TechWeek25.api;

import com.example.TechWeek25.dto.*;
import com.example.TechWeek25.entity.CommercialArea;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class APIClientHelper {
    private final WebClient webClient;
    private final WebClient encodingWebClient;

    private final ObjectMapper objectMapper;

    private Map <String, Object> responseMap;
    private Map <String, Object> bodyMap;
    private Map <String, Integer> categoryCounts;

    //SK API Related
   // private final WebClient SkApiwebClient;
    private static final int API_LIMIT = 500; // A configurable limit
    //
    // Constructor-based Dependency Injection for WebClient
    public APIClientHelper(@Qualifier("noEncoding") WebClient webClient, @Qualifier("encoding") WebClient encodingWebClient, ObjectMapper objectMapper, Map<String, Integer> categoryCounts) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.categoryCounts = categoryCounts;
        this.encodingWebClient = encodingWebClient;
    }

    public Object fetchDataFromAPI(String pageNo, String numOfRows, String radius, String cx, String cy) {
        try {
            String encodedServiceKey = "BmmyCa%2B4ItBrR%2BZS1sBhAykMkDRKPEiAnD4e2je5W5roNrHL3Aaj6tX5bykDfK7reF9grgAp2B7MSmplJRqD3w%3D%3D";

            // Manually build the full URL. String.format is great for this.
            String fullUrl = String.format(
                    "https://apis.data.go.kr/B553077/api/open/sdsc2/storeListInRadius?ServiceKey=%s&pageNo=%s&numOfRows=%s&radius=%s&cx=%s&cy=%s&type=json",
                    encodedServiceKey, pageNo, numOfRows, radius, cx, cy
            );

            responseMap = webClient.get()
                    .uri(fullUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(jsonResponse ->{
                        try {
                            responseMap = objectMapper.readValue(jsonResponse, Map.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return responseMap;
                    })
                    .block();

            bodyMap = (Map<String, Object>) responseMap.get("body");
            int totalCount = (int) bodyMap.get("totalCount");
            List<String> indexes = (List<String>) bodyMap.get("items");
            for(Object e: indexes){
                String category  = (String)((Map<String, Object>) e).get("indsSclsNm");
                if(categoryCounts.containsKey(category)){
                    categoryCounts.put(category, categoryCounts.get(category)+1);
                }else{
                    categoryCounts.put(category, 1);
                }

            }
            return new CompetitionData(totalCount, categoryCounts);

        } catch (WebClientResponseException ex) {
            // Handle HTTP errors (4xx and 5xx responses)
            System.err.println("API returned error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
            throw new RuntimeException("Error while fetching data from API", ex);
        } catch (Exception ex) {
            // Handle general exceptions
            System.err.println("An unexpected error occurred: " + ex.getMessage());
            throw new RuntimeException("Unexpected API error", ex);
        }
    }

    public String[] getCoordinatesInfo(String x, String y){
        String fullUrl = String.format(
                "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=%s&y=%s",
                x,y
        );

        responseMap  = webClient.get()
                .uri(fullUrl)
                .header("Authorization", "KakaoAK 3cd1edfe5e5c6d1b83dddc2703c0c18b")
                .retrieve()
                .bodyToMono(String.class)
                .map(jsonResponse ->{
                    try {
                        responseMap = objectMapper.readValue(jsonResponse, Map.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return responseMap;
                })
                .block();

        String district = (String)((List<Map<String, Object>>)responseMap.get("documents")).get(0).get("region_2depth_name");
        String city = (String)((List<Map<String, Object>>)responseMap.get("documents")).get(0).get("region_1depth_name");

        return new String[]{district, city};

    }

    public List<CommercialArea> getCommercialAreas(){
        final int TOTALCOUNT = 949;
        final int LIMIT = 100;
        final int NOPAGES = (int)Math.ceil((double)TOTALCOUNT / LIMIT);

        List<CommercialArea> commercialAreas = new ArrayList<>();
        for(int i = 0; i < NOPAGES; i++){
            commercialAreas.addAll(getPageHelper((i * LIMIT), LIMIT));
        }
        return commercialAreas;
    }

    private List<CommercialArea> getPageHelper(int offset, int limit){
        List<CommercialArea> commercialAreas = new ArrayList<>();
        String fullUrl = "https://apis.openapi.sk.com/puzzle/place/meta/areas?offset=" + offset + "&limit=" + limit;
        String jsonResponse = webClient.get()
                .uri(fullUrl)
                .header("Accept", "application/json")
                .header("appKey", "xOtzlSFCl18tJAgUJRwmZ3buqqQjj4yB6kdfcxFg")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        Map<String, Object> responseMap;
        try {
            // Use the objectMapper to parse the JSON string into our local map
            responseMap = objectMapper.readValue(jsonResponse, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse API response", e);
        }

        // 3. All variables are now local. They are created here and disappear when the method finishes.
        List<Object> contents = (List<Object>) responseMap.get("contents");
        for(Object json : contents){
            Map<String, Object> areaMap = (Map<String, Object>) json;
            long areaId = Long.parseLong((String) areaMap.get("areaId"));
            String areaName = (String) areaMap.get("areaName");

            //Getting city and district for the given area using KakaoAPI
            String fullUrl2 = String.format("https://dapi.kakao.com/v2/local/search/keyword.json?query=%s", areaName);
            String jsonResponse2 = encodingWebClient.get()
                    .uri("https://dapi.kakao.com/v2/local/search/keyword.json?query={query}", areaName)
                    .header("Authorization", "KakaoAK 3cd1edfe5e5c6d1b83dddc2703c0c18b")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            Map<String, Object> responseMap2;
            try{
                responseMap2 = objectMapper.readValue(jsonResponse2, Map.class);
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to parse API response", e);
            }

            Map<String, Object> meta = (Map<String, Object>)responseMap2.get("meta");
            String address;
            double longitude = 0;
            double latitude = 0;
            if (((Integer)meta.get("total_count")) == 0){
                address = (String) meta.get("selected_region");
            }
            else{
                address = (String) meta.get("selected_address");
                List<Object> documents = (List<Object>) responseMap2.get("documents");
                Map<String, Object> areaInfo = (Map<String, Object>) documents.get(0);
                address = (String) areaInfo.get("address_name");
                longitude = Double.parseDouble((String)areaInfo.get("x"));
                latitude = Double.parseDouble((String)areaInfo.get("y"));
            }



            if (address == null) {
                address = "null null";
            }
            final String[] splitAddress = address.split(" ");

            System.out.println(areaName + "Array length: " + splitAddress.length);
            System.out.println("Array content: ");
            for (String s : splitAddress) {
                System.out.println(s);
            }

            String city = splitAddress[0];

            String district = splitAddress[1];

            System.out.println(city + " " + district);

            commercialAreas.add(new CommercialArea(areaId, areaName, city, district, latitude, longitude));
        }
        return commercialAreas;
    }

    public Object getCongestionLevel(CommercialArea area){
        Map<String, List<Double>> congestionData = new HashMap<>();
        String fullUrl = String.format(
                "https://apis.openapi.sk.com/puzzle/place/congestion/stat/hourly/areas/%d",
                area.getAreaId()
        );
        final Map <String, Object> responseMap;

        final String jsonResponse  = webClient.get()
                .uri(fullUrl)
                .header("Accept", "application/json")
                .header("appKey", "xOtzlSFCl18tJAgUJRwmZ3buqqQjj4yB6kdfcxFg")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try{
            responseMap = objectMapper.readValue(jsonResponse, Map.class);
            Map<String, Object> contentsMap = (Map<String, Object>)responseMap.get("contents");
            List<Object> stat = (List<Object>) contentsMap.get("stat");
            System.out.println("Contents size: " + stat.size());

            for(int i  = 0; i < stat.size(); i++){
                Map<String, Object> statMap = (Map<String, Object>) stat.get(i);
                double congestion = (double)statMap.get("congestion");

                if(!congestionData.containsKey((String)statMap.get("dow"))){
                    congestionData.put((String)statMap.get("dow"), new ArrayList<>());
                }
                congestionData.get((String)statMap.get("dow")).add(congestion);
            }

            return congestionData;

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;


    }

    public Map<String, Double> getDemographics(String areaId) {
        Map<String, Double> demographicProfile = new HashMap<>();

        // 1. Define the granular age groups that work correctly.
        List<String> genders = List.of("male", "female");
        List<String> specificAgeGroups = List.of("0", "10", "20", "30", "40", "50", "60");

        // 2. Loop through the granular groups as before.
        for (String gender : genders) {
            for (String ageGroup : specificAgeGroups) {
                try {
                    double totalVisitors = fetchAndSumSegmentData(areaId, gender, ageGroup);
                    demographicProfile.put(gender + "_" + ageGroup, totalVisitors);
                } catch (Exception e) {
                    System.out.println("Failed to fetch data for gender " + gender + " and age group " + ageGroup);
                }
            }
        }

        // 3. Make one final call for the "70 and over" group for each gender.
        try {
            double male70Over = fetchAndSumSegmentData(areaId, "male", "70"); // API returns the combined group here
            demographicProfile.put("male_70_over", male70Over);

            double female70Over = fetchAndSumSegmentData(areaId, "female", "70"); // API returns the combined group here
            demographicProfile.put("female_70_over", female70Over);
        } catch (Exception e) {
            System.out.println("Failed to fetch data for gender 70 and over");
        }

        return demographicProfile;
    }

    /**
     * A private helper method to fetch data for one specific demographic segment.
     */
    private double fetchAndSumSegmentData(String areaId, String gender, String ageGroup) {
        String fullUrl = String.format(
                "https://apis.openapi.sk.com/puzzle/place/visit/count/raw/daily/areas/%s?gender=%s&ageGrp=%s&range=30days",
                areaId, gender, ageGroup
        );

        // This WebClient call now uses our new, perfectly nested DTOs
        VisitorApiResponse response = webClient.get()
                .uri(fullUrl)
                .header("Accept", "application/json")
                .header("appKey", "xOtzlSFCl18tJAgUJRwmZ3buqqQjj4yB6kdfcxFg") // Remember to move this!
                .retrieve()
                .bodyToMono(VisitorApiResponse.class)
                .block();

        // The FIX: A clean, direct, and type-safe path to your data!
        if (response == null || response.getContents() == null || response.getContents().getRaw() == null) {
            return 0.0;
        }

        // No more manual parsing! The list is already a perfect List<DailyVisitorData>.
        return response.getContents().getRaw().stream()
                .mapToDouble(DailyVisitorData::getApproxVisitorCount)
                .sum();
    }

    public Object getBusinessCount(String longitude, String latitude, int radius){
        int totalCount = 0;
        Map<String, Integer> categoryCounts = new HashMap<>();
        String[] categoryCodes = {"MT1", "CS2", "AC5", "OL7", "BK9", "AG2", "AD5", "FD6", "CE7", "HP8", "PM9"};

        Map<String, String> helperMap = new HashMap<>();
        helperMap.put("MT1", "대형마트");
        helperMap.put("CS2", "편의점");
        helperMap.put("AC5", "학원");
        helperMap.put("OL7", "주유소, 충전소");
        helperMap.put("BK9", "은행");
        helperMap.put("AG2", "중개업소");
        helperMap.put("AD5", "숙박");
        helperMap.put("FD6", "음식점");
        helperMap.put("CE7", "카페");
        helperMap.put("HP8", "병원");
        helperMap.put("PM9", "약국");

        for(String categoryCode : categoryCodes){
            KakaoApiResponseDTO tmp = (KakaoApiResponseDTO) getBusinessCategoryCount(longitude, latitude, radius, categoryCode);
            if (tmp != null){
                totalCount += tmp.getMeta().getTotalCount();
                categoryCounts.put(helperMap.get(categoryCode), tmp.getMeta().getTotalCount());
            }
        }

        return new Object[]{totalCount, categoryCounts};

    }

    private Object getBusinessCategoryCount(String longitude, String latitude, int radius, String category){
        String fullUrl = String.format(
                "https://dapi.kakao.com/v2/local/search/category.json?category_group_code=%s&x=%s&y=%s&radius=%d",
                category, longitude, latitude, radius
        );

        KakaoApiResponseDTO response = webClient.get()
                .uri(fullUrl)
                .header("Authorization", "KakaoAK 3cd1edfe5e5c6d1b83dddc2703c0c18b") // Remember to move this!
                .retrieve()
                .bodyToMono(KakaoApiResponseDTO.class)
                .block();

        System.out.println(response.getMeta().getTotalCount() + " " + category);

        return response;
    }
}
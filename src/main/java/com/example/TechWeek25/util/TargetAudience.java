package com.example.TechWeek25.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TargetAudience {

    public static final Map<String, List<String>> PROFILES = new HashMap<>();

    static {
        // 대형마트 (Large Mart): Families and primary shoppers
        PROFILES.put("대형마트", Arrays.asList("female_30", "male_30", "female_40", "male_40"));

        // 편의점 (Convenience Store): Younger individuals, students, singles
        PROFILES.put("편의점", Arrays.asList("female_10", "male_10", "female_20", "male_20", "female_30", "male_30"));

        // 학원 (Academy): Students are the primary users of the space
        PROFILES.put("학원", Arrays.asList("female_10", "male_10"));

        // 주유소, 충전소 (Gas/Charging Station): Car-owning adults
        PROFILES.put("주유소, 충전소", Arrays.asList("male_30", "male_40", "male_50", "female_30", "female_40"));

        // 은행 (Bank): Adults in their prime earning and borrowing years
        PROFILES.put("은행", Arrays.asList("female_30", "male_30", "female_40", "male_40", "female_50", "male_50"));

        // 중개업소 (Real Estate Agency): People actively looking to move
        PROFILES.put("중개업소", Arrays.asList("female_20", "male_20", "female_30", "male_30", "female_40"));

        // 숙박 (Accommodation): Broad range of adults, travelers
        PROFILES.put("숙박", Arrays.asList("female_20", "male_20", "female_30", "male_30", "female_40", "male_40"));

        // 음식점 (Restaurant): General dining crowd
        PROFILES.put("음식점", Arrays.asList("female_20", "male_20", "female_30", "male_30", "female_40", "male_40"));

        // 카페 (Cafe): Heavily dominated by younger demographics
        PROFILES.put("카페", Arrays.asList("female_10", "female_20", "male_20", "female_30", "male_30"));

        // 병원 (Hospital): All ages, but high frequency in older adults and young families
        PROFILES.put("병원", Arrays.asList("female_0", "male_0", "female_60", "male_60", "female_70_over", "male_70_over"));

        // 약국 (Pharmacy): Correlates with hospital visits, older demographics, and parents
        PROFILES.put("약국", Arrays.asList("female_30", "female_40", "female_50", "female_60", "male_60", "female_70_over", "male_70_over"));
    }
}
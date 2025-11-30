package com.example.TechWeek25.repository;

import com.example.TechWeek25.entity.RealEstateTrend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RealEstateTrendRepository extends JpaRepository<RealEstateTrend, Long> {

    List<RealEstateTrend> findByDistrictAndPropertyTypeOrderByYearMonthAsc(String district, String propertyType);

}


package com.example.TechWeek25.repository;


import com.example.TechWeek25.entity.CommercialArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<CommercialArea, Long> {
    public Optional<List<CommercialArea>> findByAreaName(String areaName);
    public Optional<List<CommercialArea>> findByDistrict(String district);
    public Optional<List<CommercialArea>> findByCity(String city);
}
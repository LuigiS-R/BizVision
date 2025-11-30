package com.example.TechWeek25.controller;

import com.example.TechWeek25.dto.CompetitionData;
import com.example.TechWeek25.dto.QueryInput;
import com.example.TechWeek25.service.AnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class AnalysisController {
    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/locations/analysis")
    public ResponseEntity<CompetitionData> getLocationAnalysis(@RequestBody QueryInput location){
        CompetitionData response = (CompetitionData)analysisService.analyze(location);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

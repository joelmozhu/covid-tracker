package com.development.covidtracker.controllers;

import com.development.covidtracker.models.LocationInfo;
import com.development.covidtracker.services.CovidDataService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CovidController {

    private CovidDataService covidDataService;

    @Autowired
    public CovidController(CovidDataService covidDataService) {
        this.covidDataService = covidDataService;
    }

    @GetMapping("/")
    public String covid(Model model) {
        List<LocationInfo> allInfo = covidDataService.getAllinfo();
        int totalReportedCases = allInfo.stream().mapToInt(info -> info.getLatestTotalCases()).sum();
        int totalNewCases = allInfo.stream().mapToInt(info -> info.getDiffFromPrevDay()).sum();

        model.addAttribute("locationInfo", allInfo);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);

        return "home";
    }
}

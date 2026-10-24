package com.example.backend_spring.controller;

import com.example.backend_spring.dto.DailyReportDto;
import com.example.backend_spring.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/reports")
public class AdminReportController {

    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // GET /api/admin/reports/daily?date=2026-11-10 (bez daty = dziś)
    @GetMapping("/daily")
    public DailyReportDto daily(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reportService.daily(date != null ? date : LocalDate.now());
    }
}

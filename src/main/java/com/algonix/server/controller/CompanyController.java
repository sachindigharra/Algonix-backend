package com.algonix.server.controller;

import com.algonix.server.dto.response.CompanyResponse;
import com.algonix.server.service.CompanyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(@RequestBody CompanyResponse request) {
        return ResponseEntity.ok(companyService.createCompany(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable UUID id,
            @RequestBody CompanyResponse request) {
        return ResponseEntity.ok(companyService.updateCompany(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}

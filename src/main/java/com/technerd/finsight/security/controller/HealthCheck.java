package com.technerd.finsight.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check")
@Tag(name = "Health", description = "Service health status.")
public class HealthCheck {

    @GetMapping
    @Operation(summary = "Check service health", description = "Returns a plain-text OK response when the service is available.")
    public String healthCheck() {
        return "OK";
    }
}

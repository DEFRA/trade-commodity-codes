package uk.gov.defra.cdp.trade.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCode;
import uk.gov.defra.cdp.trade.demo.service.CommodityCodeService;

import java.util.List;

/**
 * REST Controller for Commodity Code operations using PostgreSQL.
 *
 * Demonstrates:
 * - RESTful API design with proper HTTP methods
 * - OpenAPI/Swagger documentation
 * - Request validation with @Valid
 * - Proper HTTP status codes
 */
@RestController
@RequestMapping("/commodity-codes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Commodity Codes", description = "Operations for managing trade commodity codes")
public class CommodityCodeController {

    private final CommodityCodeService commodityCodeService;

    @GetMapping
    @Operation(summary = "Get all commodity codes", description = "Retrieve a list of all commodity codes")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved commodity codes")
    public ResponseEntity<List<CommodityCode>> getAllCommodityCodes() {
        log.info("GET /commodity-codes - Retrieving all commodity codes");
        List<CommodityCode> commodityCodes = commodityCodeService.findAll();
        return ResponseEntity.ok(commodityCodes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get commodity code by ID", description = "Retrieve a specific commodity code by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved commodity code"),
        @ApiResponse(responseCode = "404", description = "Commodity code not found")
    })
    public ResponseEntity<CommodityCode> getCommodityCodeById(
            @Parameter(description = "Commodity code ID") @PathVariable Long id) {
        log.info("GET /commodity-codes/{} - Retrieving commodity code by ID", id);
        CommodityCode commodityCode = commodityCodeService.findById(id);
        return ResponseEntity.ok(commodityCode);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get commodity code by code", description = "Retrieve a specific commodity code by its code value")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved commodity code"),
        @ApiResponse(responseCode = "404", description = "Commodity code not found")
    })
    public ResponseEntity<CommodityCode> getCommodityCodeByCode(
            @Parameter(description = "Commodity code value") @PathVariable String code) {
        log.info("GET /commodity-codes/code/{} - Retrieving commodity code by code", code);
        CommodityCode commodityCode = commodityCodeService.findByCode(code);
        return ResponseEntity.ok(commodityCode);
    }

    @PostMapping
    @Operation(summary = "Create commodity code", description = "Create a new commodity code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created commodity code"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Commodity code already exists")
    })
    public ResponseEntity<CommodityCode> createCommodityCode(
            @Parameter(description = "Commodity code to create") @Valid @RequestBody CommodityCode commodityCode) {
        log.info("POST /commodity-codes - Creating commodity code: {}", commodityCode.getCode());
        CommodityCode created = commodityCodeService.create(commodityCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update commodity code", description = "Update an existing commodity code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated commodity code"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Commodity code not found"),
        @ApiResponse(responseCode = "409", description = "Commodity code with new code already exists")
    })
    public ResponseEntity<CommodityCode> updateCommodityCode(
            @Parameter(description = "Commodity code ID") @PathVariable Long id,
            @Parameter(description = "Updated commodity code") @Valid @RequestBody CommodityCode commodityCode) {
        log.info("PUT /commodity-codes/{} - Updating commodity code", id);
        CommodityCode updated = commodityCodeService.update(id, commodityCode);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete commodity code", description = "Delete a commodity code by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted commodity code"),
        @ApiResponse(responseCode = "404", description = "Commodity code not found")
    })
    public ResponseEntity<Void> deleteCommodityCode(
            @Parameter(description = "Commodity code ID") @PathVariable Long id) {
        log.info("DELETE /commodity-codes/{} - Deleting commodity code", id);
        commodityCodeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search commodity codes", description = "Search commodity codes by description")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching commodity codes")
    public ResponseEntity<List<CommodityCode>> searchCommodityCodes(
            @Parameter(description = "Search term for description") @RequestParam String q) {
        log.info("GET /commodity-codes/search?q={} - Searching commodity codes", q);
        List<CommodityCode> results = commodityCodeService.searchByDescription(q);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/prefix/{prefix}")
    @Operation(summary = "Find by code prefix", description = "Find commodity codes starting with the given prefix")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching commodity codes")
    public ResponseEntity<List<CommodityCode>> findByCodePrefix(
            @Parameter(description = "Code prefix") @PathVariable String prefix) {
        log.info("GET /commodity-codes/prefix/{} - Finding commodity codes by prefix", prefix);
        List<CommodityCode> results = commodityCodeService.findByCodePrefix(prefix);
        return ResponseEntity.ok(results);
    }
}
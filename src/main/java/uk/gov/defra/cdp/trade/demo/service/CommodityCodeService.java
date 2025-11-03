package uk.gov.defra.cdp.trade.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCode;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityCodeRepository;
import uk.gov.defra.cdp.trade.demo.exceptions.ConflictException;
import uk.gov.defra.cdp.trade.demo.exceptions.NotFoundException;

import java.util.List;

/**
 * Service class for managing Commodity Codes with PostgreSQL persistence.
 *
 * Demonstrates:
 * - Transaction management with @Transactional
 * - Business logic layer separation
 * - Exception handling for domain operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommodityCodeService {

    private final CommodityCodeRepository commodityCodeRepository;

    @Transactional(readOnly = true)
    public List<CommodityCode> findAll() {
        log.debug("Finding all commodity codes");
        return commodityCodeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CommodityCode findById(Long id) {
        log.debug("Finding commodity code by id: {}", id);
        return commodityCodeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commodity code not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public CommodityCode findByCode(String code) {
        log.debug("Finding commodity code by code: {}", code);
        return commodityCodeRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Commodity code not found with code: " + code));
    }

    @Transactional
    public CommodityCode create(CommodityCode commodityCode) {
        log.debug("Creating commodity code: {}", commodityCode.getCode());
        
        if (commodityCodeRepository.existsByCode(commodityCode.getCode())) {
            throw new ConflictException("Commodity code already exists with code: " + commodityCode.getCode());
        }
        
        return commodityCodeRepository.save(commodityCode);
    }

    @Transactional
    public CommodityCode update(Long id, CommodityCode commodityCode) {
        log.debug("Updating commodity code with id: {}", id);
        
        CommodityCode existing = findById(id);
        
        // Check if code is being changed and if new code already exists
        if (!existing.getCode().equals(commodityCode.getCode()) && 
            commodityCodeRepository.existsByCode(commodityCode.getCode())) {
            throw new ConflictException("Commodity code already exists with code: " + commodityCode.getCode());
        }
        
        existing.setCode(commodityCode.getCode());
        existing.setDescription(commodityCode.getDescription());
        
        return commodityCodeRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Deleting commodity code with id: {}", id);
        
        if (!commodityCodeRepository.existsById(id)) {
            throw new NotFoundException("Commodity code not found with id: " + id);
        }
        
        commodityCodeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CommodityCode> searchByDescription(String searchTerm) {
        log.debug("Searching commodity codes by description: {}", searchTerm);
        return commodityCodeRepository.findByDescriptionContaining(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<CommodityCode> findByCodePrefix(String codePrefix) {
        log.debug("Finding commodity codes by code prefix: {}", codePrefix);
        return commodityCodeRepository.findByCodeStartingWith(codePrefix);
    }
}
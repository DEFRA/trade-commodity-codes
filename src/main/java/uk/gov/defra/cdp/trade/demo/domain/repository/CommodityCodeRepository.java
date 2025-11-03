package uk.gov.defra.cdp.trade.demo.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCode;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CommodityCode entity using PostgreSQL.
 * 
 * Demonstrates:
 * - JpaRepository for standard CRUD operations
 * - Custom query methods
 * - Named queries with @Query annotation
 */
@Repository
public interface CommodityCodeRepository extends JpaRepository<CommodityCode, Long> {

    /**
     * Find commodity code by its unique code value.
     */
    Optional<CommodityCode> findByCode(String code);

    /**
     * Check if a commodity code exists by its code value.
     */
    boolean existsByCode(String code);

    /**
     * Find all commodity codes containing the specified text in description.
     */
    @Query("SELECT c FROM CommodityCode c WHERE LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<CommodityCode> findByDescriptionContaining(@Param("searchTerm") String searchTerm);

    /**
     * Find commodity codes that start with the given code prefix.
     */
    List<CommodityCode> findByCodeStartingWith(String codePrefix);
}
package uk.gov.defra.cdp.trade.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Commodity Code entity demonstrating PostgreSQL persistence with CDP compliance.
 *
 * This entity showcases:
 * - JPA entity mapping with @Entity
 * - Unique constraint with @Column(unique = true)
 * - Bean Validation with @NotBlank
 * - Automatic ID generation with @GeneratedValue
 */
@Entity
@Table(name = "commodity_codes")
@Data
@NoArgsConstructor
public class CommodityCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    @NotBlank(message = "Code is required")
    private String code;

    @Column(nullable = false, length = 500)
    @NotBlank(message = "Description is required")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public CommodityCode(String code, String description) {
        this.code = code;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
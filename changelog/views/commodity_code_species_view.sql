SELECT DISTINCT traces_commodity_code             AS code,
                (SELECT CASE
                            WHEN RIGHT(sorting_key, 2) = '80' THEN RIGHT(traces_commodity_code, 2)
                            ELSE '' END)          AS display_code,
                traces_commodity_code_description AS description,
                (SELECT CASE
                            WHEN certificate.short_description = 'CVEDA' THEN 'CVED-A'
                            WHEN certificate.short_description = 'CVEDP' THEN 'CVED-P'
                            WHEN certificate.short_description = 'CED' THEN 'CED'
                            WHEN certificate.short_description = 'EUIA' THEN 'IMP'
                            WHEN certificate.short_description = 'CHED-PP' THEN 'CHED-PP'
                            END)                  AS cert_type,
                requirement.traces_parent_code    AS immediate_parent,
                CAST(
                        CASE
                            WHEN RIGHT(sorting_key, 2) = '80' AND requirement.is_selectable = true
                                THEN 1
                            ELSE 0 END
                    AS BOOLEAN)                   AS is_commodity,
                CAST(
                        CASE
                            WHEN EXISTS (SELECT *
                                         FROM certification_requirement cr
                                         WHERE cr.traces_parent_code = traces_commodity_code
                                           AND cr.certification_requirement_code =
                                               requirement.certification_requirement_code) THEN 1
                            ELSE 0 END
                    AS BOOLEAN)                   AS is_parent,
                cert_nom.species_name             AS species
FROM commodity_nomenclature
         INNER JOIN certification_requirement requirement
                    ON requirement.commodity_nomenclature_id_code = commodity_nomenclature.code
         INNER JOIN certificates certificate
                    ON certificate.code = requirement.certification_requirement_code
         INNER JOIN certification_nomenclature cert_nom
                    ON cert_nom.certification_requirement_code = requirement.code
WHERE requirement.is_traces_visible = true;
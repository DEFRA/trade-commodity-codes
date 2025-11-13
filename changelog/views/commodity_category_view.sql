SELECT (SELECT CASE
                   WHEN certificate.short_description = 'CVEDA'
                       THEN 'cveda'
                   WHEN certificate.short_description = 'CVEDP'
                       THEN 'cvedp'
                   WHEN certificate.short_description = 'CED'
                       THEN 'ced'
                   WHEN certificate.short_description = 'EUIA'
                       THEN 'imp'
                   WHEN certificate.short_description = 'CHED-PP'
                       THEN 'chedpp'
                   END)                        AS certificate_type,
       traces_commodity_code                   AS commodity_code,
       (SELECT fn_commodity_category_data(
                       requirement.code,
                       certificate.code,
                       traces_commodity_code)) AS data
FROM commodity_nomenclature
         INNER JOIN certification_requirement requirement
                    ON
                        requirement.commodity_nomenclature_id_code =
                        commodity_nomenclature.code
         INNER JOIN certificates certificate
                    ON
                        certificate.code =
                        requirement.certification_requirement_code;
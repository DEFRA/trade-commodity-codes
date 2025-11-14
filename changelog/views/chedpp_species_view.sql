SELECT com_nom.traces_commodity_code             AS commodity_code,
       cert_nom.species_name,
       species.eppo_code,
       cert_nom.species_id,
       com_nom.traces_commodity_code_description AS commodity_description
FROM certification_nomenclature cert_nom
         LEFT JOIN certification_requirement cert_req
                   ON cert_req.code = cert_nom.certification_requirement_code
         LEFT JOIN species
                   ON species.code = cert_nom.species_code
         LEFT JOIN commodity_nomenclature com_nom
                   ON com_nom.code = cert_req.commodity_nomenclature_id_code
WHERE cert_nom.species_code IS NOT NULL
  AND cert_req.certification_requirement_code = '851';
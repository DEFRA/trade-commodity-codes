CREATE OR REPLACE FUNCTION fn_commodity_category_data(
        input_certification_requirement_code VARCHAR(250),
        certification_type VARCHAR(250),
        traces_commodity_code VARCHAR(250))
        RETURNS TEXT AS $$
        DECLARE
            data TEXT;
            types_json TEXT;
            classes_json TEXT;
            families_json TEXT;
            models_json TEXT;
            species_json TEXT;
            invasive_species_json TEXT;
        BEGIN
        -- drop temporary tables if they exist from previous calls
        DROP TABLE IF EXISTS types_temp;
        DROP TABLE IF EXISTS classes_temp;
        DROP TABLE IF EXISTS families_temp;
        DROP TABLE IF EXISTS models_temp;
        DROP TABLE IF EXISTS species_temp;
        DROP TABLE IF EXISTS invasive_species_temp;
        DROP TABLE IF EXISTS normalised_certification_nomenclature;

        -- create temporary tables
        CREATE TEMP TABLE types_temp (
            text      VARCHAR(250),
            value     VARCHAR(250),
            isDefault VARCHAR(10)
        ) ON COMMIT DROP;

        CREATE TEMP TABLE classes_temp (
            text      VARCHAR(250),
            value     VARCHAR(250),
            type      VARCHAR(250),
            isDefault VARCHAR(10)
        ) ON COMMIT DROP;

        CREATE TEMP TABLE families_temp (
            text      VARCHAR(250),
            value     VARCHAR(250),
            clazz     VARCHAR(250),
            isDefault VARCHAR(10)
        ) ON COMMIT DROP;

        CREATE TEMP TABLE models_temp (
            text      VARCHAR(250),
            value     VARCHAR(250),
            family    VARCHAR(250),
            isDefault VARCHAR(10),
            tcfPermutation TEXT UNIQUE
        ) ON COMMIT DROP;

        CREATE TEMP TABLE species_temp (
            text      VARCHAR(250),
            eppoCode  VARCHAR(10),
            value     VARCHAR(250),
            model     VARCHAR(250),
            isDefault VARCHAR(10)
        ) ON COMMIT DROP;

        CREATE TEMP TABLE invasive_species_temp (
            species    VARCHAR(250),
            isInvasive VARCHAR(5)
        ) ON COMMIT DROP;

        CREATE TEMP TABLE normalised_certification_nomenclature (
            certification_requirement_code  VARCHAR(250),
            commodity_type_name             VARCHAR(250),
            commodity_type_id               VARCHAR(250),
            is_default_commodity_type       VARCHAR(250),
            class_name                      VARCHAR(250),
            class_id                        VARCHAR(250),
            is_default_class                VARCHAR(250),
            family_name                     VARCHAR(250),
            family_id                       VARCHAR(250),
            is_default_family               VARCHAR(250),
            species_code                    VARCHAR(250),
            species_name                    VARCHAR(250),
            species_id                      VARCHAR(250),
            is_default_species              VARCHAR(250),
            complement_id                   VARCHAR(250)
        ) ON COMMIT DROP;

        INSERT INTO normalised_certification_nomenclature (
            certification_requirement_code,
            commodity_type_name,
            commodity_type_id,
            is_default_commodity_type,
            class_name,
            class_id,
            is_default_class,
            family_name,
            family_id,
            is_default_family,
            species_code,
            species_name,
            species_id,
            is_default_species,
            complement_id)
        SELECT
            cn.certification_requirement_code,
            (CASE
                WHEN cn.commodity_type_name='Blank/Empty' THEN ''
                WHEN cn.commodity_type_name IS NULL THEN ''
                ELSE cn.commodity_type_name
            END),
            cn.commodity_type_id,
            COALESCE(CAST(cn.is_default_commodity_type AS VARCHAR), '0'),
            (CASE
                WHEN cn.class_name='Blank/Empty' THEN ''
                WHEN cn.class_name IS NULL THEN ''
                ELSE cn.class_name
            END),
            (CASE WHEN cn.class_id IS NULL THEN cn.complement_id ELSE cn.class_id END),
            COALESCE(CAST(cn.is_default_class AS VARCHAR), '0'),
            (CASE
                WHEN cn.family_name='Blank/Empty' THEN ''
                WHEN cn.family_name IS NULL THEN ''
                ELSE cn.family_name
            END),
            (CASE WHEN cn.family_id IS NULL THEN cn.complement_id ELSE cn.family_id END),
            COALESCE(CAST(cn.is_default_family AS VARCHAR), '0'),
            cn.species_code,
            (CASE
                WHEN cn.species_name='Blank/Empty' THEN ''
                WHEN cn.species_name IS NULL THEN ''
                ELSE cn.species_name
            END),
            (CASE WHEN (cn.species_id IS NULL AND cn.family_id IS NULL and cn.class_id IS NULL) THEN cn.species_id ELSE cn.complement_id END),
            COALESCE(CAST(cn.is_default_species AS VARCHAR), '0'),
            cn.complement_id
        FROM certification_nomenclature cn
        WHERE input_certification_requirement_code = cn.certification_requirement_code
          AND ((certification_type = '851')
            OR (certification_type != '851' AND cn.commodity_type_id IS NOT NULL));

        -- get all unique types for field commodity_type_name then insert them into the temporary Type table
        WITH unique_types AS (
            SELECT DISTINCT commodity_type_name,
                commodity_type_id,
                is_default_commodity_type,
                ROW_NUMBER() OVER (PARTITION BY commodity_type_name ORDER BY commodity_type_name) AS rn
            FROM normalised_certification_nomenclature
        )
        INSERT INTO types_temp(text, value, isDefault)
        SELECT DISTINCT commodity_type_name, commodity_type_id, is_default_commodity_type
        FROM unique_types
        WHERE rn = 1;

        -- get all unique classes for fields class_id and commodity_type_id then insert them into the temporary Class table
        WITH unique_classes AS (
            SELECT DISTINCT class_name,
                class_id,
                commodity_type_id,
                is_default_class,
                ROW_NUMBER() OVER (PARTITION BY class_name, commodity_type_name ORDER BY class_name, commodity_type_name) AS rn
            FROM normalised_certification_nomenclature
        )
        INSERT INTO classes_temp(text, value, type, isDefault)
        SELECT DISTINCT class_name, class_id, commodity_type_id, is_default_class
        FROM unique_classes
        WHERE rn = 1;

        -- get all unique families for fields family_name, class_name, commodity_type_name then insert them into the temporary Family table
        WITH unique_families AS (
            SELECT DISTINCT family_name,
                family_id,
                complement_id,
                class_id,
                is_default_family,
                ROW_NUMBER() OVER (PARTITION BY family_name, class_name, commodity_type_name ORDER BY family_name, class_name, commodity_type_name) AS rn
            FROM normalised_certification_nomenclature
        )
        INSERT INTO families_temp(text, value, clazz, isDefault)
        SELECT DISTINCT family_name, family_id, class_id, is_default_family
        FROM unique_families
        WHERE rn = 1;

        -- get all unique models for fields family_name, class_name, commodity_type_name then insert them into the temporary Model table
        WITH unique_models AS (
            SELECT DISTINCT complement_id,
                family_id,
                commodity_type_name,
                class_name,
                family_name,
                ROW_NUMBER() OVER (PARTITION BY family_name, class_name, commodity_type_name ORDER BY complement_id, family_id) AS rn
            FROM normalised_certification_nomenclature
        )
        INSERT INTO models_temp(text, value, family, isDefault, tcfPermutation)
        SELECT '', complement_id, family_id, '0', CONCAT(commodity_type_name, '-', class_name, '-', family_name)
        FROM unique_models
        WHERE rn = 1
        ON CONFLICT (tcfPermutation) DO NOTHING;

        -- get all unique species for fields species_id, complement_id then insert them into the temporary species table
        WITH unique_species AS (
            SELECT DISTINCT species_name,
                species.eppo_code AS eppoCode,
                species_id,
                commodity_type_name,
                class_name,
                family_name,
                is_default_species,
                ROW_NUMBER() OVER (PARTITION BY species_id, complement_id ORDER BY species_id) AS rn
            FROM normalised_certification_nomenclature
            FULL OUTER JOIN species ON species.code = species_code
        )
        INSERT INTO species_temp(text, eppoCode, value, model, isDefault)
        SELECT DISTINCT species_name,
            eppoCode,
            species_id,
            (SELECT value
             FROM models_temp
             WHERE tcfPermutation = CONCAT(commodity_type_name, '-', class_name, '-', family_name)),
            is_default_species
        FROM unique_species
        WHERE rn = 1
          AND species_id IS NOT NULL;

        -- get all invasive species for field species_id then insert them into the temporary invasiveSpecies table
        WITH unique_invasive_species AS (
            SELECT species_id,
                invasive_species_indicator,
                ROW_NUMBER() OVER (PARTITION BY species_id ORDER BY species_id) AS rn
            FROM normalised_certification_nomenclature
            INNER JOIN species ON species.code = species_code
        )
        INSERT INTO invasive_species_temp(species, isInvasive)
        SELECT species_id,
            (CASE WHEN invasive_species_indicator = true THEN 'true' ELSE 'false' END) as invasive_species_indicator
        FROM unique_invasive_species
        WHERE rn = 1 AND species_id IS NOT NULL;

        -- construct json output from each temporary tables and cater for when we have no data
        SELECT COALESCE(
            json_agg(json_build_object('text', text, 'value', value, 'isDefault', isDefault) ORDER BY text)::TEXT,
            '[]'
        ) INTO types_json FROM types_temp;

        SELECT COALESCE(
            json_agg(json_build_object('text', text, 'value', value, 'type', type, 'isDefault', isDefault) ORDER BY text)::TEXT,
            '[]'
        ) INTO classes_json FROM classes_temp;

        SELECT COALESCE(
            json_agg(json_build_object('text', text, 'value', value, 'clazz', clazz, 'isDefault', isDefault) ORDER BY text)::TEXT,
            '[]'
        ) INTO families_json FROM families_temp;

        SELECT COALESCE(
            json_agg(json_build_object('text', text, 'value', value, 'family', family, 'isDefault', isDefault) ORDER BY CAST(value AS INTEGER))::TEXT,
            '[]'
        ) INTO models_json FROM models_temp;

        SELECT COALESCE(
            json_agg(json_build_object('text', text, 'eppoCode', eppoCode, 'value', value, 'model', model, 'isDefault', isDefault) ORDER BY text)::TEXT,
            '[]'
        ) INTO species_json FROM species_temp;

        SELECT COALESCE(
            json_agg(json_build_object('species', species, 'isInvasive', isInvasive) ORDER BY CAST(species AS INTEGER))::TEXT,
            '[]'
        ) INTO invasive_species_json FROM invasive_species_temp;

        -- Construct final JSON
        data := json_build_object(
            'types', types_json::json,
            'classes', classes_json::json,
            'families', families_json::json,
            'models', models_json::json,
            'species', species_json::json,
            'invasiveSpecies', invasive_species_json::json
        )::TEXT;

        -- if we have no data then set some pseudo data
        IF data = '{"types":[],"classes":[],"families":[],"models":[],"species":[],"invasiveSpecies":[]}'
        THEN
            data := json_build_object(
                'types', json_build_array(json_build_object('text', '', 'value', traces_commodity_code, 'isDefault', '0')),
                'classes', json_build_array(json_build_object('text', '', 'value', traces_commodity_code, 'type', traces_commodity_code, 'isDefault', '0')),
                'families', json_build_array(json_build_object('text', '', 'value', traces_commodity_code, 'clazz', traces_commodity_code, 'isDefault', '0')),
                'models', json_build_array(json_build_object('text', '', 'value', traces_commodity_code, 'family', traces_commodity_code, 'isDefault', '0')),
                'species', json_build_array(),
                'invasiveSpecies', json_build_array()
            )::TEXT;
        END IF;

        RETURN data;
        END;
$$ LANGUAGE plpgsql;

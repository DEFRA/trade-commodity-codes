# PostgreSQL Liquibase Database Changelogs

This directory contains Liquibase XML changelog files for managing the commodity code database schema in PostgreSQL. These changelogs provide a complete database schema for the Trade Commodity Codes application.

## Files Overview

### Core Changelog Files

1. **`db.changelog.xml`** - Main entry point that includes all other changelogs in the correct order
2. **`changes/00-generated-commoditycode-tables-baseline.xml`** - All table structures, constraints, and indexes
3. **`functions/functions.xml`** - Includes function changelogs
4. **`functions/00-generated-commoditycode-functions-baseline.xml`** - Function definitions
5. **`views/views.xml`** - Includes view changelogs
6. **`views/00-generated-commoditycode-views-baseline.xml`** - View definitions

### Execution Order

The changelogs are executed in this specific order (handled automatically by `db.changelog.xml`):

```
1. Tables & Constraints    (changes/00-generated-commoditycode-tables-baseline.xml)
2. Functions               (functions/functions.xml)
3. Views                   (views/views.xml)
```

## Quick Start

### Prerequisites

- PostgreSQL 12+ installed and running
- Liquibase 4.0+ installed
- Database connection configured
- Target database created

### Basic Execution

```bash
# Create target database
createdb trade-commodity-codes

# Run complete migration
liquibase --changeLogFile=changelog/db.changelog.xml update

# Verify migration
liquibase --changeLogFile=changelog/db.changelog.xml status
```

## Database Schema Overview

### Core Tables (14 total)

| Table | Description | Key Features |
|-------|-------------|--------------|
| `article_72_commodities` | Article 72 specific items | Regulatory compliance tracking |
| `certificates` | Certificate types and descriptions | Primary reference for cert types |
| `certification_nomenclature` | Species-specific cert requirements | Extended classification hierarchy |
| `certification_requirement` | Links commodities to certificates | Visibility and selectability controls |
| `commodity_attributes` | Extended commodity attributes | Effective date management, propagation tracking |
| `commodity_class` | Commodity classifications | Regulatory categorization |
| `commodity_configuration` | Configuration settings | Test/trial requirements |
| `commodity_eppo_variety` | EPPO codes and varieties | Plant variety specifications |
| `commodity_group` | Business commodity groupings | Soft delete enabled |
| `commodity_group_commodity` | Individual group members | Links to main commodity data |
| `commodity_nomenclature` | Core commodity hierarchy | TRACES codes, parent-child relationships |
| `hmi_marketing` | HMI marketing standards | Certificate validity periods |
| `inspection_responsibility` | Inspection assignments | By commodity and intended use |
| `species` | Species reference data | EPPO codes, invasive species flags |

### Views (6 total)

| View | Description | Use Case |
|------|-------------|----------|
| `v_chedp_species` | CHED-P species data | Extended classification |
| `v_chedpp_species` | CHED-PP species data | Regulatory compliance |
| `v_commodity_category` | Category data with JSON | API responses |
| `v_commodity_code` | Main commodity view | Application frontend |
| `v_commodity_code_species` | Enhanced commodity+species | Comprehensive data access |
| `v_commodity_group` | Active commodity groups | Business reporting |

### Functions (1 total)

| Function | Description | Use Case |
|----------|-------------|----------|
| `fn_commodity_category_data` | Generates commodity category data | Data transformation for API responses |

## Key PostgreSQL Adaptations

### Data Type Conversions

| SQL Server | PostgreSQL               | Notes |
|------------|--------------------------|-------|
| `NVARCHAR(n)` | `VARCHAR(n)`             | Unicode support maintained |
| `NVARCHAR(MAX)` | `TEXT` or `VARCHAR(4000)` | Unlimited length |
| `BIT` | `BOOLEAN`                | True/false values |
| `BIGINT IDENTITY(1,1)` | `BIGSERIAL`              | Auto-incrementing |
| `DATETIME` | `TIMESTAMP`              | Timezone-aware options available |

### Function Adaptations

- **JSON functions**: Utilizing PostgreSQL native JSON support
- **String manipulation**: Adapted to PostgreSQL syntax
- **Date/time handling**: Using PostgreSQL timestamp functions

### Performance Optimizations

- **Indexes**: Optimized for PostgreSQL query planner
- **Auto-increment columns**: Using `BIGSERIAL` and `SERIAL` for efficient ID generation
- **Effective date tracking**: Most tables include `effective_from` and `effective_to` columns for temporal queries

## Development Environment

### Docker Compose Setup

The changelogs are automatically executed in the development environment via Docker Compose. Check your `compose.yml` file for the specific Liquibase configuration.

### Local Development

```bash
# Start complete development stack (if using Docker Compose)
docker compose up

# This will:
# 1. Start PostgreSQL
# 2. Run Liquibase migrations automatically
# 3. Start the application services
```

## Database Management

### Schema Updates

When making schema changes:

1. Create new changelog files in the appropriate directory:
   - Tables: `changes/`
   - Functions: `functions/`
   - Views: `views/`
2. Add references to the parent XML file (e.g., `db.changelog.xml`, `functions/functions.xml`, or `views/views.xml`)
3. Test migrations with a clean database
4. Commit changes to version control

### Available Functions

```sql
-- Generate commodity category data
SELECT fn_commodity_category_data(/* parameters */);
```

## Troubleshooting

### Common Issues

1. **View Dependency Issues**
   ```sql
   -- Check view definitions
   SELECT schemaname, viewname, definition
   FROM pg_views
   WHERE schemaname = 'public'
   ORDER BY viewname;
   ```

2. **Function Issues**
   ```sql
   -- List all functions
   SELECT routine_name, routine_type
   FROM information_schema.routines
   WHERE routine_schema = 'public';
   ```

3. **Table Structure Verification**
   ```sql
   -- List all tables
   SELECT tablename
   FROM pg_tables
   WHERE schemaname = 'public'
   ORDER BY tablename;
   ```

### Validation Queries

```sql
-- Verify table row counts
SELECT schemaname, tablename, n_live_tup as row_count
FROM pg_stat_user_tables
ORDER BY tablename;

-- Check primary keys
SELECT tc.table_name, tc.constraint_name, kcu.column_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
  ON tc.constraint_name = kcu.constraint_name
  AND tc.table_schema = kcu.table_schema
WHERE tc.constraint_type = 'PRIMARY KEY'
  AND tc.table_schema = 'public'
ORDER BY tc.table_name;

-- List all views
SELECT table_name, view_definition
FROM information_schema.views
WHERE table_schema = 'public'
ORDER BY table_name;
```

## Version Information

- **Target Database**: PostgreSQL 12+
- **Liquibase Version**: 4.0+
- **Schema Version**: Auto-generated baseline (November 2024)
- **Purpose**: Database schema management for Trade Commodity Codes application

## Directory Structure

```
changelog/
├── README.md                          # This file
├── db.changelog.xml                   # Main changelog entry point
├── changes/                           # Table definitions
│   └── 00-generated-commoditycode-tables-baseline.xml
├── functions/                         # Function definitions
│   ├── functions.xml                  # Function changelog index
│   ├── 00-generated-commoditycode-functions-baseline.xml
│   └── fn_commodity_category_data.sql
└── views/                             # View definitions
    ├── views.xml                      # View changelog index
    ├── 00-generated-commoditycode-views-baseline.xml
    ├── chedp_species_view.sql
    ├── chedpp_species_view.sql
    ├── commodity_category_view.sql
    ├── commodity_code_species_view.sql
    ├── commodity_code_view.sql
    └── commodity_group_view.sql
```

## Support

For issues or questions:

1. Check the troubleshooting section above
2. Review PostgreSQL logs for detailed error messages
3. Validate Liquibase changelog syntax
4. Ensure all prerequisites are met
5. Test with a clean database instance

## Contributing

When modifying these changelogs:

1. Always test changes on a clean database
2. Follow the existing structure:
   - New tables go in `changes/` directory
   - New functions go in `functions/` directory
   - New views go in `views/` directory
3. Use `runOnChange="true"` for functions and views to allow updates
4. Maintain proper changeset IDs (avoid conflicts)
5. Document any new dependencies
6. Update this README with significant changes
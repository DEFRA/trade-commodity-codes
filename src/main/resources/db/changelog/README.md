# PostgreSQL Liquibase Migration Changelogs

This directory contains consolidated Liquibase XML changelog files for migrating the commodity code database from SQL Server to PostgreSQL. These changelogs are designed specifically for ETL processes and provide a complete PostgreSQL-optimized database schema.

## Files Overview

### Core Changelog Files

1. **`master-changelog.xml`** - Main entry point that includes all other changelogs in the correct order
2. **`01-tables-changelog.xml`** - All table structures, constraints, and indexes
3. **`02-views-changelog.xml`** - All database views for data access
4. **`03-functions-changelog.xml`** - All stored functions and procedures
5. **`04-permissions-changelog.xml`** - Complete role-based security model
6. **`05-additional-objects-changelog.xml`** - Triggers, types, sequences, and ETL utilities

### Execution Order

The changelogs must be executed in this specific order (handled automatically by `master-changelog.xml`):

```
1. Tables & Constraints    (01-tables-changelog.xml)
2. Functions               (03-functions-changelog.xml)  
3. Views                   (02-views-changelog.xml)
4. Additional Objects      (05-additional-objects-changelog.xml)
5. Permissions            (04-permissions-changelog.xml)
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
createdb commodity_db

# Run complete migration
liquibase --changeLogFile=master-changelog.xml update

# Verify migration
liquibase --changeLogFile=master-changelog.xml status
```

### User Setup (IMPORTANT)

**Before running the migration**, create database users according to your authentication method:

```sql
-- Option 1: Database Authentication
CREATE USER commodity_service_user WITH PASSWORD 'your_secure_password';
CREATE USER commodity_data_loader WITH PASSWORD 'your_secure_password';
CREATE USER commodity_readonly_user WITH PASSWORD 'your_secure_password';
CREATE USER commodity_admin_user WITH PASSWORD 'your_secure_password';

-- Option 2: Azure Managed Identity
CREATE USER "your-managed-identity-name" FROM EXTERNAL PROVIDER;

-- Option 3: LDAP/AD (configure according to your setup)
```

Then uncomment and modify the role assignment statements in `04-permissions-changelog.xml`.

## Database Schema Overview

### Core Tables (15 total)

| Table | Description | Key Features |
|-------|-------------|--------------|
| `certificates` | Certificate types and descriptions | Primary reference for cert types |
| `commodity_nomenclature` | Core commodity hierarchy | TRACES codes, parent-child relationships |
| `species` | Species reference data | EPPO codes, invasive species flags |
| `certification_requirement` | Links commodities to certificates | Visibility and selectability controls |
| `certification_nomenclature` | Species-specific cert requirements | Extended classification hierarchy |
| `commodity_class` | Commodity classifications | Regulatory categorization |
| `commodity_eppo_variety` | EPPO codes and varieties | Plant variety specifications |
| `hmi_marketing` | HMI marketing standards | Certificate validity periods |
| `inspection_responsibility` | Inspection assignments | By commodity and intended use |
| `commodity_group` | Business commodity groupings | Soft delete enabled |
| `commodity_group_commodity` | Individual group members | Links to main commodity data |
| `article_72_commodities` | Article 72 specific items | Regulatory compliance |
| `commodity_configuration` | Configuration settings | Test/trial requirements |
| `commoditycode_attributes` | Extended commodity attributes | Effective date management |
| `data_load` | ETL audit trail | Load tracking and monitoring |

### Views (9 total)

| View | Description | Use Case |
|------|-------------|----------|
| `v_commodity_code` | Main commodity view | Application frontend |
| `v_commodity_category` | Category data with JSON | API responses |
| `v_commodity_group` | Active commodity groups | Business reporting |
| `v_chedpp_species` | CHED-PP species data | Regulatory compliance |
| `v_chedp_species` | CHED-P species data | Extended classification |
| `v_commodity_code_species` | Enhanced commodity+species | Comprehensive data access |
| `v_data_load_status` | ETL monitoring | Operations dashboard |
| `v_active_commodity_configurations` | Current configurations | Application logic |
| `v_active_commodity_attributes` | Current attributes | Data validation |

### Security Roles (4 total)

| Role | Permissions | Use Case |
|------|-------------|----------|
| `commodity_service_role` | Read + limited updates | Main application |
| `commodity_data_loader_role` | Full DML access | ETL processes |
| `commodity_readonly_role` | Read-only access | Reporting/analytics |
| `commodity_admin_role` | Full database access | Administration |

## Key PostgreSQL Adaptations

### Data Type Conversions

| SQL Server | PostgreSQL | Notes |
|------------|------------|-------|
| `NVARCHAR(n)` | `VARCHAR(n)` | Unicode support maintained |
| `NVARCHAR(MAX)` | `TEXT` | Unlimited length |
| `BIT` | `BOOLEAN` | True/false values |
| `BIGINT IDENTITY(1,1)` | `BIGSERIAL` | Auto-incrementing |
| `DATETIME` | `TIMESTAMP` | Timezone-aware options available |

### Function Adaptations

- **`RIGHT()` function**: Implemented as custom function (SQL Server built-in)
- **JSON functions**: Utilizing PostgreSQL native JSON support
- **String manipulation**: Adapted to PostgreSQL syntax
- **Date/time handling**: Using PostgreSQL timestamp functions

### Performance Optimizations

- **Indexes**: Optimized for PostgreSQL query planner
- **Materialized Views**: `mv_commodity_code_stats` for reporting
- **Constraints**: Check constraints for data validation
- **Sequences**: Dedicated sequences for batch processing

## ETL Integration Features

### Data Loading Support

```sql
-- Log data load operations
SELECT fn_log_data_load('table_name', 'source_file.csv');

-- Validate data during ETL
SELECT fn_validate_commodity_code_format('12345678');

-- Convert SQL Server datetime formats
SELECT fn_convert_sql_server_datetime('2024-01-01 12:00:00.000');
```

### Monitoring and Maintenance

```sql
-- Get database statistics
SELECT fn_get_database_statistics();

-- Cleanup old load records
SELECT sp_cleanup_old_data_loads(90); -- Keep 90 days

-- Archive deleted attributes  
SELECT sp_archive_deleted_attributes();

-- Refresh materialized views
REFRESH MATERIALIZED VIEW mv_commodity_code_stats;
```

## Migration Best Practices

### Pre-Migration Checklist

- [ ] PostgreSQL server configured and running
- [ ] Database users created with appropriate authentication
- [ ] Network connectivity tested
- [ ] Backup strategy in place
- [ ] Rollback plan prepared

### During Migration

- [ ] Run validation queries after each stage
- [ ] Monitor for constraint violations
- [ ] Check foreign key relationships
- [ ] Verify index creation
- [ ] Test view functionality

### Post-Migration Checklist

- [ ] Refresh materialized views
- [ ] Run comprehensive data validation
- [ ] Test application connectivity with all roles
- [ ] Verify ETL process functionality
- [ ] Update connection strings in applications
- [ ] Monitor performance and optimize as needed

## Troubleshooting

### Common Issues

1. **Permission Errors**
   ```sql
   -- Check role assignments
   SELECT r.rolname, m.rolname as member_of 
   FROM pg_roles r 
   JOIN pg_auth_members am ON r.oid = am.member 
   JOIN pg_roles m ON am.roleid = m.oid 
   WHERE r.rolname LIKE 'commodity_%';
   ```

2. **View Dependency Issues**
   ```sql
   -- Check view dependencies
   SELECT schemaname, viewname, definition 
   FROM pg_views 
   WHERE schemaname = 'public' 
   ORDER BY viewname;
   ```

3. **Function Issues**
   ```sql
   -- List all functions
   SELECT routine_name, routine_type 
   FROM information_schema.routines 
   WHERE routine_schema = 'public';
   ```

### Validation Queries

```sql
-- Verify table row counts
SELECT schemaname, tablename, n_tup_ins as row_count 
FROM pg_stat_user_tables 
ORDER BY tablename;

-- Check foreign key constraints
SELECT tc.table_name, tc.constraint_name, tc.constraint_type,
       kcu.column_name, ccu.table_name AS foreign_table_name,
       ccu.column_name AS foreign_column_name 
FROM information_schema.table_constraints AS tc 
JOIN information_schema.key_column_usage AS kcu
  ON tc.constraint_name = kcu.constraint_name
  AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage AS ccu
  ON ccu.constraint_name = tc.constraint_name
  AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY';

-- Verify materialized view data
SELECT * FROM mv_commodity_code_stats;
```

## Performance Tuning

### Recommended Settings

```sql
-- PostgreSQL configuration recommendations
-- Add to postgresql.conf:

shared_buffers = '256MB'           # Adjust based on available RAM
effective_cache_size = '1GB'       # Adjust based on total RAM
maintenance_work_mem = '64MB'      # For index creation
work_mem = '4MB'                   # For query operations
random_page_cost = 1.1             # For SSD storage

-- Enable query logging for performance analysis
log_statement = 'all'
log_min_duration_statement = 1000  # Log queries > 1 second
```

### Monitoring Queries

```sql
-- Find slow queries
SELECT query, calls, total_time, mean_time 
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;

-- Check index usage
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read, idx_tup_fetch
FROM pg_stat_user_indexes 
ORDER BY idx_scan DESC;

-- Monitor table activity
SELECT schemaname, tablename, seq_scan, seq_tup_read, idx_scan, idx_tup_fetch
FROM pg_stat_user_tables 
ORDER BY seq_scan DESC;
```

## Version Information

- **Generated From**: SQL Server Liquibase scripts in `/configuration/database/src/main/resources/`
- **Target Database**: PostgreSQL 12+
- **Liquibase Version**: 4.0+
- **Generated Date**: November 2024
- **Purpose**: ETL migration from SQL Server to PostgreSQL

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
2. Update rollback procedures
3. Maintain proper changeset IDs
4. Document any new dependencies
5. Update this README with significant changes
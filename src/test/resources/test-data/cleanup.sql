-- Cleanup script for CommodityCodeControllerIT
-- This script removes all test data after each test method execution

-- Delete all test data from the commodity codes table
DELETE FROM v_commodity_code WHERE code IN (
    '1001', '1002', '1003', '1005', '1006', '1007', '1008', '10',
    '1001101000', '1001109000', '1001991000', '1001201000', '1001909000',
    '2001', '2002', '2005', '3001', '4001', '5001', '6001',
    '9997', '9998', '9999'
);

-- Alternative cleanup approach - delete by pattern if needed
-- DELETE FROM v_commodity_code WHERE code LIKE '1%' OR code LIKE '2%' OR code LIKE '3%' OR code LIKE '4%' OR code LIKE '5%' OR code LIKE '6%' OR code LIKE '9%';

-- Reset any sequences or auto-increment values if applicable
-- This ensures consistent test execution across multiple runs

-- If using a test-specific schema, you could also truncate the entire table
-- TRUNCATE TABLE v_commodity_code RESTART IDENTITY CASCADE;
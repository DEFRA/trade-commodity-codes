-- Test data for CommodityCodeControllerIT
-- This script sets up sample commodity codes for integration testing

-- Create test table structure if using a different schema or for isolated tests
CREATE TABLE IF NOT EXISTS v_commodity_code (
    code VARCHAR(20) NOT NULL,
    cert_type VARCHAR(10) NOT NULL,
    display_code VARCHAR(20),
    description TEXT,
    immediate_parent VARCHAR(20),
    is_commodity BOOLEAN DEFAULT false,
    is_parent BOOLEAN DEFAULT false,
    PRIMARY KEY (code, cert_type)
);

-- Insert test data for various test scenarios
INSERT INTO v_commodity_code (code, cert_type, display_code, description, immediate_parent, is_commodity, is_parent) VALUES
-- Basic test codes for CRUD operations
('1001', 'PHYTO', '1001', 'Live animals; animal products', '10', true, false),
('1002', 'PHYTO', '1002', 'Vegetable products', '10', true, false),
('1003', 'PHYTO', '1003', 'Animal or vegetable fats and oils', '10', true, false),

-- Test codes for search functionality (description contains 'wheat')
('1001101000', 'PHYTO', '10011010', 'Durum wheat', '100110', true, false),
('1001109000', 'PHYTO', '10011090', 'Common wheat and meslin', '100110', true, false),
('1001991000', 'PHYTO', '10019910', 'Wheat flour for animal feeding', '100199', true, false),

-- Test codes for prefix search (starting with '10')
('1005', 'PHYTO', '1005', 'Maize (corn)', '10', true, false),
('1006', 'PHYTO', '1006', 'Rice', '10', true, false),
('1007', 'PHYTO', '1007', 'Grain sorghum', '10', true, false),
('1008', 'PHYTO', '1008', 'Buckwheat, millet and canary seed', '10', true, false),

-- Parent categories for hierarchy testing
('10', 'PHYTO', '10', 'Live animals and animal products', '', false, true),
('1001', 'CITES', '1001', 'CITES regulated live animals', '10', true, false),

-- Additional test data with different cert types
('2001', 'CITES', '2001', 'Protected animal species', '20', true, false),
('2002', 'CITES', '2002', 'Protected plant species', '20', true, false),

-- Test data for edge cases
('9998', 'PHYTO', '9998', 'Test commodity for deletion', '99', true, false),
('9997', 'PHYTO', '9997', 'Another test commodity', '99', true, false);

-- Insert some additional data for comprehensive testing
INSERT INTO v_commodity_code (code, cert_type, display_code, description, immediate_parent, is_commodity, is_parent) VALUES
-- More wheat-related products for search testing
('1001201000', 'PHYTO', '10012010', 'Seed wheat', '100120', true, false),
('1001909000', 'PHYTO', '10019090', 'Other wheat', '100190', true, false),

-- Products starting with different prefixes
('2005', 'PHYTO', '2005', 'Other vegetables, prepared or preserved', '20', true, false),
('3001', 'PHYTO', '3001', 'Glands and other organs for therapeutic uses', '30', true, false),
('4001', 'PHYTO', '4001', 'Natural rubber, latex', '40', true, false),

-- Test data with special characters and longer descriptions
('5001', 'PHYTO', '5001', 'Silkworm cocoons suitable for reeling; silk waste (including cocoons unsuitable for reeling)', '50', true, false),
('6001', 'PHYTO', '6001', 'Pile fabrics, including "long pile" fabrics and terry fabrics, knitted or crocheted', '60', true, false);
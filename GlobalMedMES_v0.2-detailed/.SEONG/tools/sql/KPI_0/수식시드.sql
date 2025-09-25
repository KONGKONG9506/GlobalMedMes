-- OEE
UPDATE tb_definition
SET formula = '(#good_qty.divide(#total_qty,4,T(java.math.RoundingMode).HALF_UP).multiply(#run_time.divide(#planned_time,4,T(java.math.RoundingMode).HALF_UP))).multiply(#hundred)'
WHERE definition_id = 1;

-- Yield
UPDATE tb_definition
SET formula = '(#good_qty.divide(#total_qty,4,T(java.math.RoundingMode).HALF_UP)).multiply(#hundred)'
WHERE definition_id = 2;

-- Defect Rate
UPDATE tb_definition
SET formula = '(#defect_qty.divide(#total_qty,4,T(java.math.RoundingMode).HALF_UP)).multiply(#hundred)'
WHERE definition_id = 3;

-- Productivity
UPDATE tb_definition
SET formula = '#produced_qty.divide(#run_time,4,T(java.math.RoundingMode).HALF_UP)'
WHERE definition_id = 4;

SELECT * FROM globalmed.tb_definition;

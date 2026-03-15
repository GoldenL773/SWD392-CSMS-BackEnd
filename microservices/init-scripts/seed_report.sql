USE csms_report;
GO

-- Add reports for the last 7 days
IF NOT EXISTS (SELECT 1 FROM daily_reports WHERE CAST(report_date AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE))
    INSERT INTO daily_reports (report_date, total_orders, total_sales, total_revenue, total_cost, profit, created_at, updated_at)
    VALUES (DATEADD(DAY, -1, GETDATE()), 45, 225.50, 225.50, 95.00, 130.50, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM daily_reports WHERE CAST(report_date AS DATE) = CAST(DATEADD(DAY, -2, GETDATE()) AS DATE))
    INSERT INTO daily_reports (report_date, total_orders, total_sales, total_revenue, total_cost, profit, created_at, updated_at)
    VALUES (DATEADD(DAY, -2, GETDATE()), 38, 190.00, 190.00, 80.00, 110.00, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM daily_reports WHERE CAST(report_date AS DATE) = CAST(DATEADD(DAY, -3, GETDATE()) AS DATE))
    INSERT INTO daily_reports (report_date, total_orders, total_sales, total_revenue, total_cost, profit, created_at, updated_at)
    VALUES (DATEADD(DAY, -3, GETDATE()), 42, 210.00, 210.00, 88.00, 122.00, GETDATE(), GETDATE());
-- ... and so on, but let's keep it simple for now or use a loop if needed.
-- For brevity, I'll just wrap the existing ones.
GO

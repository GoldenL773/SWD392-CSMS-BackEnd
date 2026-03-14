USE csms_report;
GO

IF NOT EXISTS (SELECT 1 FROM daily_reports WHERE report_date = CAST(GETDATE() AS DATE))
BEGIN
    INSERT INTO daily_reports (report_date, total_orders, total_sales, total_revenue, total_cost, profit, created_at, updated_at)
    VALUES (CAST(GETDATE() AS DATE), 10, 50.00, 50.00, 20.00, 30.00, GETDATE(), GETDATE());
END
GO

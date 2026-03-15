USE csms_report;
GO

-- Add reports for the last 7 days
INSERT INTO daily_reports (report_date, total_orders, total_sales, total_revenue, total_cost, profit, created_at, updated_at)
VALUES (DATEADD(DAY, -1, GETDATE()), 45, 225.50, 225.50, 95.00, 130.50, GETDATE(), GETDATE());
INSERT INTO daily_reports (report_date, total_orders, total_sales, total_revenue, total_cost, profit, created_at, updated_at)
VALUES (DATEADD(DAY, -2, GETDATE()), 38, 190.00, 190.00, 80.00, 110.00, GETDATE(), GETDATE());
INSERT INTO daily_reports (report_date, total_orders, total_sales, total_revenue, total_cost, profit, created_at, updated_at)
VALUES (DATEADD(DAY, -3, GETDATE()), 42, 210.00, 210.00, 88.00, 122.00, GETDATE(), GETDATE());
INSERT INTO daily_reports (report_date, total_orders, total_sales, total_revenue, total_cost, profit, created_at, updated_at)
VALUES (DATEADD(DAY, -4, GETDATE()), 35, 175.00, 175.00, 75.00, 100.00, GETDATE(), GETDATE());
INSERT INTO daily_reports (report_date, total_orders, total_sales, total_revenue, total_cost, profit, created_at, updated_at)
VALUES (DATEADD(DAY, -5, GETDATE()), 48, 240.00, 240.00, 102.00, 138.00, GETDATE(), GETDATE());
INSERT INTO daily_reports (report_date, total_orders, total_sales, total_revenue, total_cost, profit, created_at, updated_at)
VALUES (DATEADD(DAY, -6, GETDATE()), 30, 150.00, 150.00, 65.00, 85.00, GETDATE(), GETDATE());
INSERT INTO daily_reports (report_date, total_orders, total_sales, total_revenue, total_cost, profit, created_at, updated_at)
VALUES (DATEADD(DAY, -7, GETDATE()), 25, 125.00, 125.00, 55.00, 70.00, GETDATE(), GETDATE());
GO

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'csms_auth') CREATE DATABASE csms_auth;
GO
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'csms_employee') CREATE DATABASE csms_employee;
GO
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'csms_inventory') CREATE DATABASE csms_inventory;
GO
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'csms_order') CREATE DATABASE csms_order;
GO
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'csms_product') CREATE DATABASE csms_product;
GO
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'csms_report') CREATE DATABASE csms_report;
GO

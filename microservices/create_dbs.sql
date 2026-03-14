-- Create Databases for Microservices
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'csms_auth') CREATE DATABASE csms_auth;
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'csms_product') CREATE DATABASE csms_product;
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'csms_inventory') CREATE DATABASE csms_inventory;
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'csms_employee') CREATE DATABASE csms_employee;
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'csms_order') CREATE DATABASE csms_order;
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'csms_report') CREATE DATABASE csms_report;
GO

#!/bin/bash
# Wait for SQL Server to start
sleep 20

/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -d master -C -No -i /docker-entrypoint-initdb.d/init.sql

# Run seed script
sleep 10
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -d csms_auth -C -No -i /docker-entrypoint-initdb.d/seed.sql
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -d csms_product -C -No -i /docker-entrypoint-initdb.d/seed_product.sql
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -d csms_employee -C -No -i /docker-entrypoint-initdb.d/seed_employee.sql
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -d csms_inventory -C -No -i /docker-entrypoint-initdb.d/seed_inventory.sql
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -d csms_order -C -No -i /docker-entrypoint-initdb.d/seed_order.sql
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -d csms_report -C -No -i /docker-entrypoint-initdb.d/seed_report.sql

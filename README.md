# mssql-jdbc-decimal-tvp
Tests reproducing some exceptions encountered when passing BigDecimals in TVP's via mssql jdbc.  An example exception of the exception seen:

com.microsoft.sqlserver.jdbc.SQLServerException: The incoming tabular data stream (TDS) remote procedure call (RPC) protocol stream is incorrect. Table-valued parameter 0 (""), row 0, column 0: Data type 0x6C has an invalid precision or scale.

Suspected cause is in how the mssql jdbc driver assumes java's BigDecimal handles precision and scale (which differs from that of SQL Server) here: https://github.com/Microsoft/mssql-jdbc/blob/eb14f63077c47ef1fc1c690deb8cfab602baeb85/src/main/java/com/microsoft/sqlserver/jdbc/SQLServerDataTable.java#L158
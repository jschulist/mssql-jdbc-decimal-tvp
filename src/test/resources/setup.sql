CREATE SCHEMA [testing];

CREATE TYPE [testing].[TestingDataTable] AS TABLE (
	[ADecimal] decimal(18,4) NOT NULL
);

CREATE TABLE [testing].[TestingTable] (
    [ADecimal] decimal(18,4) NOT NULL
);

CREATE PROCEDURE [testing].[sp_testing]
    @testingDataTable [testing].[TestingDataTable] READONLY
AS
BEGIN
    INSERT INTO [testing].[TestingTable]
    SELECT [ADecimal] FROM @testingDataTable
END;
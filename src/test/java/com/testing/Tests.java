package com.testing;

import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import org.junit.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class Tests {
    static DataSource _dataSource;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        _dataSource = DatabaseUtils.getDataSource(DbProperties.DEFAULT.loadProperties());

        tearDownDatabase(); // clean up
        setupDatabase();
    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        tearDownDatabase();
    }

    @Test
    public void success_0() throws SQLException {
        attemptStoredProcedureExecution(getDataTableWithValue(BigDecimal.ZERO));
    }

    @Test
    public void success_1() throws SQLException {
        attemptStoredProcedureExecution(getDataTableWithValue(new BigDecimal("1")));
    }

    @Test
    public void success_1_0() throws SQLException {
        attemptStoredProcedureExecution(getDataTableWithValue(new BigDecimal("1.0")));
    }

    @Test
    public void success_0_1() throws SQLException {
        attemptStoredProcedureExecution(getDataTableWithValue(new BigDecimal("0.1")));
    }

    @Test
    public void success_1_01() throws SQLException {
        attemptStoredProcedureExecution(getDataTableWithValue(new BigDecimal("1.01")));
    }

    @Test
    public void success_max_precision() throws SQLException {
        // per our user defined table, decimal is defined as (18,4)
        attemptStoredProcedureExecution(getDataTableWithValue(new BigDecimal("99999999999999")));
    }

    @Test
    public void success_max_precision_and_scale() throws SQLException {
        // per our user defined table, decimal is defined as (18,4)
        attemptStoredProcedureExecution(getDataTableWithValue(new BigDecimal("99999999999999.9999")));
    }

    @Test
    public void fail_0_01() throws SQLException {
        BigDecimal bigDecimal = new BigDecimal("0.01");
        // internally the above bigDecimal has:
        // precision = 1
        // scale = 2

        attemptStoredProcedureExecution(getDataTableWithValue(bigDecimal));
    }

    @Test
    public void fail_0_001() throws SQLException {
        BigDecimal bigDecimal = new BigDecimal("0.001");
        // internally the above bigDecimal has:
        // precision = 1
        // scale = 3

        attemptStoredProcedureExecution(getDataTableWithValue(bigDecimal));
    }

    @Test
    public void fail_with_specific_scale() throws SQLException {
        BigDecimal bigDecimal = new BigDecimal("0.01").setScale(4, BigDecimal.ROUND_DOWN);
        // internally the above bigDecimal has:
        // precision = 0, according to source, must call precision() to obtain and set precision
        // scale = 4

        attemptStoredProcedureExecution(getDataTableWithValue(bigDecimal));
    }

    @Test
    public void fail_with_specific_scale_force_precision() throws SQLException {
        BigDecimal bigDecimal = new BigDecimal("0.01").setScale(4, BigDecimal.ROUND_DOWN);
        // internally the above bigDecimal has:
        // precision = 0
        // scale = 4
        bigDecimal.precision();
        // now the precision = 3

        attemptStoredProcedureExecution(getDataTableWithValue(bigDecimal));
    }

    private Connection getConnection() throws SQLException {
        return _dataSource.getConnection();
    }

    private static void setupDatabase() throws SQLException {
        runBatch("setup.sql");
    }

    private static void tearDownDatabase() throws SQLException {
        runBatch("teardown.sql");
    }

    private static String loadResourceToString(String resource) {
        return new Scanner(Tests.class.getClassLoader().getResourceAsStream(resource), "UTF-8")
            .useDelimiter("\\A")
            .next();
    }

    private static void runBatch(String resource) throws SQLException {
        try (Connection connection = _dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            String sql = loadResourceToString(resource);

            String[] pieces = sql.split(";");

            for (String piece : pieces) {
                statement.execute(piece);
            }
        }
    }

    private static SQLServerDataTable getDataTableWithValue(BigDecimal bigDecimal) throws SQLServerException {
        SQLServerDataTable dataTable = new SQLServerDataTable();
        dataTable.addColumnMetadata("ADecimal", Types.DECIMAL);

        dataTable.addRow(bigDecimal);

        return dataTable;
    }

    private void attemptStoredProcedureExecution(SQLServerDataTable dataTable) throws SQLException {
        try (Connection connection = getConnection();
             SQLServerPreparedStatement statement =
                 (SQLServerPreparedStatement)connection.prepareStatement("exec [testing].[sp_testing] ?")) {

            statement.setStructured(1, "[testing].[TestingDataTable]", dataTable);

            statement.execute();
        }
    }
}

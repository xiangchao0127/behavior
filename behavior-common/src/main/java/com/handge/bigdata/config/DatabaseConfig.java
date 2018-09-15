/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan. 
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna. 
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus. 
 * Vestibulum commodo. Ut rhoncus gravida arcu. 
 */

package com.handge.bigdata.config;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.convert.DisabledListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.event.ConfigurationErrorEvent;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class DatabaseConfig extends AbstractConfiguration implements Serializable {
    /**
     * Constant for the statement used by getProperty.
     */
    private static final String SQL_GET_PROPERTY = "SELECT * FROM %s WHERE %s =?";

    /**
     * Constant for the statement used by isEmpty.
     */
    private static final String SQL_IS_EMPTY = "SELECT count(*) FROM %s WHERE 1 = 1";

    /**
     * Constant for the statement used by clearProperty.
     */
    private static final String SQL_CLEAR_PROPERTY = "DELETE FROM %s WHERE %s =?";

    /**
     * Constant for the statement used by clear.
     */
    private static final String SQL_CLEAR = "DELETE FROM %s WHERE 1 = 1";

    /**
     * Constant for the statement used by getKeys.
     */
    private static final String SQL_GET_KEYS = "SELECT DISTINCT %s FROM %s WHERE 1 = 1";

    /**
     * The data source to connect to the database.
     */
    private DataSource dataSource;

    /**
     * The configurationName of the table containing the configurations.
     */
    private String table;

    /**
     * The column containing the configurationName of the configuration.
     */
    private String configurationNameColumn;

    /**
     * The column containing the keys.
     */
    private String keyColumn;

    /**
     * The column containing the values.
     */
    private String valueColumn;

    /**
     * The configurationName of the configuration.
     */
    private String configurationName;

    /**
     * A flag whether commits should be performed by this configuration.
     */
    private boolean autoCommit;

    private Connection connection;


    /**
     * Creates a new instance of {@code DatabaseConfiguration}.
     */
    public DatabaseConfig() {
        initLogger(new ConfigurationLogger(DatabaseConfig.class));
        addErrorLogListener();
    }

    /**
     * Converts a CLOB to a string.
     *
     * @param clob the CLOB to be converted
     * @return the extracted string value
     * @throws SQLException if an error occurs
     */
    private static Object convertClob(Clob clob) throws SQLException {
        int len = (int) clob.length();
        return (len > 0) ? clob.getSubString(1, len) : StringUtils.EMPTY;
    }

    /**
     * Returns the {@code DataSource} for obtaining database connections.
     *
     * @return the {@code DataSource}
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Sets the {@code DataSource} for obtaining database connections.
     *
     * @param dataSource the {@code DataSource}
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns the name of the table containing configuration data.
     *
     * @return the name of the table to be queried
     */
    public String getTable() {
        return table;
    }

    /**
     * Sets the name of the table containing configuration data.
     *
     * @param table the table name
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * Returns the name of the table column with the configuration name.
     *
     * @return the name of the configuration name column
     */
    public String getConfigurationNameColumn() {
        return configurationNameColumn;
    }

    /**
     * Sets the name of the table column with the configuration name.
     *
     * @param configurationNameColumn the name of the column with the
     *                                configuration name
     */
    public void setConfigurationNameColumn(String configurationNameColumn) {
        this.configurationNameColumn = configurationNameColumn;
    }

    /**
     * Returns the name of the column containing the configuration keys.
     *
     * @return the name of the key column
     */
    public String getKeyColumn() {
        return keyColumn;
    }

    /**
     * Sets the name of the column containing the configuration keys.
     *
     * @param keyColumn the name of the key column
     */
    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    /**
     * Returns the name of the column containing the configuration values.
     *
     * @return the name of the value column
     */
    public String getValueColumn() {
        return valueColumn;
    }

    /**
     * Sets the name of the column containing the configuration values.
     *
     * @param valueColumn the name of the value column
     */
    public void setValueColumn(String valueColumn) {
        this.valueColumn = valueColumn;
    }

    /**
     * Returns the name of this configuration instance.
     *
     * @return the name of this configuration
     */
    public String getConfigurationName() {
        return configurationName;
    }

    /**
     * Sets the name of this configuration instance.
     *
     * @param configurationName the name of this configuration
     */
    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    /**
     * Returns a flag whether this configuration performs commits after database
     * updates.
     *
     * @return a flag whether commits are performed
     */
    public boolean isAutoCommit() {
        return autoCommit;
    }

    /**
     * Sets the auto commit flag. If set to <b>true</b>, this configuration
     * performs a commit after each database update.
     *
     * @param autoCommit the auto commit flag
     */
    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    /**
     * Returns the value of the specified property. If this causes a database
     * error, an error event will be generated of type
     * {@code READ} with the causing exception. The
     * event's {@code propertyName} is set to the passed in property key,
     * the {@code propertyValue} is undefined.
     *
     * @param key the key of the desired property
     * @return the value of this property
     */
    @Override
    protected Object getPropertyInternal(final String key) {
        JdbcOperation<Object> op =
                new JdbcOperation<Object>(ConfigurationErrorEvent.READ,
                        ConfigurationErrorEvent.READ, key, null) {
                    @Override
                    protected Object performOperation() throws SQLException {
                        ResultSet rs =
                                openResultSet(String.format(SQL_GET_PROPERTY,
                                        table, keyColumn), true, key);

                        List<Object> results = new ArrayList<Object>();
                        while (rs.next()) {
                            Object value = extractPropertyValue(rs);
                            // Split value if it contains the list delimiter
                            for (Object o : getListDelimiterHandler().parse(value)) {
                                results.add(o);
                            }
                        }

                        if (!results.isEmpty()) {
                            return (results.size() > 1) ? results : results
                                    .get(0);
                        } else {
                            return null;
                        }
                    }
                };

        return op.execute();
    }

    /**
     * Adds a property to this configuration. If this causes a database error,
     * an error event will be generated of type {@code ADD_PROPERTY}
     * with the causing exception. The event's {@code propertyName} is
     * set to the passed in property key, the {@code propertyValue}
     * points to the passed in value.
     *
     * @param key the property key
     * @param obj the value of the property to add
     */
    @Override
    protected void addPropertyDirect(final String key, final Object obj) {
        new JdbcOperation<Void>(ConfigurationErrorEvent.WRITE,
                ConfigurationEvent.ADD_PROPERTY, key, obj) {
            @Override
            protected Void performOperation() throws SQLException {
                StringBuilder query = new StringBuilder("INSERT INTO ");
                query.append(table).append(" (");
                query.append(keyColumn).append(", ");
                query.append(valueColumn);
                if (configurationNameColumn != null) {
                    query.append(", ").append(configurationNameColumn);
                }
                query.append(") VALUES (?, ?");
                if (configurationNameColumn != null) {
                    query.append(", ?");
                }
                query.append(")");

                PreparedStatement pstmt = initStatement(query.toString(),
                        false, key, String.valueOf(obj));
                if (configurationNameColumn != null) {
                    pstmt.setString(3, configurationName);
                }

                pstmt.executeUpdate();
                return null;
            }
        }
                .execute();
    }

    /**
     * Adds a property to this configuration. This implementation
     * temporarily disables list delimiter parsing, so that even if the value
     * contains the list delimiter, only a single record is written into
     * the managed table. The implementation of {@code getProperty()}
     * takes care about delimiters. So list delimiters are fully supported
     * by {@code DatabaseConfiguration}, but internally treated a bit
     * differently.
     *
     * @param key   the key of the new property
     * @param value the value to be added
     */
    @Override
    protected void addPropertyInternal(String key, Object value) {
        ListDelimiterHandler oldHandler = getListDelimiterHandler();
        try {
            // temporarily disable delimiter parsing
            setListDelimiterHandler(DisabledListDelimiterHandler.INSTANCE);
            super.addPropertyInternal(key, value);
        } finally {
            setListDelimiterHandler(oldHandler);
        }
    }

    /**
     * Checks if this configuration is empty. If this causes a database error,
     * an error event will be generated of type {@code READ}
     * with the causing exception. Both the event's {@code propertyName}
     * and {@code propertyValue} will be undefined.
     *
     * @return a flag whether this configuration is empty.
     */
    @Override
    protected boolean isEmptyInternal() {
        JdbcOperation<Integer> op =
                new JdbcOperation<Integer>(ConfigurationErrorEvent.READ,
                        ConfigurationErrorEvent.READ, null, null) {
                    @Override
                    protected Integer performOperation() throws SQLException {
                        ResultSet rs = openResultSet(String.format(
                                SQL_IS_EMPTY, table), true);

                        return rs.next() ? Integer.valueOf(rs.getInt(1)) : null;
                    }
                };

        Integer count = op.execute();
        return count == null || count.intValue() == 0;
    }

    /**
     * Checks whether this configuration contains the specified key. If this
     * causes a database error, an error event will be generated of type
     * {@code READ} with the causing exception. The
     * event's {@code propertyName} will be set to the passed in key, the
     * {@code propertyValue} will be undefined.
     *
     * @param key the key to be checked
     * @return a flag whether this key is defined
     */
    @Override
    protected boolean containsKeyInternal(final String key) {
        JdbcOperation<Boolean> op =
                new JdbcOperation<Boolean>(ConfigurationErrorEvent.READ,
                        ConfigurationErrorEvent.READ, key, null) {
                    @Override
                    protected Boolean performOperation() throws SQLException {
                        ResultSet rs = openResultSet(
                                String.format(SQL_GET_PROPERTY, table, keyColumn), true, key);

                        return rs.next();
                    }
                };

        Boolean result = op.execute();
        return result != null && result.booleanValue();
    }

    /**
     * Removes the specified value from this configuration. If this causes a
     * database error, an error event will be generated of type
     * {@code CLEAR_PROPERTY} with the causing exception. The
     * event's {@code propertyName} will be set to the passed in key, the
     * {@code propertyValue} will be undefined.
     *
     * @param key the key of the property to be removed
     */
    @Override
    protected void clearPropertyDirect(final String key) {
        new JdbcOperation<Void>(ConfigurationErrorEvent.WRITE,
                ConfigurationEvent.CLEAR_PROPERTY, key, null) {
            @Override
            protected Void performOperation() throws SQLException {
                PreparedStatement ps = initStatement(String.format(
                        SQL_CLEAR_PROPERTY, table, keyColumn), true, key);
                ps.executeUpdate();
                return null;
            }
        }
                .execute();
    }

    /**
     * Removes all entries from this configuration. If this causes a database
     * error, an error event will be generated of type
     * {@code CLEAR} with the causing exception. Both the
     * event's {@code propertyName} and the {@code propertyValue}
     * will be undefined.
     */
    @Override
    protected void clearInternal() {
        new JdbcOperation<Void>(ConfigurationErrorEvent.WRITE,
                ConfigurationEvent.CLEAR, null, null) {
            @Override
            protected Void  performOperation()   {
                PreparedStatement state = null;
                try {
                     state = initStatement(String.format(SQL_CLEAR,
                            table), true);
                    state.executeUpdate();
                }catch (Exception e){
                    throw  new RuntimeException(e);
                }finally {
                   if(state != null) {
                       try {
                           state.close();
                       } catch (SQLException e) {
                           e.printStackTrace();
                       }
                   }
                }

                return null;
            }
        }
                .execute();
    }

    /**
     * Returns an iterator with the names of all properties contained in this
     * configuration. If this causes a database
     * error, an error event will be generated of type
     * {@code READ} with the causing exception. Both the
     * event's {@code propertyName} and the {@code propertyValue}
     * will be undefined.
     *
     * @return an iterator with the contained keys (an empty iterator in case
     * of an error)
     */
    @Override
    protected Iterator<String> getKeysInternal() {
        final Collection<String> keys = new ArrayList<String>();
        new JdbcOperation<Collection<String>>(ConfigurationErrorEvent.READ,
                ConfigurationErrorEvent.READ, null, null) {
            @Override
            protected Collection<String> performOperation() throws SQLException {
                ResultSet rs = openResultSet(String.format(
                        SQL_GET_KEYS, keyColumn, table), true);

                while (rs.next()) {
                    keys.add(rs.getString(1));
                }
                rs.close();
                return keys;
            }
        }
                .execute();
        return keys.iterator();
    }

    /**
     * Returns the used {@code DataSource} object.
     *
     * @return the data source
     * @since 1.4
     */
    public DataSource getDatasource() {
        return dataSource;
    }

    /**
     * Close the specified database objects.
     * Avoid closing if null and hide any SQLExceptions that occur.
     *
     * @param stmt The statement to close
     * @param rs   the result set to close
     */
    protected void close( Statement stmt, ResultSet rs) {
        try {
            if (rs != null || (!rs.isClosed())) {
                rs.close();
            }
        } catch (SQLException e) {
            getLogger().error("An error occurred on closing the result set", e);
        }

        try {
            if (stmt != null|| (!stmt.isClosed())) {
                stmt.close();
            }
        } catch (SQLException e) {
            getLogger().error("An error occured on closing the statement", e);
        }
    }

    /**
     * Extracts the value of a property from the given result set. The passed in
     * {@code ResultSet} was created by a SELECT statement on the underlying
     * database table. This implementation reads the value of the column
     * determined by the {@code valueColumn} property. Normally the contained
     * value is directly returned. However, if it is of type {@code CLOB}, text
     * is extracted as string.
     *
     * @param rs the current {@code ResultSet}
     * @return the value of the property column
     * @throws SQLException if an error occurs
     */
    protected Object extractPropertyValue(ResultSet rs) throws SQLException {
        Object value = rs.getObject(valueColumn);
        if (value instanceof Clob) {
            value = convertClob((Clob) value);
        }
        return value;
    }

    private synchronized Connection getJDBCConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = getDatasource().getConnection();
            }
        } catch (SQLException e) {
            fireError(null, null, null,
                    null, e);
        }
        return this.connection;
    }

    /**
     * An internally used helper class for simplifying database access through
     * plain JDBC. This class provides a simple framework for creating and
     * executing a JDBC statement. It especially takes care of proper handling
     * of JDBC resources even in case of an error.
     *
     * @param <T> the type of the results produced by a JDBC operation
     */
    private abstract class JdbcOperation<T> {
        /**
         * The type of the event to send in case of an error.
         */
        private final EventType<? extends ConfigurationErrorEvent> errorEventType;
        /**
         * The type of the operation which caused an error.
         */
        private final EventType<?> operationEventType;
        /**
         * The property configurationName for an error event.
         */
        private final String errorPropertyName;
        /**
         * The property value for an error event.
         */
        private final Object errorPropertyValue;

        /**
         * Stores the statement.
         */
        private PreparedStatement pstmt;
        /**
         * Stores the result set.
         */
        private ResultSet resultSet;

        /**
         * Creates a new instance of {@code JdbcOperation} and initializes the
         * properties related to the error event.
         *
         * @param errEvType   the type of the error event
         * @param opType      the operation event type
         * @param errPropName the property configurationName for the error event
         * @param errPropVal  the property value for the error event
         */
        protected JdbcOperation(
                EventType<? extends ConfigurationErrorEvent> errEvType,
                EventType<?> opType, String errPropName, Object errPropVal) {
            errorEventType = errEvType;
            operationEventType = opType;
            errorPropertyName = errPropName;
            errorPropertyValue = errPropVal;
        }

        /**
         * Executes this operation. This method obtains a database connection
         * and then delegates to {@code performOperation()}. Afterwards it
         * performs the necessary clean up. Exceptions that are thrown during
         * the JDBC operation are caught and transformed into configuration
         * error events.
         *
         * @return the result of the operation
         */
        public T execute() {
            T result = null;

            try {

                result = performOperation();

            } catch (SQLException e) {
                fireError(errorEventType, operationEventType, errorPropertyName,
                        errorPropertyValue, e);
            } finally {
                close( pstmt, resultSet);
            }

            return result;
        }

        /**
         * Returns the current connection. This method can be called while
         * {@code execute()} is running. It returns <b>null</b> otherwise.
         *
         * @return the current connection
         */
        protected Connection getConnection() {
            return getJDBCConnection();
        }



        /**
         * Creates an initializes a {@code PreparedStatement} object for
         * executing an SQL statement. This method first calls
         * {@code createStatement()} for creating the statement and then
         * initializes the statement's parameters.
         *
         * @param sql     the statement to be executed
         * @param nameCol a flag whether the configurationName column should be taken into
         *                account
         * @param params  the parameters for the statement
         * @return the initialized statement object
         * @throws SQLException if an SQL error occurs
         */
        protected PreparedStatement initStatement(String sql, boolean nameCol,
                                                  Object... params) throws SQLException {
            String statement;
            if (nameCol && configurationNameColumn != null) {
                StringBuilder buf = new StringBuilder(sql);
                buf.append(" AND ").append(configurationNameColumn).append("=?");
                statement = buf.toString();
            } else {
                statement = sql;
            }

            pstmt = getConnection().prepareStatement(statement);

            int idx = 1;
            for (Object param : params) {
                pstmt.setObject(idx++, param);
            }
            if (nameCol && configurationNameColumn != null) {
                pstmt.setString(idx, configurationName);
            }

            return pstmt;
        }

        /**
         * Creates a {@code PreparedStatement} for a query, initializes it and
         * executes it. The resulting {@code ResultSet} is returned.
         *
         * @param sql     the statement to be executed
         * @param nameCol a flag whether the configurationName column should be taken into
         *                account
         * @param params  the parameters for the statement
         * @return the {@code ResultSet} produced by the query
         * @throws SQLException if an SQL error occurs
         */
        protected ResultSet openResultSet(String sql, boolean nameCol,
                                          Object... params) throws SQLException {
            initStatement(sql, nameCol, params);
            resultSet = pstmt.executeQuery();
            return resultSet;
        }

        /**
         * Performs the JDBC operation. This method is called by
         * {@code execute()} after this object has been fully initialized.
         * Here the actual JDBC logic has to be placed.
         *
         * @return the result of the operation
         * @throws SQLException if an SQL error occurs
         */
        protected abstract T performOperation() throws SQLException;
    }
}

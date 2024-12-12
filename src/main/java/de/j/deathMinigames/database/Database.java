package de.j.deathMinigames.database;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.mapper.RowMapperRegistry;
import de.chojo.sadu.postgresql.databases.PostgreSql;
import de.chojo.sadu.postgresql.mapper.PostgresqlMapper;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.j.stationofdoom.main.Main;

import java.util.HashMap;

public class Database {
    private static Database instance;
    private HikariDataSource dataSource;
    private int port = 9667; // 9667
    private String host = "localhost"; // localhost
    private String database = "stationofdoom"; // stationofdoom
    private String user = "mc"; // mc
    private String password = "65465"; // 65465
    private String applicationName = "StationOfDoom"; // StationOfDoom
    private String schema = "public"; // default

    private Database() {}

    /**
     * Returns the single instance of the Database. If it's the first time the
     * method is called, a new instance is created.
     *
     * @return the single instance of the Database.
     */
    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    /**
     * Initializes the database connection.
     * <p>
     * This method creates a Postgres data source with the configuration specified in the constructor.
     * It also sets the default query configuration and creates the player data table if it does not already exist.
     * <p>
     * The maximum pool size is set to 3 and the minimum pool size is set to 1.
     * This means that the database connection will be reused if it is idle for a certain amount of time.
     * <p>
     * This method should be called once when the plugin is enabled.
     */
    public void initDatabase(){
        dataSource = DataSourceCreator.create(PostgreSql.get()).configure(config -> config.host("localhost")
                .port(port)
                .database(database)
                .user(user)
                .password(password)
                .currentSchema(schema)
                .applicationName(applicationName)
        )
                .create()
                .withMaximumPoolSize(3)
                .withMinimumIdle(1)
                .build();

        configureDefaultQuery();
        PlayerDataDatabase.getInstance().createTable();
        Main.getMainLogger().info("Database initialized");
    }

    /**
     * Configures the default query settings for the database.
     *
     * This method sets up a QueryConfiguration using the current data source.
     * It specifies an exception handler to log warnings, enables exception throwing,
     * and enforces atomic operations. Additionally, it registers the default
     * PostgreSQL row mapper. The configured QueryConfiguration is then set as the default.
     */
    private void configureDefaultQuery() {
        QueryConfiguration config = QueryConfiguration.builder(dataSource)
                .setExceptionHandler(err -> Main.getMainLogger().warning(err.getMessage()))
                .setThrowExceptions(true)
                .setAtomic(true)
                .setRowMapperRegistry(new RowMapperRegistry().register(PostgresqlMapper.getDefaultMapper()))
                .build();
        QueryConfiguration.setDefault(config);
    }

    /**
     * Sets the connection information for the database.
     *
     * This method updates the connection parameters including the host, port,
     * database name, user credentials, application name, and schema.
     * The provided information will be used to configure the database connection.
     *
     * @param host           The hostname of the database server.
     * @param port           The port number on which the database server is listening.
     * @param database       The name of the database.
     * @param user           The username for database authentication.
     * @param password       The password for database authentication.
     * @param applicationName The name of the application using the database.
     * @param schema         The database schema to be used.
     */
    public void setConnectionInfo(String host, int port, String database, String user, String password, String applicationName, String schema) {
        this.applicationName = applicationName;
        this.schema = schema;
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        dataSource.close();
    }

    /**
     * Returns a HashMap containing all connection information.
     * The keys in the map are as follows:
     * <ul>
     * <li>host: the hostname of the database server</li>
     * <li>port: the port number on which the database server is listening</li>
     * <li>database: the name of the database</li>
     * <li>user: the username for database authentication</li>
     * <li>password: the password for database authentication</li>
     * <li>applicationName: the name of the application using the database</li>
     * <li>schema: the database schema to be used</li>
     * </ul>
     * @return a HashMap containing all connection information.
     */
    public HashMap<String, Object> getConnectionInfo() {
        HashMap<String, Object> info = new HashMap<>();
        info.put("host", host);
        info.put("port", port);
        info.put("database", database);
        info.put("user", user);
        info.put("password", password);
        info.put("applicationName", applicationName);
        info.put("schema", schema);
        return info;
    }
}

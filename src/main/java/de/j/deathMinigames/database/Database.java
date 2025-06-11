package de.j.deathMinigames.database;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.mapper.RowMapperRegistry;
import de.chojo.sadu.postgresql.databases.PostgreSql;
import de.chojo.sadu.postgresql.mapper.PostgresqlMapper;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.j.deathMinigames.main.Config;
import de.j.stationofdoom.main.Main;

import java.util.HashMap;

public class Database {
    private static Database instance;
    private HikariDataSource dataSource;
    private int port;
    private String host;
    private String database;
    private String user;
    private String password;
    private String applicationName;
    private String schema;
    public volatile boolean isConnected = false;

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
        if(checkIfConnectionInfoIsNotEntered()) {
            Main.getMainLogger().info("Database connection information is not completely entered, running without one");
            return;
        }
        setConnectionInfo();
        try {
            dataSource = DataSourceCreator.create(PostgreSql.get()).configure(config -> config.host(host)
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

            Main.getMainLogger().info("Database connected");
            isConnected = true;
            configureDefaultQuery();
            PlayerDataDatabase.getInstance().createTable();
            TeamsDatabase.getInstance().createTable();
            TeamEnderchestsDatabase.getInstance().createTable();
            Main.getMainLogger().info("Database initialized");
        }
        catch(Exception e) {
            Main.getMainLogger().info("No database found, running without one");
        }
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

    private boolean checkIfConnectionInfoIsNotEntered() {
        return Config.getInstance().getDatabaseConfig().containsValue("default");
    }

    private void setConnectionInfo() {
        HashMap<String, String> connectionInfo = Config.getInstance().getDatabaseConfig();
        if(!validateConnectionInfo(connectionInfo)) {
            Main.getMainLogger().warning("Invalid database connection information, running without one");
            return;
        }
        port = Integer.parseInt(connectionInfo.get("port"));
        host = connectionInfo.get("host");
        database = connectionInfo.get("database");
        user = connectionInfo.get("user");
        password = connectionInfo.get("password");
        applicationName = connectionInfo.get("applicationName");
        schema = connectionInfo.get("schema");
    }

    private boolean validateConnectionInfo(HashMap<String, String> connectionInfo) {
        if (connectionInfo == null) {
            return false;
        }
        String[] required = {"port", "host", "database", "user", "password", "applicationName", "schema"};
        for (String key : required) {
            if (!connectionInfo.containsKey(key) || connectionInfo.get(key) == null) {
                return false;
            }
        }
        try {
            Integer.parseInt(connectionInfo.get("port"));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}

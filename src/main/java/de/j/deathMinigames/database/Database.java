package de.j.deathMinigames.database;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.mapper.RowMapperRegistry;
import de.chojo.sadu.postgresql.databases.PostgreSql;
import de.chojo.sadu.postgresql.mapper.PostgresqlMapper;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.j.deathMinigames.main.Config;
import de.j.stationofdoom.main.Main;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.print.Paper;
import java.io.File;
import java.util.HashMap;

public class Database {
    private static Database instance;
    private HikariDataSource dataSource;
    private int port; // 9667
    private String host; //localhost
    private String database; // stationofdoom
    private String user; // mc
    private String password; // 65465
    private String applicationName; // StationOfDoom
    private String schema; // public
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
        if(checkIfConnectionInfoIsEmpty()) {
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

    private boolean checkIfConnectionInfoIsEmpty() {
        return Config.getInstance().getDatabaseConfig().containsValue("default");
    }

    private void setConnectionInfo() {
        HashMap<String, String> connectionInfo = Config.getInstance().getDatabaseConfig();
        port = Integer.parseInt(connectionInfo.get("port"));
        Main.getMainLogger().info("Port set to " + port);
        host = connectionInfo.get("host");
        Main.getMainLogger().info("Host set to " + host);
        database = connectionInfo.get("database");
        Main.getMainLogger().info("Database set to " + database);
        user = connectionInfo.get("user");
        Main.getMainLogger().info("User set to " + user);
        password = connectionInfo.get("password");
        Main.getMainLogger().info("Password set to " + password);
        applicationName = connectionInfo.get("applicationName");
        Main.getMainLogger().info("Application name set to " + applicationName);
        schema = connectionInfo.get("schema");
        Main.getMainLogger().info("Schema set to " + schema);
        Main.getMainLogger().info("Database connection information set");
    }
}

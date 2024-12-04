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
                .withMaximumPoolSize(1)
                .build();

        configureDefaultQuery();
        PlayerDataDatabase.getInstance().createTable();
    }

    private void configureDefaultQuery() {
        QueryConfiguration config = QueryConfiguration.builder(dataSource)
                .setExceptionHandler(err -> Main.getPlugin().getLogger().warning(err.getMessage()))
                .setThrowExceptions(true)
                .setAtomic(true)
                .setRowMapperRegistry(new RowMapperRegistry().register(PostgresqlMapper.getDefaultMapper()))
                .build();
        QueryConfiguration.setDefault(config);
    }

    public void setConnectionInfo(String host, int port, String database, String user, String password, String applicationName, String schema) {
        this.applicationName = applicationName;
        this.schema = schema;
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public void closeConnection() {
        dataSource.close();
    }

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

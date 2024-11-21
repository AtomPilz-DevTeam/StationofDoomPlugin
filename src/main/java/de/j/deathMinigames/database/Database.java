package de.j.deathMinigames.database;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.postgresql.databases.PostgreSql;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.stationofdoom.main.Main;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static net.minecraft.world.level.chunk.storage.RegionFileVersion.configure;

public class Database {
    private static Database instance;
    private static volatile Statement statement;

    public static Statement getStatement() {
        return statement;
    }

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
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        try {
            DataSource dataSource = createDataSource();
            Connection connection = dataSource.getConnection();
            statement = connection.createStatement();
            createTablePlayerData();
            HandlePlayers.initKnownPlayersPlayerData();
            Main.getPlugin().getLogger().info("Successfully connected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            Main.getPlugin().getLogger().warning("Could not connect to the database!");
        }

        HikariDataSource dataSource = DataSourceCreator.create(PostgreSql.get()).configure(config -> config.host("localhost")
                .port(9667)
                .database("stationofdoom")
                .user("mc")
                .password("65465")
                .currentSchema("default")
                .applicationName("StationOfDoom")
        )
                .create()
                .withMaximumPoolSize(3)
                .withMaximumPoolSize(1)
                .build();
    }

    public static DataSource createDataSource() throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL("jdbc:postgresql://localhost:9667/stationofdoom");
        dataSource.setUser("mc");
        dataSource.setPassword("65465");
        return dataSource;
    }
    
    public static void closeStatement() {
        try {
            statement.close();
            Main.getPlugin().getLogger().info("Successfully disconnected from the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            Main.getPlugin().getLogger().warning("Could not disconnect from the database!");
        }
    }

    private void createTablePlayerData() {
        try {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS playerData (id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(50), UUID UUID, status VARCHAR(50), introduction BOOLEAN, usesPlugin BOOLEAN, difficulty INTEGER, decisionTimer INTEGER, " +
                    "hasWonParkourAtleastOnce BOOLEAN, bestParkourTime INTEGER);";
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

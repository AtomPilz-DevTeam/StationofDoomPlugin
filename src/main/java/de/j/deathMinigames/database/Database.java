package de.j.deathMinigames.database;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.core.configuration.DatabaseConfig;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.postgresql.databases.PostgreSql;
import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.queries.api.query.Query;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.main.Main;
import org.postgresql.ds.PGSimpleDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class Database {
    private static Database instance;

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

        //QueryConfiguration. setDefault(QueryConfiguration) //TODO
    }
    
    public static void closeStatement() {
    }
}

package io.github.codeblocteam;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.sql.SqlService;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.*;
import java.util.Optional;

@Plugin(id = "playermanager", name = "Player Manager", version = "0.0.1")
public class PlayerManager {

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    private Logger logger;

    private SqlService sqlConnectionPool = null;

    private DataSource getDataSource(String url) throws SQLException {
        if (sqlConnectionPool == null) {
            sqlConnectionPool = Sponge.getServiceManager()
                    .provide(SqlService.class)
                    .orElseThrow(() -> new SQLException("Cannot find SqlService"));
        }

        return sqlConnectionPool.getDataSource(url);
    }

    private String dbURL() {
        return "jdbc:h2:" + configDir + "/players";
    }

    private void initDB() throws SQLException {
        try (Connection conn = getDataSource(dbURL()).getConnection()) {
            conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS players (name VARCHAR(32) PRIMARY KEY, xp BIGINT)"
            ).execute();
        }
    }

    private Optional<Player> getPlayerFromResultSet(ResultSet res) throws SQLException {
        return res.next()
                ? Optional.of(new Player(res.getString(1), res.getLong(2)))
                : Optional.empty();
    }

    @Listener
    public void onServerStart(GameInitializationEvent event) throws SQLException {
        initDB();
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Login event) throws SQLException {
        save(new Player(event.getProfile().getName().orElseThrow(() -> new SQLException("Connected player has no name")), 0));
        logger.info(getByName("Kiligolo").toString());
    }

    public Optional<Player> getByName(String name) throws SQLException {
        try (Connection conn = getDataSource(dbURL()).getConnection()) {
            ResultSet res = conn.prepareStatement("SELECT name, xp FROM players").executeQuery();
            return getPlayerFromResultSet(res);
        }
    }

    public int save(Player player) throws SQLException {
        try (Connection conn = getDataSource(dbURL()).getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO players (name, xp) VALUES (?, ?)");

            stmt.setString(1, player.getName());
            stmt.setLong(2, player.getXP());
            return stmt.executeUpdate();
        }
    }
}

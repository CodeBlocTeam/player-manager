package io.github.codeblocteam;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SQLPlayerDataService implements PlayerDataService {

    private final String dbURL;

    private SqlService sqlConnectionPool = null;

    private final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS players (" +
            " name VARCHAR(32) PRIMARY KEY," +
            " xp BIGINT," +
            " password CHAR(60)" +
            ")";

    private final String SQL_SELECT_PLAYERS = "SELECT %s FROM players";

    private final String SQL_MERGE_PLAYERS =
            "MERGE INTO players %s VALUES %s";

    @NotNull
    private DataSource getDataSource(String url) throws SQLException {
        if (sqlConnectionPool == null) {
            sqlConnectionPool = Sponge.getServiceManager()
                    .provide(SqlService.class)
                    .orElseThrow(() -> new SQLException("Cannot find SqlService"));
        }

        return sqlConnectionPool.getDataSource(url);
    }

    private void initDB() throws SQLException {
        try (Connection conn = getDataSource(dbURL).getConnection()) {
            conn.prepareStatement(SQL_CREATE_TABLE).execute();
        }
    }

    private Optional<Player> getPlayerFromResultSet(ResultSet res) throws SQLException {
        return res.next()
                ? Optional.of(new Player(res.getString(1), res.getLong(2), res.getString(3)))
                : Optional.empty();
    }

    public SQLPlayerDataService(String dbURL) throws SQLException {
        this.dbURL = dbURL;
        initDB();
    }

    @Override
    public Optional<Player> getByName(String name) throws SQLException {
        try (Connection conn = getDataSource(dbURL).getConnection()) {
            String sql = String.format(SQL_SELECT_PLAYERS, "name, xp, password");
            ResultSet res = conn.prepareStatement(sql).executeQuery();
            return getPlayerFromResultSet(res);
        }
    }

    @Override
    public int save(Player player) throws SQLException {
        try (Connection conn = getDataSource(dbURL).getConnection()) {
            String sql = String.format(SQL_MERGE_PLAYERS, "(name, xp, password)", "(?,?,?)");
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, player.getName());
            stmt.setLong(2, player.getXP());
            stmt.setString(3, player.getPassword());
            return stmt.executeUpdate();
        }
    }
}

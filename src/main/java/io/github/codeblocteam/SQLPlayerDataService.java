package io.github.codeblocteam;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SQLPlayerDataService implements PlayerDataService {

    /** The JDBC URL to the player database. */
    private final String dbURL;

    /** A SFLJ logger. */
    private final Logger logger;

    /** The SqlService provided by Sponge. */
    private SqlService sqlConnectionPool = null;

    /** SQL statement to create the tables. */
    private final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS players (" +
            " name VARCHAR(32) PRIMARY KEY," +
            " xp BIGINT," +
            " password CHAR(60)" +
            ")";

    /** SQL statement pattern to select from the `players` table. */
    private final String SQL_SELECT_PLAYERS =
            "SELECT %s FROM players %s";

    /** SQL statement pattern to update player data. */
    private final String SQL_MERGE_PLAYERS =
            "MERGE INTO players %s VALUES %s";

    /**
     * Retrieve a connection pool to the database.
     * Use try-with-resources with this function to quickly connect to the
     * database:
     *
     *     try (Connection conn = getDataSource(url).getConnection()) {
     *         // Here you can use conn
     *     }
     *
     * @param url The JDBC url to the database.
     * @return A datasource from which you can get a connection to the database.
     * @throws SQLException If access to the database cannot be made, or
     *                      if the SqlService is not found.
     */
    private DataSource getDataSource(String url) throws SQLException {
        if (sqlConnectionPool == null) {
            sqlConnectionPool = Sponge.getServiceManager()
                    .provide(SqlService.class)
                    .orElseThrow(() -> new SQLException("Cannot find SqlService"));
        }

        return sqlConnectionPool.getDataSource(url);
    }

    /**
     * Create the table in the database.
     *
     * @throws SQLException If access to the database is severed.
     */
    private void initDB() throws SQLException {
        try (Connection conn = getDataSource(dbURL).getConnection()) {
            conn.prepareStatement(SQL_CREATE_TABLE).execute();
        }
    }

    /**
     * Retrieve the first player from the result set.
     * If the result set is empty, then an empty optional is returned.
     *
     * The columns must start with the name, the xp and the password of
     * the player, in that order. Otherwise, an exception is raised.
     *
     * @param res The result set.
     * @return The player data in the result set, if any.
     * @throws SQLException If the order of the columns is not respected,
     *                      or if access to the database is severed.
     */
    private Optional<PlayerModel> getPlayerFromResultSet(ResultSet res) throws SQLException {
        return res.next()
                ? Optional.of(new PlayerModel(res.getString(1), res.getLong(2), res.getString(3)))
                : Optional.empty();
    }

    public SQLPlayerDataService(String dbURL, Logger logger) throws SQLException {
        this.dbURL = dbURL;
        this.logger = logger;
        initDB();
    }

    @Override
    public Optional<PlayerModel> getByName(String name) throws SQLException {
        try (Connection conn = getDataSource(dbURL).getConnection()) {
            String sql = String.format(SQL_SELECT_PLAYERS, "name, xp, password", "WHERE name = ?");
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, name);

            ResultSet res = stmt.executeQuery();
            Optional<PlayerModel> player = getPlayerFromResultSet(res);

            logger.info("Fetched player from db: " + player);
            return player;
        }
    }

    @Override
    public int save(PlayerModel playerModel) throws SQLException {
        try (Connection conn = getDataSource(dbURL).getConnection()) {
            String sql = String.format(SQL_MERGE_PLAYERS, "(name, xp, password)", "(?,?,?)");
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, playerModel.getName());
            stmt.setLong(2, playerModel.getXP());
            stmt.setString(3, playerModel.getPassword());

            logger.info("Saving player to db: " + playerModel);
            return stmt.executeUpdate();
        }
    }
}

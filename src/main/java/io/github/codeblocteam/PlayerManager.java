package io.github.codeblocteam;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import javax.inject.Inject;
import java.nio.file.Path;
import java.sql.SQLException;

@Plugin(id = "playermanager", name = "Player Manager", version = "0.0.1")
public class PlayerManager {

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    private Logger logger;

    /**
     * Initialization: Starts the service.
     *
     * @param event Not used.
     * @throws SQLException If access to the SQL database fails.
     */
    @Listener
    public void onServerStart(GameInitializationEvent event) throws SQLException {
        String dbURL = "jdbc:h2:" + configDir + "/players";
        Sponge.getServiceManager().setProvider(
                this,
                PlayerDataService.class,
                new SQLPlayerDataService(dbURL, logger)
        );
    }
}

package io.github.codeblocteam;

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

    @Listener
    public void onServerStart(GameInitializationEvent event) throws SQLException {
        String dbURL = "jdbc:h2:" + configDir + "/players";
        Sponge.getServiceManager().setProvider(
                this,
                PlayerDataService.class,
                new SQLPlayerDataService(dbURL)
        );
    }
}

package io.github.codeblocteam;

import java.util.Optional;

/**
 * Provide access to custom player data.
 */
public interface PlayerDataService {
    /**
     * Fetch data related to the given player name.
     * If there is no data about the name, then the Optional is empty.
     *
     * @param name The name of the player
     * @return The data about the player name
     * @throws Exception When the data could not be fetched.
     */
    Optional<Player> getByName(String name) throws Exception;

    /**
     * Save player data.
     *
     * @param player The data to save.
     * @return The number of entries saved (1 if saved, 0 if not).
     * @throws Exception When the data could not be saved.
     */
    int save(Player player) throws Exception;
}

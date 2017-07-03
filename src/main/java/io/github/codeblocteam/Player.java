package io.github.codeblocteam;

import java.math.BigDecimal;

public class Player {

    /** The name (pseudo) of the player */
    private String name;

    /** The amount of XP the player has */
    private BigDecimal xp;

    public Player(String name, BigDecimal xp) {
        setName(name);
        setXP(xp);
    }

    /**
     * Getter for the name.
     * @return The name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name.
     * @param name The new name of the player.
     */
    public void setName(String name) {
        this.name = (name == null ? "" : name);
    }

    /**
     * Getter for the XP.
     * @return The amount of XP of the player.
     */
    public BigDecimal getXP() {
        return xp;
    }

    /**
     * Setter for the XP.
     * @param xp The new amount of XP of the player.
     */
    public void setXP(BigDecimal xp) {
        this.xp = (xp == null ? BigDecimal.ZERO : xp);
    }

}

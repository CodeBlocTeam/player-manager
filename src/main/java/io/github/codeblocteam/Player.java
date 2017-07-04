package io.github.codeblocteam;

import java.math.BigDecimal;

public class Player {

    /** The name (pseudo) of the player */
    private String name;

    /** The amount of XP the player has */
    private long xp;

    /** The password of the player. */
    private String password;

    public Player(String name, long xp, String password) {
        setName(name);
        setXP(xp);
        setPassword(password);
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
    public long getXP() {
        return xp;
    }

    /**
     * Setter for the XP.
     * @param xp The new amount of XP of the player.
     */
    public void setXP(long xp) {
        this.xp = xp;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = (password == null ? "" : password);
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", xp=" + xp +
                '}';
    }

}

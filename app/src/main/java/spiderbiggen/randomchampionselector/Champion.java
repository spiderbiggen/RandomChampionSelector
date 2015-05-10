package spiderbiggen.randomchampionselector;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Class that defines the champion object;
 *
 * Created by Stefan on 10-5-2015.
 */
public class Champion implements Serializable{

    public final String NAME;
    public final String ROLE;

    public final int HEALTH;
    public final int RESOURCE;
    public final String RESOURCE_TYPE;

    public final int RANGE;
    public final int MOVEMENT_SPEED;
    public Drawable IMAGE;


    /**
     * Default constructor for object (only for test purposes).
     */
    public Champion() {
        this("Name", "Fighter", 2000, 1000, "Mana", 75, 340, null);
    }

    /**
     * Constructor for {@link Champion} assigns values to each resource.
     *
     * @param name Name of the champion.
     * @param role Primary role of the champion.
     * @param hp Base health at max level.
     * @param resource Base amount of the resource at max level (can be 0 if no resType).
     * @param resource_type The resource type for this champion eg mana, energy, fury; can be none if this champion has no resource.
     * @param range The base auto attack range at max level.
     * @param movespeed The base movement speed of this champion.
     * @param image The drawable to use for this champion.
     */
    public Champion(String name, String role, int hp, int resource, String resource_type, int range, int movespeed, Drawable image) {
        NAME = name;
        ROLE = role;
        HEALTH = hp;
        RESOURCE = resource;
        RESOURCE_TYPE = resource_type;
        RANGE = range;
        MOVEMENT_SPEED = movespeed;
        IMAGE = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Champion)) return false;

        Champion champion = (Champion) o;

        return NAME.equals(champion.NAME) && ROLE.equals(champion.ROLE);

    }

    @Override
    public int hashCode() {
        int result = NAME.hashCode();
        result = 31 * result + ROLE.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Champion{" +
                "NAME='" + NAME + '\'' +
                ", PRIMARY_ROLE='" + ROLE + '\'' +
                ", HEALTH=" + HEALTH +
                ", RESOURCE_TYPE='" + RESOURCE_TYPE + '\'' +
                ", RESOURCE=" + RESOURCE +
                '}';
    }

    public boolean is(String type) {
        return type.equals(this.ROLE);
    }
}

package spiderbiggen.randomchampionselector;

import java.io.Serializable;

/**
 * Class that defines the champion object;
 * <p/>
 * Created by Stefan on 10-5-2015.
 */
public class Champion implements Serializable {

    private String name;
    private String role;
    private int health;
    private int resource;
    private String resourceType;
    private int range;
    private int movementSpeed;

    /**
     * Default constructor for object (only for test purposes).
     */
    public Champion() {
        this("Aatrox...", "Fighter", 2000, 1000, "Mana", 75, 340);
    }

    /**
     * Constructor for {@link Champion} assigns values to each resource.
     *
     * @param name          Name of the champion.
     * @param role          Primary role of the champion.
     * @param health        Base health at max level.
     * @param resource      Base amount of the resource at max level (can be 0 if no resType).
     * @param resourceType  The resource type for this champion eg mana, energy, fury; can be null if this champion has no resource.
     * @param range         The base auto attack Range at max level.
     * @param movementSpeed The base movement speed of this champion.
     */
    public Champion(String name, String role, int health, int resource, String resourceType, int range, int movementSpeed) {
        this.name = name;
        this.role = role;
        this.health = health;
        this.resource = resource;
        this.resourceType = resourceType;
        this.range = range;
        this.movementSpeed = movementSpeed;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public int getHealth() {
        return health;
    }

    public int getResource() {
        return resource;
    }

    public String getResourceType() {
        return resourceType;
    }

    public int getRange() {
        return range;
    }

    public int getMovementSpeed() {
        return movementSpeed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Champion)) return false;
        Champion champion = (Champion) o;
        return this.getName().equals(champion.getName()) && role.equals(champion.getRole());
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + role.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Champion{" +
                "Name='" + this.getName() + '\'' +
                ", PRIMARY_Role='" + this.getRole() + '\'' +
                ", Health=" + this.getHealth() +
                ", ResourceType='" + this.getResourceType() + '\'' +
                ", Resource=" + this.getResource() +
                '}';
    }

    public boolean is(String type) {
        return type.equals(this.role);
    }
}

package spiderbiggen.randomchampionselector.champion;

import java.io.Serializable;

/**
 * Class that defines the champion object;
 * <p/>
 * Created by Stefan on 10-5-2015.
 */
public class Champion implements Serializable {

    private String name;
    private ChampionRole role;
    private int health;
    private int resource;
    private ChampionResource resourceType;
    private ChampionAttackType attackType;

    /**
     * Default constructor for object (only for test purposes).
     */
    public Champion() {
        this("Aatrox...", ChampionRole.FIGHTER, 2000, 1000, ChampionResource.MANA, ChampionAttackType.MELEE);
    }

    /**
     * Constructor for {@link Champion} assigns values to each resource.
     *
     * @param name         Name of the champion.
     * @param role         Primary role of the champion.
     * @param health       Base health at max level.
     * @param resource     Base amount of the resource at max level (can be 0 if no resType).
     * @param resourceType The resource type for this champion eg mana, energy, fury; can be null if this champion has no resource.
     * @param attackType   The auto attack type.
     */
    public Champion(String name, ChampionRole role, int health, int resource, ChampionResource resourceType, ChampionAttackType attackType) {
        this.name = name;
        this.role = role;
        this.health = health;
        this.resource = resource;
        this.resourceType = resourceType;
        this.attackType = attackType;
    }

    public String getName() {
        return name;
    }

    public ChampionRole getRole() {
        return role;
    }

    public int getHealth() {
        return health;
    }

    public int getResource() {
        return resource;
    }

    public ChampionResource getResourceType() {
        return resourceType;
    }

    public ChampionAttackType getAttackType() {
        return attackType;
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
        return type.equals(this.role.toString());
    }
}

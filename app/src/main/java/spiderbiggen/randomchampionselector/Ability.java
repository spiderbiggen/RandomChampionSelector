package spiderbiggen.randomchampionselector;

/**
 * Created by Stefan on 22-5-2015.
 */
public class Ability {
    private String type;
    private String name;
    private String cost;
    private int cooldown;
    private String description;

    public Ability(String type, String name, String cost, int cooldown, String description) {
        this.type = type;
        this.name = name;
        this.cost = cost;
        this.cooldown = cooldown;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getCost() {
        return cost;
    }

    public int getCooldown() {
        return cooldown;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ability ability = (Ability) o;

        return getName().equals(ability.getName()) &&
                !(getCost() != null ? !getCost().equals(ability.getCost()) : ability.getCost() != null) &&
                !(getDescription() != null ? !getDescription().equals(ability.getDescription()) : ability.getDescription() != null);

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + (getCost() != null ? getCost().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }
}

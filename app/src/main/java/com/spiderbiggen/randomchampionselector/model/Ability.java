package com.spiderbiggen.randomchampionselector.model;

/**
 * Created by Stefan on 22-5-2015.
 */
public class Ability {
    private String name;
    private String cost;
    private String cooldown;
    private String description;

    /**
     * Default constructor.
     *
     * @param name        the name of the ability
     * @param cost        the ability cost
     * @param cooldown    the cool down
     * @param description the description
     */
    public Ability(String name, String cost, String cooldown, String description) {
        this.name = name;
        this.cost = cost;
        this.cooldown = cooldown;
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public String getCost() {
        return cost;
    }

    public String getCooldown() {
        return cooldown;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Ability ability = (Ability) o;

        return getName().equals(ability.getName()) &&
            !(getCost() != null
                ? !getCost().equals(ability.getCost())
                : ability.getCost() != null) &&
            !(getDescription() != null
                ? !getDescription().equals(ability.getDescription())
                : ability.getDescription() != null);

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + (getCost() != null ? getCost().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }
}

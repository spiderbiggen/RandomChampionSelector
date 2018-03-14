package com.spiderbiggen.randomchampionselector.model;

/**
 *
 * @author Stefan Breetveld
 */
public enum Lane {
    DUO_SUPPORT("Support"),
    JUNGLE("Jungle"),
    TOP("Top"),
    MIDDLE("Mid"),
    DUO_CARRY("Bot");

    private String name;

    Lane(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

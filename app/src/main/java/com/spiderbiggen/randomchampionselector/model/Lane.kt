package com.spiderbiggen.randomchampionselector.model

/**
 * In game role description.
 *
 * @author Stefan Breetveld
 */
enum class Lane constructor(val description: String) {
    DUO_SUPPORT("Support"),
    JUNGLE("Jungle"),
    TOP("Top"),
    MIDDLE("Mid"),
    DUO_CARRY("Bot");

    override fun toString(): String {
        return description
    }
}

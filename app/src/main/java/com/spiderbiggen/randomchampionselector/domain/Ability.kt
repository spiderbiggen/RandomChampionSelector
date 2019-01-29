package com.spiderbiggen.randomchampionselector.domain

/**
 *
 * @author Stefan Breetveld
 *
 * @param name        the name of the ability
 * @param cost        the ability cost
 * @param cooldown    the cool down
 * @param description the description
 */
data class Ability(val name: String, val cost: String?, val cooldown: String, val description: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || other !is Ability) {
            return false
        }

        val ability = other as? Ability

        return name == ability!!.name &&
                cost == ability.cost &&
                description == ability.description

    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (cost?.hashCode() ?: 0)
        result = 31 * result + (description.hashCode())
        return result
    }
}

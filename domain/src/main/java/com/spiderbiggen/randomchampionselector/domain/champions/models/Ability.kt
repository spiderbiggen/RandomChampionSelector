package com.spiderbiggen.randomchampionselector.domain.champions.models

/**
 *
 * @author Stefan Breetveld
 *
 * @param name        the name of the ability
 * @param cost        the ability cost
 * @param cooldown    the cool down
 * @param description the description
 */
data class Ability(val name: String, val cost: String?, val cooldown: String, val description: String)

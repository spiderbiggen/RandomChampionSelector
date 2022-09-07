package com.spiderbiggen.randomchampionselector.data.ddragon.models

import java.io.Serializable

/**
 *
 * @author Stefan Breetveld
 *
 * @param name        the name of the ability
 * @param cost        the ability cost
 * @param cooldown    the cool down
 * @param description the description
 */
data class ApiAbility(val name: String, val cost: String?, val cooldown: String, val description: String): Serializable

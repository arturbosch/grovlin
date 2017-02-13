package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.parser.parseFromResource

/**
 * @author Artur Bosch
 */
fun parseFromTestResource(resourceName: String) = parseFromResource(resourceName).toAsT()
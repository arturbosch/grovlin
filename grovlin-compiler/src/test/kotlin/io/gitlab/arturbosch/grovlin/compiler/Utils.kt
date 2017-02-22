package io.gitlab.arturbosch.grovlin.compiler

import io.gitlab.arturbosch.grovlin.ast.resolution.resolveSymbols
import io.gitlab.arturbosch.grovlin.ast.resolution.resolveTypes
import io.gitlab.arturbosch.grovlin.ast.toAsT
import io.gitlab.arturbosch.grovlin.parser.parseFromResource

/**
 * @author Artur Bosch
 */

fun parseFromTestResource(resourceName: String) = parseFromResource(resourceName).toAsT(
		resourceName.substring(0, resourceName.lastIndexOf("."))).apply {
	resolveSymbols()
	resolveTypes()
}
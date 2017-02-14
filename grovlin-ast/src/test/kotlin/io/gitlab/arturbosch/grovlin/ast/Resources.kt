package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.resolution.resolveSymbols
import io.gitlab.arturbosch.grovlin.ast.resolution.resolveTypes
import io.gitlab.arturbosch.grovlin.parser.parse
import io.gitlab.arturbosch.grovlin.parser.parseFromResource

/**
 * @author Artur Bosch
 */
fun parseFromTestResource(resourceName: String) = parseFromResource(resourceName).toAsT()

fun String.asGrovlinFile() = parse().root!!.toAsT()

fun GrovlinFile.resolved() = apply { resolveSymbols(); resolveTypes() }
package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.visitors.asGrovlinFile
import io.gitlab.arturbosch.grovlin.parser.parse
import io.gitlab.arturbosch.grovlin.parser.parseFromResource

/**
 * @author Artur Bosch
 */
fun parseFromTestResource(resourceName: String) = parseFromResource(resourceName).asGrovlinFile()

fun String.asGrovlinFile() = parse().root.asGrovlinFile()

fun GrovlinFile.resolved() = identify().resolve().apply {
	errors.forEach { println(it) }
}

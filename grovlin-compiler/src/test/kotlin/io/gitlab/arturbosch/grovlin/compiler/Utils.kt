package io.gitlab.arturbosch.grovlin.compiler

import io.gitlab.arturbosch.grovlin.ast.identify
import io.gitlab.arturbosch.grovlin.ast.resolve
import io.gitlab.arturbosch.grovlin.ast.visitors.asGrovlinFile
import io.gitlab.arturbosch.grovlin.compiler.backend.asJavaFile
import io.gitlab.arturbosch.grovlin.compiler.java.interpret
import io.gitlab.arturbosch.grovlin.parser.parse
import io.gitlab.arturbosch.grovlin.parser.parseFromResource

/**
 * @author Artur Bosch
 */

fun parseFromTestResource(resourceName: String) = parseFromResource(resourceName)
		.asGrovlinFile(resourceName.substring(0, resourceName.lastIndexOf(".")))
		.identify()
		.resolve()
		.apply { errors.forEach(::println) }

fun String.asGrovlinFile() = parse().root.asGrovlinFile()
		.identify()
		.resolve()
		.apply { errors.forEach(::println) }

fun runFromResource(resourceName: String) = parseFromTestResource(resourceName)
		.asJavaFile()
		.apply { all().forEach { println(it.unit) } }
		.interpret()

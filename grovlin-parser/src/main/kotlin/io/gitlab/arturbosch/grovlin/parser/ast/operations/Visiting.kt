package io.gitlab.arturbosch.grovlin.parser.ast.operations

import io.gitlab.arturbosch.grovlin.parser.ast.Node
import kotlin.reflect.memberProperties

fun Node.process(operation: (Node) -> Unit) {
	operation(this)
	this.javaClass.kotlin.memberProperties.forEach { property ->
		val value = property.get(this)
		when (value) {
			is Node -> value.process(operation)
			is Collection<*> -> value.forEach { if (it is Node) it.process(operation) }
		}
	}
}

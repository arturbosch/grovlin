package io.gitlab.arturbosch.grovlin.ast.operations

import io.gitlab.arturbosch.grovlin.ast.Node
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

inline fun <reified T : Node> Node.processNodesOfType(crossinline operation: (T) -> Unit) = process {
	if (it is T) operation(it)
}

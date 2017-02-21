package io.gitlab.arturbosch.grovlin.ast.operations

import io.gitlab.arturbosch.grovlin.ast.Node
import kotlin.reflect.memberProperties

fun Node.process(operation: (Node) -> Unit) {
	operation(this)
	javaClass.kotlin.memberProperties.forEach { property ->
		val value = property.get(this)
		value.processIfPropertyIsFromTypeNode(operation)
	}
}

private fun Any?.processIfPropertyIsFromTypeNode(operation: (Node) -> Unit) {
	when (this) {
		is Node -> process(operation)
		is Collection<*> -> this.forEach { if (it is Node) it.process(operation) }
	}
}

inline fun <reified T : Node> Node.processNodesOfType(crossinline operation: (T) -> Unit) = process {
	if (it is T) operation(it)
}

inline fun <reified T : Node> Node.collectByType(): List<T> {
	val list = mutableListOf<T>()
	process { if (it is T) list.add(it) }
	return list
}
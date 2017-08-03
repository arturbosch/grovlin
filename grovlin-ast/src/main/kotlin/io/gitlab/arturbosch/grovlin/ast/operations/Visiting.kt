package io.gitlab.arturbosch.grovlin.ast.operations

import io.gitlab.arturbosch.grovlin.ast.AstNode
import kotlin.reflect.memberProperties

fun AstNode.process(operation: (AstNode) -> Unit) {
	operation(this)
	javaClass.kotlin.memberProperties.forEach { property ->
		val value = property.get(this)
		value.processIfPropertyIsFromTypeNode(operation)
	}
}

private fun Any?.processIfPropertyIsFromTypeNode(operation: (AstNode) -> Unit) {
	when (this) {
		is AstNode -> process(operation)
		is Collection<*> -> this.forEach { (it as? AstNode)?.process(operation) }
	}
}

inline fun <reified T : AstNode> AstNode.processNodesOfType(crossinline operation: (T) -> Unit) = process {
	if (it is T) operation(it)
}

inline fun <reified T : AstNode> AstNode.collectByType(): List<T> {
	val list = mutableListOf<T>()
	process { if (it is T) list.add(it) }
	return list
}

inline fun <reified T : AstNode> AstNode.findByType(): T? {
	var result: T? = null
	process {
		if (it is T) {
			result = it
			return@process
		}
	}
	return result
}

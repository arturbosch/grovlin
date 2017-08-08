package io.gitlab.arturbosch.grovlin.ast.operations

import io.gitlab.arturbosch.grovlin.ast.AstNode

fun AstNode.process(operation: (AstNode) -> Unit) {
	operation(this)
	children.forEach { it.process(operation) }
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

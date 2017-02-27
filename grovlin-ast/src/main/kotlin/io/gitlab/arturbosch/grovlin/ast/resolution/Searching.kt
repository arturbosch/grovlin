@file:Suppress("UNCHECKED_CAST")

package io.gitlab.arturbosch.grovlin.ast.resolution

import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.Node
import io.gitlab.arturbosch.grovlin.ast.NodeWithStatements
import io.gitlab.arturbosch.grovlin.ast.Statement
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.isBefore
import io.gitlab.arturbosch.grovlin.ast.operations.processNodesOfType
import java.util.IdentityHashMap
import kotlin.reflect.memberProperties

/**
 * @author Artur Bosch
 */

fun Node.childParentMap(): Map<Node, Node> {
	val map = IdentityHashMap<Node, Node>()
	processRelations { child, parent -> if (parent != null) map[child] = parent }
	return map
}

fun Node.processRelations(parent: Node? = null, operation: (Node, Node?) -> Unit) {
	operation(this, parent)
	this.javaClass.kotlin.memberProperties.forEach { property ->
		val value = property.get(this)
		when (value) {
			is Node -> value.processRelations(this, operation)
			is Collection<*> -> value.forEach { if (it is Node) it.processRelations(this, operation) }
		}
	}
}

fun <T : Node> Node.ancestor(klass: Class<T>, childParentMap: Map<Node, Node>): T? {
	if (childParentMap.containsKey(this)) {
		val parent = childParentMap[this]
		if (klass.isInstance(parent)) {
			return parent as T
		}
		return parent?.ancestor(klass, childParentMap)
	}
	return null
}

fun GrovlinFile.resolveSymbols() {
	val childParentMap = this.childParentMap()

	processNodesOfType<VarReference> {
		val statementContainingRefernce = it.ancestor(Statement::class.java, childParentMap)
		var currentNode = it.ancestor(NodeWithStatements::class.java, childParentMap)
		do {
			val valueDeclarations = currentNode?.statements?.preceding(statementContainingRefernce)
					?.filterIsInstance<VarDeclaration>() ?: emptyList()
			it.reference.tryToResolve(valueDeclarations.reversed())
			if (it.reference.isResolved()) {
				return@processNodesOfType
			}
			currentNode = currentNode?.ancestor(NodeWithStatements::class.java, childParentMap)
		} while (it.reference.source == null && currentNode != null)
//		if (it.reference.source == null) {
//			val declaration = it.ancestor(MemberDeclaration::class.java, childParentMap)
//			val valueDeclarations = statements.preceding(statement).filterIsInstance<VarDeclaration>()
//			it.reference.tryToResolve(valueDeclarations.reversed())
//		}
	}

	processNodesOfType<Assignment> {
		val varDeclarations = statements.preceding(it).filterIsInstance<VarDeclaration>()
		it.reference.tryToResolve(varDeclarations.reversed())
	}
}

private fun List<Statement>.preceding(node: Node?): List<Node> {
	if (node == null) emptyList<Statement>()
	return filter { it.isBefore(node!!) }
}

package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

interface Named {
	val name: String
}


interface AstNode {
	var position: Position?
	var parent: Node?
}

abstract class Node : AstNode {
	override var position: Position? = null
	override var parent: Node? = null
}

interface NodeWithName : AstNode, Named

interface NodeWithStatements : AstNode {
	val statements: MutableList<Statement>
}

interface NodeWithType : AstNode {
	var type: Type
	fun isUnsolved() = type is UnknownType
}

interface NodeWithBlock : AstNode {
	val block: BlockStatement?
}

abstract class Expression : Node()

abstract class Statement : Node()

interface VariableDeclaration : NodeWithType, Named

interface MemberDeclaration : AstNode

interface TopLevelDeclarable {
	fun isTopLevelDeclaration(): Boolean = true
}

interface NodeWithReference<N : Named> : AstNode {
	val reference: Reference<N>
}

data class Reference<N : Named>(val name: String, var source: N? = null) {
	override fun toString(): String {
		if (source == null) {
			return "Ref($name)[Unsolved]"
		} else {
			return "Ref($name)[Solved]"
		}
	}

	fun tryToResolve(candidates: List<N>): Boolean {
		val res = candidates.find { it.name == this.name }
		source = res
		return res != null
	}

	fun isResolved() = source != null
}

package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

interface Named {
	val name: String
}

interface Node {
	val position: Position?
}

interface NodeWithName : Node, Named

interface NodeWithStatements : Node {
	val statements: MutableList<Statement>
}

interface NodeWithMemberDeclarations : Node {
	val declarations: MutableList<MemberDeclaration>
}

interface NodeWithType : Node {
	var type: Type
	fun isUnsolved() = type is UnknownType
}

interface NodeWithBlock : Node {
	val block: BlockStatement?
}

interface Expression : Node

interface Statement : Node

interface MemberDeclaration : Statement

interface TopLevelDeclarable {
	fun isTopLevelDeclaration(): Boolean = true
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

}

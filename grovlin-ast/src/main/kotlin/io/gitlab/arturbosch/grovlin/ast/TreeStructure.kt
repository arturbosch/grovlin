package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

interface Named {
	val name: String
}


interface AstNode {
	var position: Position?
	var parent: AstNode?
	var children: List<AstNode>
	var typeReference: Type?
}

abstract class Node : AstNode {
	override var position: Position? = null
	override var parent: AstNode? = null
	override var children: List<AstNode> = emptyList()
	override var typeReference: Type? = null
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

	fun findTypeByName(name: String): TypeDeclaration? = block?.statements
			?.filterIsInstance<TypeDeclaration>()
			?.find { it.name == name }

	fun findObjectByName(name: String): ObjectDeclaration? = block?.statements
			?.filterIsInstance<ObjectDeclaration>()
			?.find { it.name == name }

	fun findMethodByName(name: String): MethodDeclaration? = block?.statements
			?.filterIsInstance<MethodDeclaration>()
			?.find { it.name == name }

	fun findPropertyByName(name: String): PropertyDeclaration? = block?.statements
			?.filterIsInstance<PropertyDeclaration>()
			?.find { it.name == name }

	fun findVariableByName(name: String): VarDeclaration? = block?.statements
			?.filterIsInstance<VarDeclaration>()
			?.find { it.name == name }

	fun topLevelStatements(): List<Statement> = block?.statements
			?.filter { it is TopLevelDeclarable && it.isTopLevelDeclaration() } ?: emptyList()

	fun statements(): List<Statement> = block?.statements ?: emptyList()
}

abstract class Expression : Node()

abstract class Statement : Node()

interface VariableDeclaration : NodeWithType, Named

abstract class MemberDeclaration : Statement()

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

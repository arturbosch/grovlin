package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import io.gitlab.arturbosch.grovlin.ast.symbols.Scope
import io.gitlab.arturbosch.grovlin.ast.symbols.Symbol

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
	var symbol: Symbol?
	var resolutionScope: Scope?
	var evaluationType: Type?
}

abstract class Node : AstNode {
	override var position: Position? = null
	override var parent: AstNode? = null
	override var children: List<AstNode> = emptyList()
	override var symbol: Symbol? = null
	override var resolutionScope: Scope? = null
	override var evaluationType: Type? = null
}

abstract class Expression : Node()

abstract class Statement : Node()

interface Declaration : AstNode, NodeWithType, NodeWithName

interface TopLevelDeclarable {
	fun isTopLevelDeclaration(): Boolean = true
}

abstract class MemberDeclaration : Statement(), Declaration

interface VariableDeclaration : Declaration, NodeWithType, Named

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

	fun findVariableReferencesByName(name: String): List<VarReference> = block
			?.collectByType<VarReference>()
			?.filter { it.reference.name == name } ?: emptyList()

	fun topLevelStatements(): List<Statement> = block?.statements
			?.filter { it is TopLevelDeclarable && it.isTopLevelDeclaration() } ?: emptyList()

	fun statements(): List<Statement> = block?.statements ?: emptyList()
}

interface NodeWithReference<N : Named> : AstNode {
	val reference: Reference<N>
}

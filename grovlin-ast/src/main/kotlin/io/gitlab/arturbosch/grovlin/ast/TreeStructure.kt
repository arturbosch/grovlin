package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import io.gitlab.arturbosch.grovlin.ast.operations.findParentByType
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
	var promotionType: Type?
	fun getDeclaredFile() = findParentByType<GrovlinFile>()
}

abstract class Node : AstNode {
	override var position: Position? = null
	override var parent: AstNode? = null
	override var children: List<AstNode> = emptyList()
	override var symbol: Symbol? = null
	override var resolutionScope: Scope? = null
	override var evaluationType: Type? = null
	override var promotionType: Type? = null
}

abstract class Expression : Node()

abstract class Statement : Node()

interface Declaration : AstNode, NodeWithType, NodeWithName

interface TopLevelDeclarableNode : AstNode {
	val isTopLevelDeclaration get(): Boolean = parent?.parent is GrovlinFile // GrovlinFile>Block>This
	override fun getDeclaredFile() =
			if (isTopLevelDeclaration) parent?.parent as GrovlinFile
			else super.getDeclaredFile()
}

abstract class MemberDeclaration : Statement(), Declaration

interface VariableDeclaration : Declaration, NodeWithType, Named

interface NodeWithName : AstNode, Named

interface NodeWithOverride : AstNode {
	var hasOverride: Boolean
}

interface NodeWithStatements : AstNode {
	val statements: MutableList<Statement>
}

interface NodeWithType : AstNode {
	var type: Type
	fun isUnsolved(): Boolean = type == UnknownType
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
			?.filter { it.reference == name } ?: emptyList()

	fun topLevelStatements(): List<Statement> = block?.statements
			?.filter { it is TopLevelDeclarableNode && it.isTopLevelDeclaration } ?: emptyList()

	fun statements(): List<Statement> = block?.statements ?: emptyList()
}

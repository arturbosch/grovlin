package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

data class TypeDeclaration(val type: ObjectOrTypeType,
						   val extendedTypes: MutableList<ObjectOrTypeType>,
						   override val declarations: MutableList<MemberDeclaration>,
						   override val position: Position? = null) : Statement, Named, NodeWithMemberDeclarations, TopLevelDeclarable {
	override val name: String = type.name
}

data class ObjectDeclaration(val type: ObjectOrTypeType,
							 val extendedObject: ObjectOrTypeType?,
							 val extendedTypes: MutableList<ObjectOrTypeType>,
							 override val declarations: MutableList<MemberDeclaration>,
							 override val position: Position? = null) : Statement, Named, NodeWithMemberDeclarations, TopLevelDeclarable {
	override val name: String = type.name
}

data class MethodDeclaration(override val name: String,
							 override val block: BlockStatement?,
							 override val position: Position? = null) : MemberDeclaration, NodeWithBlock, NodeWithName, TopLevelDeclarable {
	fun mustBeOverriden() = block == null
}

data class LambdaDeclaration(override val name: String,
							 override val statements: MutableList<Statement>,
							 override val position: Position? = null) : Statement, NodeWithStatements, NodeWithName, TopLevelDeclarable

data class PropertyDeclaration(override var type: Type,
							   override val name: String,
							   val value: Expression?,
							   override val position: Position? = null) : MemberDeclaration, VariableDeclaration

data class VarDeclaration(override val name: String,
						  val value: Expression,
						  override val position: Position? = null) : VariableDeclaration {
	override var type: Type = UnknownType
}

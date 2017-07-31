package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

class TypeDeclaration(val type: ObjectOrTypeType,
					  val extendedTypes: MutableList<ObjectOrTypeType>,
					  override val block: BlockStatement?)
	: Statement(), NodeWithBlock, Named, TopLevelDeclarable {

	override val name: String = type.name
}

class ObjectDeclaration(val type: ObjectOrTypeType,
						val extendedObject: ObjectOrTypeType?,
						val extendedTypes: MutableList<ObjectOrTypeType>,
						override val block: BlockStatement?)
	: Statement(), NodeWithBlock, Named, TopLevelDeclarable {

	override val name: String = type.name
}

class MethodDeclaration(override val name: String,
						override val block: BlockStatement?)
	: Statement(), MemberDeclaration, NodeWithBlock, NodeWithName, TopLevelDeclarable {

	fun mustBeOverridden() = block == null
}

class LambdaDeclaration(override val name: String,
						override val block: BlockStatement)
	: Statement(), NodeWithBlock, NodeWithName, TopLevelDeclarable

class PropertyDeclaration(override var type: Type,
						  override val name: String,
						  val value: Expression?)
	: Statement(), MemberDeclaration, VariableDeclaration

class VarDeclaration(override val name: String,
					 val value: Expression)
	: Statement(), VariableDeclaration {

	override var type: Type = UnknownType
}

package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

class TypeDeclaration(val type: ObjectOrTypeType,
					  val extendedTypes: MutableList<ObjectOrTypeType>,
					  override val block: BlockStatement?)
	: MemberDeclaration(), NodeWithBlock, Named, TopLevelDeclarable {

	override val name: String = type.name
}

class ObjectDeclaration(val type: ObjectOrTypeType,
						val extendedObject: ObjectOrTypeType?,
						val extendedTypes: MutableList<ObjectOrTypeType>,
						override val block: BlockStatement?)
	: MemberDeclaration(), NodeWithBlock, Named, TopLevelDeclarable {

	override val name: String = type.name
}

class MethodDeclaration(override val name: String,
						override val block: BlockStatement?,
						override var type: Type = VoidType,
						val parameters: MutableList<ParameterDeclaration> = mutableListOf())
	: MemberDeclaration(), NodeWithType, NodeWithBlock, NodeWithName, TopLevelDeclarable {

	fun mustBeOverridden() = block == null
}

class LambdaDeclaration(override val name: String,
						override val block: BlockStatement)
	: MemberDeclaration(), NodeWithBlock, NodeWithName, TopLevelDeclarable

class PropertyDeclaration(override var type: Type,
						  override val name: String,
						  val value: Expression?)
	: MemberDeclaration(), VariableDeclaration

class VarDeclaration(override val name: String,
					 val value: Expression?,
					 val isVal: Boolean = false)
	: MemberDeclaration(), VariableDeclaration {

	override var type: Type = UnknownType
}

class ParameterDeclaration(override val name: String,
						   override var type: Type)
	: Declaration(), NodeWithName, NodeWithType

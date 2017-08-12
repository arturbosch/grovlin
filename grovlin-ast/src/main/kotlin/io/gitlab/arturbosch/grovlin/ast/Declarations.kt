package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

class TypeDeclaration(val typeType: ObjectOrTypeType,
					  val extendedTypes: MutableList<ObjectOrTypeType>,
					  override val block: BlockStatement?)
	: MemberDeclaration(), NodeWithBlock, TopLevelDeclarable {

	override val name: String = typeType.name
	override var type: Type = typeType
}

class ObjectDeclaration(val objectType: ObjectOrTypeType,
						val extendedObject: ObjectOrTypeType?,
						val extendedTypes: MutableList<ObjectOrTypeType>,
						override val block: BlockStatement?)
	: MemberDeclaration(), NodeWithBlock, TopLevelDeclarable {

	override val name: String = objectType.name
	override var type: Type = objectType
}

open class MethodDeclaration(override val name: String,
							 override val block: BlockStatement?,
							 override var type: Type = VoidType,
							 val parameters: MutableList<ParameterDeclaration> = mutableListOf())
	: MemberDeclaration(), NodeWithBlock, TopLevelDeclarable {

	fun mustBeOverridden() = block == null
	val parameterSignature get() = "$name(${parameters.joinToString(", ") { it.parameterSignature }})"
	val signature get() = "$name(${parameters.joinToString(", ") { it.parameterSignature }})" +
			if (type != VoidType) ": $type" else ""
}

class LambdaDeclaration(override val name: String,
						override val block: BlockStatement)
	: MemberDeclaration(), NodeWithBlock, TopLevelDeclarable {

	override var type: Type = UnknownType
}

class PropertyDeclaration(override var type: Type,
						  override val name: String,
						  val value: Expression?)
	: MemberDeclaration(), VariableDeclaration

class VarDeclaration(override val name: String,
					 var value: Expression?,
					 val isVal: Boolean = false)
	: MemberDeclaration(), VariableDeclaration {

	override var type: Type = UnknownType
}

class ParameterDeclaration(override val name: String,
						   override var type: Type)
	: Statement(), VariableDeclaration {

	val parameterSignature get() = "$type $name"
}

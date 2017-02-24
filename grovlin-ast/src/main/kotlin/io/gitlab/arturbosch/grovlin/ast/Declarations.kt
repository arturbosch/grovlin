package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

data class TypeDeclaration(val type: ObjectOrTypeType,
						   val extendedTypes: MutableList<ObjectOrTypeType>,
						   override val statements: MutableList<Statement>,
						   override val position: Position? = null) : Statement, Named, NodeWithStatements, TopLevelDeclarable {
	override val name: String = type.name
}

data class ObjectDeclaration(val type: ObjectOrTypeType,
							 val extendedObject: ObjectOrTypeType,
							 val extendedTypes: MutableList<ObjectOrTypeType>,
							 override val statements: MutableList<Statement>,
							 override val position: Position? = null) : Statement, Named, NodeWithStatements, TopLevelDeclarable {
	override val name: String = type.name
}

data class MethodDeclaration(override val name: String,
							 override val statements: MutableList<Statement>,
							 override val position: Position? = null) : Statement, NodeWithStatements, NodeWithName, TopLevelDeclarable

data class LambdaDeclaration(override val name: String,
							 override val statements: MutableList<Statement>,
							 override val position: Position? = null) : Statement, NodeWithStatements, NodeWithName, TopLevelDeclarable

data class PropertyDeclaration(override val name: String,
							   val value: Expression,
							   override val position: Position? = null) : Statement, NodeWithName

data class VarDeclaration(override val name: String,
						  val value: Expression,
						  override val position: Position? = null) : Statement, NodeWithName, NodeWithType {
	override var type: Type = UnknownType
}

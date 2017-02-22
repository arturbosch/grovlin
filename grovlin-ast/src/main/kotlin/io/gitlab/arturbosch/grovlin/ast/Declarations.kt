package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

data class TypeDeclaration(override val name: String,
						   override val statements: MutableList<Statement>,
						   override val position: Position? = null) : Statement, NodeWithStatements, NodeWithName, TopLevelDeclarable

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

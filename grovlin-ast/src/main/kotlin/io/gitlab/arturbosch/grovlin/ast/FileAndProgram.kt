package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

data class GrovlinFile(override val name: String,
					   override val statements: MutableList<Statement>,
					   override val position: Position? = null) : NodeWithStatements, NodeWithName

data class Program(override val name: String,
				   override val statements: MutableList<Statement>,
				   override val position: Position? = null) : Statement, NodeWithStatements, TopLevelDeclarable, NodeWithName

fun GrovlinFile.findTypeByName(name: String): TypeDeclaration? = statements.filterIsInstance<TypeDeclaration>().find { it.name == name }
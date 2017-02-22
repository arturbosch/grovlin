package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

interface ConditionalStatement : Statement {
	val condition: Expression
	val thenStatement: BlockStatement
}

data class IfStatement(override val condition: Expression,
					   override val thenStatement: BlockStatement,
					   val elifs: MutableList<ElifStatement>,
					   val elseStatement: BlockStatement?,
					   override val position: Position?) : ConditionalStatement

data class ElifStatement(override val condition: Expression,
						 override val thenStatement: BlockStatement,
						 override val position: Position?) : ConditionalStatement

data class BlockStatement(override val statements: MutableList<Statement>,
						  override val position: Position?) : Statement, NodeWithStatements

data class ExpressionStatement(val expression: Expression, override val position: Position?) : Statement

data class Assignment(val reference: Reference<VarDeclaration>, val value: Expression, override val position: Position? = null) : Statement

data class Print(val value: Expression, override val position: Position? = null) : Statement

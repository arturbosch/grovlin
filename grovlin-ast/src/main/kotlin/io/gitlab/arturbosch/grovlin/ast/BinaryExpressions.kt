package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

interface BinaryExpression : Expression {
	val left: Expression
	val right: Expression
}

interface RelationExpression : BinaryExpression

data class SumExpression(override val left: Expression,
						 override val right: Expression,
						 override val position: Position? = null) : BinaryExpression

data class SubtractionExpression(override val left: Expression,
								 override val right: Expression,
								 override val position: Position? = null) : BinaryExpression

data class MultiplicationExpression(override val left: Expression,
									override val right: Expression,
									override val position: Position? = null) : BinaryExpression

data class DivisionExpression(override val left: Expression,
							  override val right: Expression,
							  override val position: Position? = null) : BinaryExpression

data class AndExpression(override val left: Expression,
						 override val right: Expression,
						 override val position: Position? = null) : BinaryExpression

data class OrExpression(override val left: Expression,
						override val right: Expression,
						override val position: Position? = null) : BinaryExpression

data class XorExpression(override val left: Expression,
						 override val right: Expression,
						 override val position: Position? = null) : BinaryExpression

data class EqualExpression(override val left: Expression,
						   override val right: Expression,
						   override val position: Position? = null) : RelationExpression

data class UnequalExpression(override val left: Expression,
							 override val right: Expression,
							 override val position: Position? = null) : RelationExpression

data class LessEqualExpression(override val left: Expression,
							   override val right: Expression,
							   override val position: Position? = null) : RelationExpression

data class LessExpression(override val left: Expression,
						  override val right: Expression,
						  override val position: Position? = null) : RelationExpression

data class GreaterExpression(override val left: Expression,
							 override val right: Expression,
							 override val position: Position? = null) : RelationExpression

data class GreaterEqualExpression(override val left: Expression,
								  override val right: Expression,
								  override val position: Position? = null) : RelationExpression
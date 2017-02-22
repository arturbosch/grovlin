package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

data class MinusExpression(val value: Expression, override val position: Position? = null) : Expression

data class NotExpression(val value: Expression, override val position: Position? = null) : Expression

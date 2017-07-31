package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

abstract class UnaryExpression(val value: Expression) : Expression()

class MinusExpression(value: Expression) : UnaryExpression(value)

class NotExpression(value: Expression) : UnaryExpression(value)

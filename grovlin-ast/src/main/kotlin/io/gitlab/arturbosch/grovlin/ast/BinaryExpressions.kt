package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

abstract class BinaryExpression(val left: Expression,
								val right: Expression) : Expression()

abstract class RelationExpression(left: Expression,
								  right: Expression)
	: BinaryExpression(left, right)

class SumExpression(left: Expression,
					right: Expression)
	: BinaryExpression(left, right)

class SubtractionExpression(left: Expression,
							right: Expression)
	: BinaryExpression(left, right)

class MultiplicationExpression(left: Expression,
							   right: Expression)
	: BinaryExpression(left, right)

class DivisionExpression(left: Expression,
						 right: Expression)
	: BinaryExpression(left, right)

class AndExpression(left: Expression,
					right: Expression)
	: BinaryExpression(left, right)

class OrExpression(left: Expression,
				   right: Expression)
	: BinaryExpression(left, right)

class XorExpression(left: Expression,
					right: Expression)
	: BinaryExpression(left, right)

class EqualExpression(left: Expression,
					  right: Expression)
	: RelationExpression(left, right)

class UnequalExpression(left: Expression,
						right: Expression)
	: RelationExpression(left, right)

class LessEqualExpression(left: Expression,
						  right: Expression)
	: RelationExpression(left, right)

class LessExpression(left: Expression,
					 right: Expression)
	: RelationExpression(left, right)

class GreaterExpression(left: Expression,
						right: Expression)
	: RelationExpression(left, right)

class GreaterEqualExpression(left: Expression,
							 right: Expression)
	: RelationExpression(left, right)

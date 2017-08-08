package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

class ParenExpression(val expression: Expression) : Expression()

open class CallExpression(val scope: Expression?,
						  override val name: String,
						  val arguments: List<Expression> = emptyList()) : Expression(), NodeWithName

class GetterAccessExpression(val scope: Expression?,
							 override val name: String) : Expression(), NodeWithName

class SetterAccessExpression(val scope: Expression?,
							 override val name: String,
							 val expression: Expression) : Expression(), NodeWithName

class ThisReference(val reference: String) : Expression() {

	companion object {
		fun instance() = ThisReference("this")
	}
}

class TypeConversion(val value: Expression,
					 val targetType: Type) : Expression()

class VarReference(val reference: String) : Expression() {

	val varName get() = reference
}

class ObjectCreation(val type: ObjectOrTypeType) : Expression()

class IntRangeExpression(val start: IntLit,
						 val endExclusive: IntLit) : Expression()

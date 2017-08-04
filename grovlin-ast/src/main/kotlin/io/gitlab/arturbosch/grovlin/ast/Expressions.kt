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

class ThisReference(val reference: Reference<TypeDeclaration>) : Expression() {

	companion object {
		fun instance() = ThisReference(Reference("this"))
	}
}

class TypeConversion(val value: Expression,
					 val targetType: Type) : Expression()

class VarReference(override val reference: Reference<VariableDeclaration>)
	: Expression(), NodeWithReference<VariableDeclaration>

class ObjectCreation(val type: ObjectOrTypeType) : Expression()

class IntRangeExpression(val start: IntLit,
						 val endExclusive: IntLit) : Expression()

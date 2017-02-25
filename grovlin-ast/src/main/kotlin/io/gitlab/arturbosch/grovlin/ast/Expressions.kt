package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

data class ParenExpression(val expression: Expression, override val position: Position?) : Expression

data class CallExpression(val scope: Expression?, override val name: String, override val position: Position?) : Expression, NodeWithName

data class ThisReference(val reference: Reference<TypeDeclaration>, override val position: Position?) : Expression

data class TypeConversion(val value: Expression, val targetType: Type, override val position: Position? = null) : Expression

data class VarReference(val reference: Reference<VarDeclaration>, override val position: Position? = null) : Expression

data class ObjectCreation(val type: ObjectOrTypeType, override val position: Position?) : Expression
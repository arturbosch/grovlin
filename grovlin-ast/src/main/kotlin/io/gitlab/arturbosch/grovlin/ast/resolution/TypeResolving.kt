package io.gitlab.arturbosch.grovlin.ast.resolution

import io.gitlab.arturbosch.grovlin.ast.BinaryExpression
import io.gitlab.arturbosch.grovlin.ast.BoolLit
import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.DecLit
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.Expression
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.IntLit
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.MinusExpression
import io.gitlab.arturbosch.grovlin.ast.Node
import io.gitlab.arturbosch.grovlin.ast.NodeWithType
import io.gitlab.arturbosch.grovlin.ast.NotExpression
import io.gitlab.arturbosch.grovlin.ast.NumberType
import io.gitlab.arturbosch.grovlin.ast.ObjectCreation
import io.gitlab.arturbosch.grovlin.ast.ParenExpression
import io.gitlab.arturbosch.grovlin.ast.Position
import io.gitlab.arturbosch.grovlin.ast.PrimitiveType
import io.gitlab.arturbosch.grovlin.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.ast.RelationExpression
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.ast.TypeConversion
import io.gitlab.arturbosch.grovlin.ast.UnknownType
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.operations.processNodesOfType
import io.gitlab.arturbosch.grovlin.ast.validation.SemanticError

/**
 * @author Artur Bosch
 */

fun GrovlinFile.resolveTypes(): List<SemanticError> {
	val errors = mutableListOf<SemanticError>()
	resolveSymbols()
	processNodesOfType<NodeWithType> {
		if (it.isUnsolved()) {
			try {
				it.tryToSolve()
			} catch (ex: TypeMissMatchError) {
				errors.add(SemanticError(ex.message!!, it.position!!.start))
			} catch (ex: RelationsNotResolvedError) {
				errors.add(SemanticError(ex.message!!, it.position!!.start))
			}
		}
	}
	return errors
}

private fun NodeWithType.tryToSolve(): Type {
	if (!isUnsolved()) return type
	when (this) {
		is VarDeclaration -> type = value?.resolveType() ?: UnknownType
		is PropertyDeclaration -> type
		else -> throw UnsupportedOperationException("")
	}
	return type
}

private fun Expression.resolveType(): Type = when (this) {
	is ParenExpression -> expression.resolveType()
	is BinaryExpression -> resolveBinaryExpression()
	is TypeConversion -> targetType
	is MinusExpression -> value.resolveType()
	is NotExpression -> value.resolveType()
	is VarReference -> safeDeclaration().tryToSolve()
	is ObjectCreation -> type
	is IntLit -> type
	is DecLit -> type
	is BoolLit -> type
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

private fun BinaryExpression.resolveBinaryExpression(): Type {
	val leftType = left.resolveType()
	val rightType = right.resolveType()
	val type = if (this is RelationExpression) {
		resolveRelationTypes(leftType, rightType)
	} else if (rightType.name == leftType.name) {
		rightType
	} else if (rightType is PrimitiveType && leftType is PrimitiveType) {
		resolvePrimitiveType(leftType, rightType)
	} else {
		null
	}
	return type ?: throw TypeMissMatchError(leftType, rightType, position)
}

private fun resolveRelationTypes(leftType: Type, rightType: Type): Type? = when {
	leftType is NumberType && rightType is NumberType -> BoolType
	else -> null
}

private fun resolvePrimitiveType(leftType: Type, rightType: Type): Type? = when {
	leftType is IntType && rightType is DecimalType -> rightType
	leftType is DecimalType && rightType is IntType -> leftType
	else -> null
}

private fun VarReference.safeDeclaration() = this.reference.source ?: throw RelationsNotResolvedError(this)

class TypeMissMatchError(leftType: Type, rightType: Type, point: Position?) :
		IllegalStateException("Type mismatch at ${point?.start} - left=${leftType.name} and right=${rightType.name}")

class RelationsNotResolvedError(node: Node) :
		IllegalStateException("References not resolved for ${node.javaClass.simpleName} on ${node.position?.start}")

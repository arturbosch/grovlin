package io.gitlab.arturbosch.grovlin.compiler.backend.builtins

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.ObjectCreationExpr
import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.EqualExpression
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.ObjectOrTypeType
import io.gitlab.arturbosch.grovlin.ast.PrimitiveType
import io.gitlab.arturbosch.grovlin.compiler.backend.toJava

/**
 * @author Artur Bosch
 */
fun EqualExpression.builtinToJava(): Expression {

	fun equalsCall(leftExpr: Expression = left.toJava(), rightExpr: Expression = right.toJava()) =
			MethodCallExpr(leftExpr, "equals", NodeList.nodeList(rightExpr))

	val leftType = left.evaluationType
	val rightType = right.evaluationType
	return when {
		leftType is PrimitiveType && rightType is PrimitiveType ->
			BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.EQUALS)
		leftType is ObjectOrTypeType && rightType is ObjectOrTypeType -> equalsCall()
		leftType is PrimitiveType && rightType is ObjectOrTypeType ->
			equalsCall(leftExpr = getObjectForm(leftType, left))
		leftType is ObjectOrTypeType && rightType is PrimitiveType ->
			equalsCall(rightExpr = getObjectForm(rightType, right))
		else -> equalsCall()
	}
}

private fun getObjectForm(primitiveType: PrimitiveType,
						  value: io.gitlab.arturbosch.grovlin.ast.Expression): ObjectCreationExpr {
	val type = when (primitiveType) {
		IntType -> JavaParser.parseClassOrInterfaceType("Integer")
		BoolType -> JavaParser.parseClassOrInterfaceType("Boolean")
		DecimalType -> JavaParser.parseClassOrInterfaceType("Double")
		else -> throw UnsupportedOperationException()
	}
	val objForm = ObjectCreationExpr(null, type, NodeList.nodeList(value.toJava()))
	return objForm
}

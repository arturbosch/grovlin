package io.gitlab.arturbosch.grovlin.compiler.backend.builtins

import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_TO_STRING_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.ToString
import io.gitlab.arturbosch.grovlin.compiler.backend.toJava

/**
 * @author Artur Bosch
 */
fun ToString.builtinToJava(): MethodCallExpr {
	val scopeType = scope?.evaluationType
	val scopeExpr = scope?.toJava() ?: throw IllegalStateException("Builtin ToString expects a scope!")

	return when (scopeType) {
		IntType -> MethodCallExpr(NameExpr("Integer"), BUILTIN_TO_STRING_NAME, NodeList.nodeList(scopeExpr))
		BoolType -> MethodCallExpr(NameExpr("Boolean"), BUILTIN_TO_STRING_NAME, NodeList.nodeList(scopeExpr))
		DecimalType -> MethodCallExpr(NameExpr("Double"), BUILTIN_TO_STRING_NAME, NodeList.nodeList(scopeExpr))
		else -> MethodCallExpr(scopeExpr, BUILTIN_TO_STRING_NAME)
	}
}

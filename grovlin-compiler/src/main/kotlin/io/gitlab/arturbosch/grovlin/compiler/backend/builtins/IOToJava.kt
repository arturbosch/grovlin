package io.gitlab.arturbosch.grovlin.compiler.backend.builtins

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.expr.SimpleName
import io.gitlab.arturbosch.grovlin.ast.builtins.Print
import io.gitlab.arturbosch.grovlin.ast.builtins.PrintLn
import io.gitlab.arturbosch.grovlin.ast.builtins.RandomNumber
import io.gitlab.arturbosch.grovlin.compiler.backend.toJava

/**
 * @author Artur Bosch
 */
fun PrintLn.builtinToJava(): MethodCallExpr {
	val fieldAccess = FieldAccessExpr(NameExpr("System"), "out")
	val arguments =
			if (arguments.isEmpty()) NodeList()
			else NodeList.nodeList(arguments[0].toJava())
	return MethodCallExpr(fieldAccess, SimpleName("println"), arguments)
}

fun Print.builtinToJava(): MethodCallExpr {
	val fieldAccess = FieldAccessExpr(NameExpr("System"), "out")
	val arguments = NodeList.nodeList(arguments[0].toJava())
	return MethodCallExpr(fieldAccess, SimpleName("print"), arguments)
}

fun readlineToJava() = MethodCallExpr(
		MethodCallExpr(NameExpr("System"), SimpleName("console"), NodeList()),
		"readLine",
		NodeList())

fun RandomNumber.builtinToJava() = MethodCallExpr(ObjectCreationExpr(null,
		JavaParser.parseClassOrInterfaceType("java.util.Random"),
		NodeList()), "nextInt", NodeList.nodeList(arguments[0].toJava()))

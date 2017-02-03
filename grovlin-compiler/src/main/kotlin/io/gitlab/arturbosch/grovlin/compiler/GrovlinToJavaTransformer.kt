package io.gitlab.arturbosch.grovlin.compiler

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.DoubleLiteralExpr
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.IntegerLiteralExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.type.ArrayType
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.PrimitiveType
import io.gitlab.arturbosch.grovlin.parser.ast.DecLit
import io.gitlab.arturbosch.grovlin.parser.ast.DivisionExpression
import io.gitlab.arturbosch.grovlin.parser.ast.Expression
import io.gitlab.arturbosch.grovlin.parser.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.parser.ast.IntLit
import io.gitlab.arturbosch.grovlin.parser.ast.MultiplicationExpression
import io.gitlab.arturbosch.grovlin.parser.ast.Print
import io.gitlab.arturbosch.grovlin.parser.ast.Program
import io.gitlab.arturbosch.grovlin.parser.ast.Statement
import io.gitlab.arturbosch.grovlin.parser.ast.SubtractionExpression
import io.gitlab.arturbosch.grovlin.parser.ast.SumExpression
import io.gitlab.arturbosch.grovlin.parser.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.parser.ast.VarReference
import com.github.javaparser.ast.expr.Expression as JavaParserExpression
import com.github.javaparser.ast.stmt.Statement as JavaParserStatement

/**
 * @author Artur Bosch
 */

fun GrovlinFile.toJava(): CompilationUnit {
	val unit = CompilationUnit()

	val program = (statements.find { it is Program } ?: throw IllegalStateException("No program statement found!")) as Program

	val clazz = ClassOrInterfaceDeclaration()
	clazz.setName("ProgramGrovlin")
	clazz.addModifier(Modifier.PUBLIC)
	unit.addType(clazz)

	val main = clazz.addMethod("main", Modifier.PUBLIC, Modifier.STATIC)
	main.addParameter(ArrayType(ClassOrInterfaceType("String")), "args")
	val statements = program.statements.mapTo(NodeList<JavaParserStatement>()) { it.toJava() }
	main.setBody(BlockStmt(statements))

	return unit
}

private fun Statement.toJava(): JavaParserStatement = when (this) {
	is VarDeclaration -> ExpressionStmt(VariableDeclarationExpr(VariableDeclarator(PrimitiveType.intType(), name, value.toJava())))
	is Print -> ExpressionStmt(MethodCallExpr(FieldAccessExpr(NameExpr("System"), "out"),
			SimpleName("println"), NodeList<JavaParserExpression>().apply { add(this@toJava.value.toJava()) }))
	else -> throw UnsupportedOperationException("not implemented")
}

private fun Expression.toJava(): JavaParserExpression = when (this) {
	is SumExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.PLUS)
	is SubtractionExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.MINUS)
	is MultiplicationExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.MULTIPLY)
	is DivisionExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.DIVIDE)
	is IntLit -> IntegerLiteralExpr(value)
	is DecLit -> DoubleLiteralExpr(value)
	is VarReference -> NameExpr(name)
	else -> throw UnsupportedOperationException("not implemented")
}
